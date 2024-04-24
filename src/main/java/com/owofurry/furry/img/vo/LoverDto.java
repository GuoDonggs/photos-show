package com.owofurry.furry.img.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoverDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 7733088756125449355L;

    private Long fileId;

    private Integer loverNum;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Integer> fileLovers;

}
