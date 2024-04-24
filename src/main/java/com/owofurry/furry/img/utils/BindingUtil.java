package com.owofurry.furry.img.utils;

import com.owofurry.furry.img.exception.UserOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Slf4j
public class BindingUtil {
    public static void validate(BindingResult result) {

        if (result.hasErrors()) {
            FieldError fieldError = result.getFieldError();
            if (fieldError != null) {
                log.debug("参数错误：{}", fieldError.getDefaultMessage());
                throw new UserOperationException(result.getFieldError().getDefaultMessage());
            }
            throw new UserOperationException("参数错误");
        }
    }
}
