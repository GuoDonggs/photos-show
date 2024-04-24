package com.owofurry.furry.img.controller.resource;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.owofurry.furry.img.config.PathConfiguration;
import com.owofurry.furry.img.config.SystemConfiguration;
import com.owofurry.furry.img.dto.param.UploadFileParam;
import com.owofurry.furry.img.entity.FileRecord;
import com.owofurry.furry.img.exception.UserOperationException;
import com.owofurry.furry.img.service.FileRecordService;
import com.owofurry.furry.img.service.KeywordService;
import com.owofurry.furry.img.utils.BindingUtil;
import com.owofurry.furry.img.utils.LimitUtil;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.utils.UserUtil;
import com.owofurry.furry.img.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传", description = "文件上传接口，需登录")
@Slf4j
public class UploadFileController {

    FileRecordService fileRecordService;

    KeywordService keywordService;

    SystemConfiguration systemConfiguration;

    PathConfiguration pathConfiguration;

    public UploadFileController(FileRecordService fileRecordService,
                                KeywordService keywordService,
                                SystemConfiguration systemConfiguration,
                                PathConfiguration pathConfiguration) {
        this.fileRecordService = fileRecordService;
        this.keywordService = keywordService;
        this.systemConfiguration = systemConfiguration;
        this.pathConfiguration = pathConfiguration;
    }

    @PostMapping("/image")
    @Operation(summary = "上传图片", description = "上传图片，需登录")
    public R uploadFile(@Validated UploadFileParam param,
                        BindingResult bindingResult) throws IOException {
        assentNotLimit();
        assentKeywords(param.getKeywords());
        assentFileAllow(param.getFiles(), "image");
        BindingUtil.validate(bindingResult);
        FileOutputStream outputStream;
        InputStream inputStream;
        String fileName;
        String path;
        FileRecord fileRecord;
        for (MultipartFile file : param.getFiles()) {
            // 处理上传文件
            fileName = IdUtil.simpleUUID() + "." + FileNameUtil.getSuffix(file.getOriginalFilename());
            path = pathConfiguration.getImage() + File.separator + fileName;
            outputStream = new FileOutputStream(pathConfiguration.getBase() + path);
            inputStream = file.getInputStream();
            inputStream.transferTo(outputStream);
            inputStream.close();
            outputStream.close();
            // 设置参数
            fileRecord = buildRecord(param.getTitle(), param.getIntroduce(), path);
            fileRecord.setKeywords(buildKeyword(param.getKeywords()));
            // 修正请求路径

            fileRecordService.addRecord(fileRecord);
        }
        // 添加关键字
        keywordService.add(param.getKeywords());
        return RUtil.ok();
    }

    private FileRecord buildRecord(String title, String introduce, String path) {
        FileRecord fileRecord = new FileRecord();
        fileRecord.setUploadDate(DateUtil.date());
        fileRecord.setUploadUser(UserUtil.getId());
        fileRecord.setTitle(title);
        fileRecord.setHasChecked(false);
        fileRecord.setIntroduce(introduce);
        fileRecord.setFilePath(path.replace(File.separator, "/"));
        return fileRecord;
    }

    private String buildKeyword(List<String> keyword) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = keyword.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private void assentNotLimit() {
        boolean res = LimitUtil.cumulative("uploadFile:" + UserUtil.getId(), 10, 10);
        if (res) {
            throw new UserOperationException("站点限流，请晚一点在尝试");
        }
    }

    private void assentKeywords(List<String> keywords) {
        for (int i = 0; i < keywords.size(); i++) {
            if (keywords.get(i).length() > 5) {
                throw new UserOperationException("关键字长度不能超过5");
            }
            if (keywords.get(i).isEmpty()) {
                throw new UserOperationException("关键字不能为空");
            }
            String s = keywords.get(i).trim().split("[,，\\s]")[0];
            if (s.isEmpty() || s.length() > 5) {
                throw new UserOperationException("关键字格式错误");
            }
            keywords.set(i, s);
        }
    }

    private void assentFileAllow(MultipartFile[] files, String contentType) {
        if (files == null || files.length == 0) {
            throw new UserOperationException("不得上传空文件");
        }
        if (files.length > systemConfiguration.getMaxFileNum()) {
            throw new UserOperationException("文件数量过多");
        }
        for (MultipartFile f : files) {
            if (f.getSize() > systemConfiguration.getMaxFileSize() * 1024L * 1024L) {
                throw new UserOperationException("文件过大");
            }
            if (f.getContentType() == null || !f.getContentType().contains(contentType)) {
                throw new UserOperationException("文件类型错误");
            }
        }
    }

}
