package com.owofurry.furry.img.service.imp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.owofurry.furry.img.config.PathConfiguration;
import com.owofurry.furry.img.elasticsearch.KeywordImageDoc;
import com.owofurry.furry.img.elasticsearch.KeywordImageRepository;
import com.owofurry.furry.img.entity.FileRecord;
import com.owofurry.furry.img.mapper.FileRecordMapper;
import com.owofurry.furry.img.service.AdminFileRecordService;
import com.owofurry.furry.img.service.FileRecordService;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.vo.R;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * admin文件记录管理
 *
 * @author 果冻
 * @date 2024/04/26
 */
@Service
public class AdminFileRecordImp implements AdminFileRecordService {

    FileRecordService fileRecordService;

    FileRecordMapper recordMapper;

    PathConfiguration pathConfiguration;

    RedisTemplate<String, Object> redisTemplate;

    KeywordImageRepository imageRepository;

    ExecutorService executorService;

    public AdminFileRecordImp(FileRecordService fileRecordService,
                              FileRecordMapper recordMapper,
                              PathConfiguration pathConfiguration,
                              RedisTemplate<String, Object> redisTemplate,
                              KeywordImageRepository imageRepository,
                              @Qualifier("virtualThreadExecutor") ExecutorService executorService) {
        this.fileRecordService = fileRecordService;
        this.recordMapper = recordMapper;
        this.pathConfiguration = pathConfiguration;
        this.redisTemplate = redisTemplate;
        this.imageRepository = imageRepository;
        this.executorService = executorService;
    }

    @Override
    @Cacheable(cacheNames = "file_record:not_check")
    public R listNotCheck(int page, int size) {
        QueryWrapper<FileRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("has_checked", 0);
        Page<FileRecord> recordPage = new Page<>(page, size);
        return RUtil.ok(recordMapper.selectPage(recordPage, wrapper).getRecords());
    }

    @Override
    @CacheEvict(cacheNames = {"file_record:search", "file_record:not_check",
            "file_record:user",
            "file_record:hot", "file_record:list"}, allEntries = true)
    public void deleteRecord(Long... fileId) {
        for (Long id : fileId) {
            FileRecord byFileId = fileRecordService.findByFileId(id);
            recordMapper.deleteById(id);
            File f = new File(pathConfiguration.getBase() + File.separator + byFileId.getFilePath());
            FileUtil.del(f);
            redisTemplate.delete("file_record:" + id);
            imageRepository.deleteById(id);
        }
        cacheEvict();
    }

    @CacheEvict(cacheNames = {
            "file_record:search",
            "file_record:not_check",
            "file_record:user"}, allEntries = true)
    public int checked(Long fileId) {
        UpdateWrapper<FileRecord> wrapper = new UpdateWrapper<>();
        wrapper.eq("file_id", fileId);
        wrapper.set("has_checked", 1);
        int r = recordMapper.update(wrapper);
        if (r == 1) {
            // 异步更新 els
            executorService.execute(() -> {
                Optional<KeywordImageDoc> doc = imageRepository.findById(fileId);
                doc.ifPresent(e -> {
                    e.setHasChecked(true);
                    imageRepository.save(e);
                });
            });
        }
        return r;
    }


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

}
