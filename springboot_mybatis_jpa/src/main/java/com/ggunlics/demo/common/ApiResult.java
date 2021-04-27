package com.ggunlics.demo.common;

import cn.hutool.http.HttpStatus;
import lombok.Data;

/**
 * 统一返回
 *
 * @author ggunlics
 * @date 2021/3/5 9:27
 **/
@Data
public class ApiResult<T> {
    private static final Integer CODE_SUCCESS = 200;
    private static final Integer CODE_FAILURE = 400;
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";
    /**
     * 返回状态码 200 成功; 400 失败
     */
    private Integer code;
    /**
     * 返回信息
     */
    private String msg;
    /**
     * 如果使用javax.validation的话
     * 可以使用@NotBlank/@NotNull表示字段必须
     */
    private T data;

    public ApiResult(Integer code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public ApiResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ApiResult<String> ok() {
        return new ApiResult<>(CODE_SUCCESS, SUCCESS);
    }

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(CODE_SUCCESS, SUCCESS, data);
    }

    public static ApiResult<String> ok(String msg) {
        return new ApiResult<>(CODE_SUCCESS, msg);
    }

    public static ApiResult<String> error() {
        return new ApiResult<>(CODE_FAILURE, FAILURE);
    }

    public static ApiResult<String> error(String msg) {
        return new ApiResult<>(CODE_FAILURE, msg);
    }

    public static ApiResult<String> loginExpired(Exception e) {
        return new ApiResult<>(HttpStatus.HTTP_UNAUTHORIZED, e.getMessage());
    }
}
