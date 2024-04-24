package com.owofurry.furry.img.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "上传文件参数")
public class UploadFileParam {

    @Schema(description = "文件,根据配置文件相关配置，决定多文件上传大小和单文件大小")
    MultipartFile[] files;

    @Schema(description = "关键字，用于搜索")
    @Size(max = 10, min = 1, message = "关键字长度不得大于100")
    List<String> keywords;

    @Nullable
    String title;

    @Nullable
    String introduce;

}
