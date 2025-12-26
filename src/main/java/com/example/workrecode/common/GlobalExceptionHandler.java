package com.example.workrecode.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有异常
     * @param e 异常对象
     * @return 统一结果
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 记录日志，这里简单打印异常信息
        e.printStackTrace();
        // 返回统一错误结果
        return Result.error(500, "服务器内部错误：" + e.getMessage());
    }

    /**
     * 处理空指针异常
     * @param e 异常对象
     * @return 统一结果
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e) {
        e.printStackTrace();
        return Result.error(500, "空指针异常：" + e.getMessage());
    }

    /**
     * 处理数据类型转换异常
     * @param e 异常对象
     * @return 统一结果
     */
    @ExceptionHandler(ClassCastException.class)
    public Result<?> handleClassCastException(ClassCastException e) {
        e.printStackTrace();
        return Result.error(500, "数据类型转换异常：" + e.getMessage());
    }
}
