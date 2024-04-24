package com.owofurry.furry.img.controller.admin;

import com.owofurry.furry.img.service.FileRecordService;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.vo.R;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/file")
@PreAuthorize("hasRole('root')")
public class FileControlController {

    FileRecordService fileRecordService;

    public FileControlController(FileRecordService fileRecordService) {
        this.fileRecordService = fileRecordService;
    }

    @GetMapping("/list/no_check/{page}")
    public R listNotCheck(@PathVariable("page") int page) {
        return RUtil.ok();
    }

    @DeleteMapping("/del/{fileId}")
    public R del(@PathVariable("fileId") long fileId) {
        fileRecordService.deleteRecord(fileId);
        return RUtil.ok();
    }

}