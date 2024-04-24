package com.owofurry.furry.img.controller;

import com.google.common.util.concurrent.Striped;
import com.owofurry.furry.img.entity.FileRecord;
import com.owofurry.furry.img.exception.UserOperationException;
import com.owofurry.furry.img.service.FileRecordService;
import com.owofurry.furry.img.service.LoverService;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.utils.UserUtil;
import com.owofurry.furry.img.vo.R;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.locks.Lock;

@RestController
@RequestMapping("/lover")
public class LoverController {
    LoverService loverService;

    FileRecordService fileRecordService;

    Striped<Lock> lock;

    public LoverController(LoverService loverService,
                           FileRecordService fileRecordService,
                           Striped<Lock> lock) {
        this.loverService = loverService;
        this.fileRecordService = fileRecordService;
        this.lock = lock;
    }

    @GetMapping("/num/{fileId}")
    public R getLoverNum(@PathVariable("fileId") Long fileId) {
        FileRecord fileRecord = fileRecordService.findByFileId(fileId);
        if (fileRecord == null) {
            throw new UserOperationException("文件不存在");
        }
        return RUtil.ok(fileRecord.getLoverNum());
    }

    @GetMapping("/exists/{fileId}")
    public R isLover(@PathVariable("fileId") Long fileId) {
        FileRecord fileRecord = fileRecordService.findByFileId(fileId);
        if (fileRecord == null) {
            throw new UserOperationException("文件不存在");
        }
        // 判断是否点过赞
        return RUtil.ok(loverService.exist(fileId, UserUtil.getId()));
    }

    @PostMapping("/love")
    public R love(@RequestParam("fileId") Long fileId) {
        FileRecord fileRecord = fileRecordService.findByFileId(fileId);
        if (fileRecord == null) {
            throw new UserOperationException("文件不存在");
        }
        Lock l = lock.get(fileId + ":" + UserUtil.getId());
        try {
            l.lock();
            if (!loverService.save(fileId, UserUtil.getId())) {
                throw new UserOperationException("您已经点过赞了");
            } else {
                fileRecordService.incrLoverNum(fileId);
            }
        } finally {
            l.unlock();
        }
        return RUtil.ok();
    }

    @PostMapping("/unloved")
    public R unloved(@RequestParam("fileId") Long fileId) {
        FileRecord fileRecord = fileRecordService.findByFileId(fileId);
        if (fileRecord == null) {
            throw new UserOperationException("文件不存在");
        }
        Lock l = lock.get(fileId + ":" + UserUtil.getId());
        try {
            l.lock();
            if (!loverService.delete(fileId, UserUtil.getId())) {
                throw new UserOperationException("您没有点过赞");
            } else {
                fileRecordService.decrLoverNum(fileId);
            }
        } finally {
            l.unlock();
        }
        return RUtil.ok();
    }

}
