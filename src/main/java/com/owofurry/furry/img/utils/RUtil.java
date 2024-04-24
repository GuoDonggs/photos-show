package com.owofurry.furry.img.utils;

import com.owofurry.furry.img.vo.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RUtil {
    public static int NO_AUTH_CODE = 0;
    public static int SUCCESS_CODE = 1;
    public static int FAILED_CODE = -1;
    public static int NOT_ALLOW_CODE = -2;
    public static int NOT_FOUNT_CODE = -3;

    public static String OK_MESSAGE = "OK";

    public static R ok( Object data) {
        return new R (SUCCESS_CODE, OK_MESSAGE, data);
    }

    public static   R ok() {
        return new R (SUCCESS_CODE, OK_MESSAGE, null);
    }

    public static R error(int code, String msg) {
        return new R (code, msg, null);
    }

    public static   R notAllow(String msg) {
        return new R (NOT_ALLOW_CODE, msg, null);
    }

    public static   R notFound( Object data) {
        return new R (NOT_FOUNT_CODE, "no found", data);
    }

    public static   R notFound() {
        return new R (NOT_FOUNT_CODE, "no found", null);
    }

    public static ResponseEntity<R> okRE(Object data) {
        return new ResponseEntity<> (ok(data), HttpStatus.OK);
    }

    public static   ResponseEntity<R> okRE() {
        return new ResponseEntity<> (ok(), HttpStatus.OK);
    }

    public static ResponseEntity<R> errorRE(int code, HttpStatus status, String msg) {
        return new ResponseEntity<>(error(code, msg), status);
    }

    public static ResponseEntity<R> notAllowRE(String msg) {
        return new ResponseEntity<>(notAllow(msg), HttpStatus.BAD_REQUEST);
    }
}
