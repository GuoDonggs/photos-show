package com.owofurry.furry.img.controller.resource;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.AntPathMatcher;
import com.owofurry.furry.img.config.PathConfiguration;
import com.owofurry.furry.img.exception.UserOperationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * 资源映射控制器
 *
 * @author 果冻
 */
@Deprecated
//@RestController
@RequestMapping("/raw")
@Slf4j
@Tag(name = "资源映射控制器")
public class ResourceMapController {

    private final PathConfiguration pathConfig;

    private final AntPathMatcher matcher = new AntPathMatcher();

    public ResourceMapController(PathConfiguration pathConfig) {
        this.pathConfig = pathConfig;
    }

    @GetMapping("/**")
    @Operation(summary = "请求文件", description = "映射用户上传保存文件夹中的文件")
    public void resourceMap(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        // 获取当前访问路径的 uri
        String path = matcher.extractPathWithinPattern(
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(),
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString());
        path = path.replace("/", File.separator);
        File file = new File(pathConfig.getBase() + File.separator + path);
        log.info("用户请求文件:{}", file.getAbsolutePath());
        if (file.exists() && file.isFile()) {
            response.setHeader("Content-Type", FileUtil.getMimeType(file.getAbsolutePath()));
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            bis.transferTo(response.getOutputStream());
            response.setHeader("Cache-Control", "max-age=31536000");
        } else {
            throw new UserOperationException("文件不存在");
        }
    }
}
