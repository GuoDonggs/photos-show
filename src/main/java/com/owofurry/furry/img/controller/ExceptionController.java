package com.owofurry.furry.img.controller;

import com.owofurry.furry.img.exception.SystemRunningException;
import com.owofurry.furry.img.exception.UserOperationException;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常处理程序
 *
 * @author gs
 */
@Slf4j
@ControllerAdvice
@ResponseBody // 确保异常处理返回的是文本而不是视图
public class ExceptionController {

    @ExceptionHandler({UserOperationException.class})
    public ResponseEntity<R> userOperationException(UserOperationException e) {
        return new ResponseEntity<>(RUtil.notAllow(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<R> missParam() {
        return new ResponseEntity<>(RUtil.notAllow("缺少请求参数"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<R> mediaNotAllow() {
        return RUtil.errorRE(RUtil.NOT_ALLOW_CODE, HttpStatus.BAD_REQUEST, "请求类型不受支持");
    }

    @ExceptionHandler({NoHandlerFoundException.class, AccessDeniedException.class})
    public ResponseEntity<R> notFound() {
        return new ResponseEntity<>(RUtil.notFound(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<R> methodNotAllow() {
        return RUtil.errorRE(RUtil.NOT_ALLOW_CODE, HttpStatus.BAD_REQUEST, "BAD REQUEST METHOD");
    }

    @ExceptionHandler({FileSizeLimitExceededException.class, MaxUploadSizeExceededException.class})
    public ResponseEntity<R> fileSizeLimit() {
        return RUtil.errorRE(RUtil.NOT_ALLOW_CODE, HttpStatus.BAD_REQUEST, "文件过大");
    }

    @ExceptionHandler({NullPointerException.class, SystemRunningException.class, Exception.class})
    public ResponseEntity<R> nullPointerException(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(RUtil.error(RUtil.FAILED_CODE, "发生未知错误"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
