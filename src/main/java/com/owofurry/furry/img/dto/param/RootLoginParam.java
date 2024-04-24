package com.owofurry.furry.img.dto.param;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RootLoginParam {
    @NotBlank
    String username;

    @NotBlank
    String passwd;
}
