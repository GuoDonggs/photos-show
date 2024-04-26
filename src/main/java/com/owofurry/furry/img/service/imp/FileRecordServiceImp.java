package com.owofurry.furry.img.service.imp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.owofurry.furry.img.aop.FileRecordFilter;
import com.owofurry.furry.img.config.PathConfiguration;
import com.owofurry.furry.img.elasticsearch.KeywordImageDoc;
import com.owofurry.furry.img.elasticsearch.KeywordImageRepository;
import com.owofurry.furry.img.entity.FileRecord;
import com.owofurry.furry.img.exception.UserOperationException;
import com.owofurry.furry.img.mapper.FileRecordMapper;
import com.owofurry.furry.img.service.FileRecordService;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.utils.UserUtil;
import com.owofurry.furry.img.vo.R;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FileRecordServiceImp implements FileRecordService {

    RedisTemplate<String, Object> redisTemplate;

    FileRecordMapper recordMapper;
    PathConfiguration pathConfiguration;
    KeywordImageRepository imageRepository;
    ElasticsearchOperations elasticsearchOperations;
    ExecutorService executorService;

    public FileRecordServiceImp(RedisTemplate<String, Object> redisTemplate,
                                FileRecordMapper recordMapper,
                                PathConfiguration pathConfiguration,
                                KeywordImageRepository imageRepository,
                                ElasticsearchOperations elasticsearchOperations,
                                ExecutorService virtualThreadExecutor) {
        this.redisTemplate = redisTemplate;
        this.recordMapper = recordMapper;
        this.pathConfiguration = pathConfiguration;
        this.imageRepository = imageRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.executorService = virtualThreadExecutor;
    }

    @PostConstruct
    public void init() {
        int pageSize = 100;
        int page = 1;
        log.info("开始初始化图片搜索关键字");
        Page<FileRecord> iPage = new Page<>(page, pageSize);
        List<FileRecord> records = recordMapper.selectPage(iPage, null).getRecords();
        List<KeywordImageDoc> docs;
        imageRepository.deleteAll();
        while (!records.isEmpty()) {
            docs = records.stream().map(KeywordImageDoc::new).toList();
            imageRepository.saveAll(docs);
            page++;
            iPage = new Page<>(page, pageSize);
            records = recordMapper.selectPage(iPage, null).getRecords();
        }
        log.info("图片搜索关键字初始化完成, 总数: {}", imageRepository.count());
    }

    @Override
    @FileRecordFilter
    @Cacheable(cacheNames = "file_record:hot", key = "#size")
    public R hot(int size) {
        QueryWrapper<FileRecord> wrapper = new QueryWrapper<>();
        wrapper.orderBy(true, false, "lover_num");
        Page<FileRecord> iPage = new Page<>(1, size);
        return RUtil.ok(recordMapper.selectPage(iPage, wrapper).getRecords());
    }

    @Override
    @FileRecordFilter
    @Cacheable(cacheNames = "file_record:list", key = "#page + ':' + #size")
    public R listFile(int page, int size) {
        QueryWrapper<FileRecord> wrapper = new QueryWrapper<>();
        wrapper.orderBy(true, false, "upload_date");
        Page<FileRecord> iPage = new Page<>(page, size);
        return RUtil.ok(recordMapper.selectPage(iPage, wrapper).getRecords());
    }

    @Override
    public FileRecord findByFileId(Long fileId) {
        FileRecord r = (FileRecord) redisTemplate.opsForValue().get("file_record:fileId:" + fileId);
        if (r == null) {
            r = recordMapper.selectById(fileId);
            redisTemplate
                    .opsForValue()
                    .set("file_record:fileId:" + fileId, Objects.requireNonNullElseGet(r, FileRecord::new));
            return r;
        } else {
            return r.getFileId() == null ? null : r;
        }
    }

    @Override
    @Cacheable(cacheNames = "file_record:user", key = " #user_id + ':' + #page + ':' + #size")
    public R listByUser(int user_id, int page, int size) {
        QueryWrapper<FileRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("upload_user", user_id);
        wrapper.orderBy(true, false, "upload_date");
        Page<FileRecord> iPage = new Page<>(page, size);
        return RUtil.ok(recordMapper.selectPage(iPage, wrapper).getRecords());
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "file_record:hot", allEntries = true)
    public void update(FileRecord record) {
        FileRecord r = recordMapper.selectForUpdate(record.getFileId());
        if (r == null) {
            throw new UserOperationException("文件不存在");
        }
        recordMapper.updateById(record);
        imageRepository.deleteById(r.getFileId());
        imageRepository.save(new KeywordImageDoc(record));
        redisTemplate.opsForValue().set("file_record:fileId:" + record.getFileId(), record);
    }

    @Override
    @CacheEvict(cacheNames = {"file_record:search", "file_record:user",
            "file_record:hot", "file_record:list"},
            allEntries = true)
    public void addRecord(FileRecord record) {
        recordMapper.insert(record);
        ThreadUtil.execute(() -> {
            QueryWrapper<FileRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("file_path", record.getFilePath());
            FileRecord fileRecord = recordMapper.selectOne(wrapper);
            assert fileRecord != null;
            imageRepository.save(new KeywordImageDoc(fileRecord));
            log.info("新增图片: {}", record);
        });
    }

    @Override
    @CacheEvict(cacheNames = {"file_record:search",
            "file_record:user",
            "file_record:hot", "file_record:list"}, allEntries = true)
    public void deleteRecord(Integer userId, Long... fileId) {
        for (Long id : fileId) {
            FileRecord byFileId = findByFileId(id);
            if (byFileId == null) {
                continue;
            }
            if (byFileId.getUploadUser().equals(userId) || UserUtil.isAdmin()) {
                recordMapper.deleteById(id);
                File f = new File(pathConfiguration.getBase() + File.separator + byFileId.getFilePath());
                deleteFile(f);
                redisTemplate.delete("file_record:fileId:" + id);
                imageRepository.deleteById(id);
            } else {
                throw new UserOperationException("无法删除他人图片");
            }
        }
        cacheEvict();
    }

    @Override
    @FileRecordFilter
    @Cacheable(cacheNames = "file_record:search", key = "#keyword + ':' + #page + ':' + #size")
    public R search(String keyword, int page, int size) {
        if (page - 1 >= 0) {
            page--;
        }
        return RUtil.ok(imageRepository.findKeywordImageByAllStrLikeIgnoreCase(keyword, PageRequest.of(page, size)));
    }

    @Override
    public void incrLoverNum(Long fileId) {
        recordMapper.incrLoverNum(fileId);
        redisTemplate.delete("file_record:fileId:" + fileId);
        ThreadUtil.execute(() -> {
            imageRepository.save(new KeywordImageDoc(findByFileId(fileId)));
        });
    }

    @Override
    public void decrLoverNum(Long fileId) {
        recordMapper.decrLoverNum(fileId);
        redisTemplate.delete("file_record:fileId:" + fileId);
        ThreadUtil.execute(() -> {
            imageRepository.save(new KeywordImageDoc(findByFileId(fileId)));
        });
    }

    /**
     * 缓存逐出
     */
    private void cacheEvict() {
        ThreadUtil.execute(() -> {
            Cursor<String> scan = redisTemplate.scan(ScanOptions.scanOptions()
                    .match("file_record:*")
                    .count(10).build());
            while (scan.hasNext()) {
                redisTemplate.delete(scan.next());
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException ignored) {

                }
            }
            scan.close();
        });
    }

    private void deleteFile(File f) {
        executorService.execute(() -> {
            while (!FileUtil.del(f)) {
                log.info("{} 删除失败, 重试中...", f.getAbsolutePath());
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("{} 删除成功", f.getAbsolutePath());
        });
    }

}
