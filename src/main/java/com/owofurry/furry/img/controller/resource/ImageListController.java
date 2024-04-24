package com.owofurry.furry.img.controller.resource;

import com.owofurry.furry.img.exception.UserOperationException;
import com.owofurry.furry.img.service.FileRecordService;
import com.owofurry.furry.img.utils.LimitUtil;
import com.owofurry.furry.img.utils.RequestAddressUtil;
import com.owofurry.furry.img.utils.UserUtil;
import com.owofurry.furry.img.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "获取图片", description = "获取图片相集合接口")
@RestController
@RequestMapping("/image")
public class ImageListController {

    FileRecordService fileRecordService;

    public ImageListController(FileRecordService fileRecordService) {
        this.fileRecordService = fileRecordService;
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门图片")
    @Parameter(name = "size", description = "最大数量，最大为15", required = false, example = "15")
    public R hot(@RequestParam(value = "size", required = false, defaultValue = "15") int size) {
        if (size > 15) {
            size = 15;
        }
        return fileRecordService.hot(size);
    }

    @GetMapping("/list")
    @Operation(summary = "获取图片列表")
    @Parameter(name = "page", description = "页数，从1开始", required = false, example = "1")
    @Parameter(name = "size", description = "每页数量，最大为20", required = false, example = "20")
    public R list(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                  @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        if (size > 20) {
            size = 20;
        }
        return fileRecordService.listFile(page, size);
    }

    @GetMapping("/list/by_user")
    public R listByuser(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                        @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        if (size > 20) {
            size = 20;
        }
        return fileRecordService.listByUser(UserUtil.getId(), page, size);
    }

    @GetMapping("/search/{str}")
    @Operation(summary = "搜索图片")
    @Parameter(name = "str", description = "搜索关键字", required = true, example = "蓝蓝鱼")
    public R search(@PathVariable("str") String str,
                    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                    @RequestParam(value = "size", required = false, defaultValue = "30") int size,
                    HttpServletRequest request) {
        boolean res = LimitUtil.cumulative("search:limit:" + RequestAddressUtil.getRemoteAddress(request),
                100, 5);
        if (res) {
            throw new UserOperationException("请求太频繁啦，小水管服务器，请等一等再试");
        }
        return fileRecordService.search(str, page, size);
    }


}