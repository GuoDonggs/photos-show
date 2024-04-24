package com.owofurry.furry.img.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "通用的返回信息类")
public class R implements Serializable {
    @Schema(description = "1 为成功，其他均为不同失败结果")
    int code;
    @Schema(description = "成功为ok，失败则为的原因")
    String msg;

    @Schema(description = "返回的数据,失败默认不返回")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object data;
}
