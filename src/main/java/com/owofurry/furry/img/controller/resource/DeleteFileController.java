package com.owofurry.furry.img.controller.resource;

import com.owofurry.furry.img.service.FileRecordService;
import com.owofurry.furry.img.service.LoverService;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.utils.UserUtil;
import com.owofurry.furry.img.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delete")
@Tag(name = "删除文件", description = "删除指定ID文件")
public class DeleteFileController {
    FileRecordService fileRecordService;

    LoverService loverService;

    public DeleteFileController(FileRecordService fileRecordService,
                                LoverService loverService) {
        this.fileRecordService = fileRecordService;
        this.loverService = loverService;
    }

    @DeleteMapping("{fileId}")
    @Operation(summary = "删除文件")
    @Parameter(description = "文件ID", required = true, example = "1", name = "fileId")
    public R deleteFile(@PathVariable("fileId") Long fileId) {
        fileRecordService.deleteRecord(UserUtil.getId(), fileId);
        loverService.delete(fileId);
        return RUtil.ok();
    }
}