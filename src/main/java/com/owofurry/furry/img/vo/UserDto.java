package com.owofurry.furry.img.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    Integer uid;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String token;

    public UserDto(Integer uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public UserDto(Integer uid, String username, String token) {
        this.uid = uid;
        this.username = username;
        this.token = token;
    }
}
