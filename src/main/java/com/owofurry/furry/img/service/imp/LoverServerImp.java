package com.owofurry.furry.img.service.imp;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.util.concurrent.Striped;
import com.owofurry.furry.img.entity.FileLover;
import com.owofurry.furry.img.mapper.FileLoverMapper;
import com.owofurry.furry.img.service.LoverService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
@Slf4j
public class LoverServerImp implements LoverService {

    private final Striped<Lock> lock;
    RedisTemplate<String, Object> redisTemplate;
    FileLoverMapper fileLoverMapper;

    public LoverServerImp(Striped<Lock> lock, RedisTemplate<String, Object> redisTemplate,
                          FileLoverMapper fileLoverMapper) {
        this.lock = lock;
        this.redisTemplate = redisTemplate;
        this.fileLoverMapper = fileLoverMapper;
    }

    @PostConstruct
    private void init() {
        // 读取数据库喜欢数据到redis的 hyperloglog 方便后续去重
        log.info("清空原有 FileLover 的 redis-hyperloglog 数据");
        Cursor<String> scan = redisTemplate.scan(ScanOptions.scanOptions()
                .match("file_lover:hyperloglog:*").count(10).build());
        while (scan.hasNext()) {
            redisTemplate.delete(scan.next());
        }
        scan.close();
        // 读取数据库喜欢数据到redis的 hyperloglog 方便后续去重
        log.info("开始初始化 FileLover 的 redis-hyperloglog 数据");
        int page = 1;
        int pageSize = 100;
        Page<FileLover> iPage = new Page<>(page, pageSize);
        List<FileLover> fileLovers;
        long count = 0L;
        do {
            fileLovers = fileLoverMapper.selectPage(iPage, null).getRecords();
            // 更新页数据
            page++;
            iPage = new Page<>(page, pageSize);
            if (!fileLovers.isEmpty()) {
                count += fileLovers.size();
                // 写入redis，转换格式为： fileId : ...userId
                for (FileLover lover : fileLovers) {
                    redisTemplate.opsForHyperLogLog().add("file_lover:hyperloglog:" + lover.getFileId(), lover.getUserId());
                }
            }
        } while (!fileLovers.isEmpty() && fileLovers.size() < pageSize);
        log.info("FileLover 的 redis-hyperloglog 数据初始化完成, 共 {} 条", count);
    }

    @Override
    public List<FileLover> findLiverByUser(Integer userId) {
        Object cache = redisTemplate.opsForValue().get("file_lover:userId:" + userId);
        if (cache != null) {
            return assentListCache(cache);
        } else {
            QueryWrapper<FileLover> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            List<FileLover> fileLovers = fileLoverMapper.selectList(wrapper);
            redisTemplate.opsForValue().set("file_lover:userId:" + userId, fileLovers, 3, TimeUnit.MINUTES);
            return fileLovers;
        }
    }

    @Override
    public List<FileLover> findLoverByFile(Long fileId) {
        Object cache = redisTemplate.opsForValue().get("file_lover:fileId:" + fileId);
        if (cache != null) {
            return assentListCache(cache);
        } else {
            QueryWrapper<FileLover> wrapper = new QueryWrapper<>();
            wrapper.eq("file_id", fileId);
            List<FileLover> fileLovers = fileLoverMapper.selectList(wrapper);
            redisTemplate.opsForValue().set("file_lover:fileId:" + fileId, fileLovers, 3, TimeUnit.MINUTES);
            return fileLovers;
        }
    }

    @Override
    public boolean delete(Long fileId, Integer userId) {
        // 对于同一条记录进行上锁。防止高并发下产生 MYSQL 死锁
        Lock l = lock.get(fileId + ":" + userId);
        try {
            l.lock();
            if (exist(fileId, userId)) {
                QueryWrapper<FileLover> wrapper = new QueryWrapper<>();
                wrapper.eq("user_id", userId).eq("file_id", fileId);
                int i = fileLoverMapper.delete(wrapper);
                if (i > 0) {
                    redisTemplate.opsForValue().set("file_lover:fileId:" + fileId + ":userId:" + userId,
                            false, 3, TimeUnit.MINUTES);
                    clearCache(fileId, userId);
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("删除 FileLover 失败", e);
        } finally {
            l.unlock();
        }
        return false;
    }

    @Override
    @Transactional
    public boolean delete(Long fileId) {
        QueryWrapper<FileLover> wrapper = new QueryWrapper<>();
        wrapper.eq("file_id", fileId);
        int t = fileLoverMapper.delete(wrapper);
        clearAllCache(fileId, null);
        return t > 0;
    }

    @Override
    public boolean save(Long fileId, Integer userId) {
        // 对于同一条记录进行上锁。防止高并发下产生 MYSQL 死锁
        Lock l = lock.get(fileId + ":" + userId);
        if (!exist(fileId, userId)) {
            try {
                l.lock();
                FileLover fileLover = new FileLover();
                fileLover.setFileId(fileId);
                fileLover.setUserId(userId);
                int i = fileLoverMapper.insert(fileLover);
                if (i > 0) {
                    redisTemplate.opsForValue().set("file_lover:fileId:" + fileId + ":userId:" + userId,
                            true, 3, TimeUnit.MINUTES);
                    redisTemplate.opsForHyperLogLog().add("file_lover:hyperloglog:" + fileId, userId);
                    clearCache(fileId, userId);
                    return true;
                }
            } catch (Exception e) {
                log.error("添加 FileLover 失败", e);
            } finally {
                l.unlock();
            }
        }
        return false;
    }

    @Override
    public boolean exist(Long fileId, Integer userId) {
        // 先查询缓存
        Boolean cache = (Boolean) redisTemplate.opsForValue().get("file_lover:fileId:" + fileId + ":userId:" + userId);
        if (cache != null) {
            return cache;
        } else {
            if (Boolean.TRUE.equals(redisTemplate.hasKey("file_lover:hyperloglog:" + fileId))) {
                // 复制原有 hyperloglog 到一个临时的 key进行 add 操作，防止add操作后对下次判断数据产生干扰
                String tempId = IdUtil.simpleUUID();
                redisTemplate.copy("file_lover:hyperloglog:" + fileId,
                        "file_lover:hyperloglog:" + fileId + ":" + tempId,
                        false);
                //  查询 hyperloglog
                Long add = redisTemplate.opsForHyperLogLog().add("file_lover:hyperloglog:" + fileId + ":" + tempId, userId);
                redisTemplate.delete("file_lover:hyperloglog:" + fileId + ":" + tempId);
                if (add == 1) {
                    // 添加成功，说明之前没有添加过，也就是说数据库中没有对应记录
                    redisTemplate.opsForValue().set("file_lover:fileId:" + fileId + ":userId:" + userId,
                            false, 3, TimeUnit.MINUTES);
                    return false;
                }
            } else {
                reloadHyperLogLog(fileId);
            }
            // 开始回写缓存
            Lock l = lock.get(fileId + ":" + userId);
            try {
                l.lock();
                cache = (Boolean) redisTemplate.opsForValue().get("file_lover:fileId:" + fileId + ":userId:" + userId);
                if (cache != null) {
                    return cache;
                } else {
                    // 查询数据库
                    QueryWrapper<FileLover> wrapper = new QueryWrapper<>();
                    wrapper.eq("file_id", fileId).eq("user_id", userId);
                    List<FileLover> fileLovers = fileLoverMapper.selectList(wrapper);
                    FileLover fileLover = null;
                    // 处理数据异常
                    if (fileLovers.size() > 2) {
                        log.error("FileLover 数据异常，fileId: {}, userId: {}", fileId, userId);
                        fileLover = fileLovers.getFirst();
                    } else if (!fileLovers.isEmpty()) {
                        fileLover = fileLovers.getFirst();
                    }
                    redisTemplate.opsForValue().set("file_lover:fileId:" + fileId + ":userId:" + userId,
                            fileLover != null, 3, TimeUnit.MINUTES);
                    return fileLover != null;
                }
            } finally {
                l.unlock();
            }
        }
    }


    /**
     * 断言缓存数据
     *
     * @param cache 缓存
     * @return List<FileLover>
     */
    private List<FileLover> assentListCache(Object cache) {
        assert cache instanceof List<?>;
        List<?> cacheList = (List<?>) cache;
        if (cacheList.isEmpty()) {
            return null;
        } else {
            return cacheList.stream().map(e -> (FileLover) e).toList();
        }
    }

    /**
     * 重新加载 HyperLogLog
     *
     * @param fileId 文件id
     */
    private void reloadHyperLogLog(Long fileId) {
        // 线程池异步执行
        ThreadUtil.execute(() -> {
            log.info("重载 file_lover:hyperloglog:{}", fileId);
            Long count = 0L;
            redisTemplate.delete("file_lover:hyperloglog:" + fileId);
            List<FileLover> fileLovers = findLoverByFile(fileId);
            if (fileLovers != null && !fileLovers.isEmpty()) {
                // 写入redis，转换格式为： fileId : ...userId
                count += fileLovers.size();
                for (FileLover lover : fileLovers) {
                    redisTemplate.opsForHyperLogLog().add("file_lover:hyperloglog:" + lover.getFileId(), lover.getUserId());
                }
            }
            log.info("重载 file_lover:hyperloglog:{}，重载数量：{}", fileId, count);
        });
    }

    private void clearCache(Long fileId, Integer userId) {
        redisTemplate.delete("file_lover:fileId:" + fileId);
        redisTemplate.delete("file_lover:userId:" + userId);
    }

    private void clearAllCache(Long fileId, Integer userId) {
        clearCache(fileId, userId);
        redisTemplate.delete("file_lover:hyperloglog:" + fileId);
        redisTemplate.delete("file_lover:fileId:" + fileId + ":userId:" + userId);
    }

}
