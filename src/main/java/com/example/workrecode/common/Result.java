package com.example.workrecode.common;

import lombok.Data;

/**
 * 统一结果封装类
 */
@Data
public class Result<T> {

    /**
     * 状态码，200表示成功，其他表示失败
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 构造方法私有化，防止外部直接实例化
     */
    private Result() {}

    /**
     * 成功返回结果
     * @param data 返回数据
     * @param <T> 数据类型
     * @return 统一结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功返回结果
     * @param message 提示信息
     * @param data 返回数据
     * @param <T> 数据类型
     * @return 统一结果
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     * @param <T> 数据类型
     * @return 统一结果
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    /**
     * 失败返回结果
     * @param code 状态码
     * @param message 提示信息
     * @param <T> 数据类型
     * @return 统一结果
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}
