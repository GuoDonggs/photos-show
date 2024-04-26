package com.owofurry.furry.img.controller.admin;

import com.owofurry.furry.img.service.AdminFileRecordService;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.vo.R;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/file")
@PreAuthorize("hasRole('root')")
public class FileControlController {

    AdminFileRecordService fileRecordService;

    public FileControlController(AdminFileRecordService fileRecordService) {
        this.fileRecordService = fileRecordService;
    }

    @GetMapping("/list/no_check")
    public R listNotCheck(@RequestParam("page") int page,
                          @RequestParam("size") int size) {
        return fileRecordService.listNotCheck(page, size);
    }

    @PostMapping("/checked")
    public R checked(@RequestParam("fileId") long fileId) {
        int r = fileRecordService.checked(fileId);
        if (r == 1) {
            return RUtil.ok();
        }
        return RUtil.error(RUtil.FAILED_CODE, "操作失败，操作受影响数量为 " + r);
    }

    @DeleteMapping("/del/{fileId}")
    public R del(@PathVariable("fileId") long fileId) {
        fileRecordService.deleteRecord(fileId);
        return RUtil.ok();
    }

}