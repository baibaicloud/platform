/*
 * Copyright (C) 2008-2014 star-net. All rights reserved.
 * 
 * Modified history:
 *   loon  2014-4-25 下午11:10:08  created
 */
package com.loon.bridge.core.exception;

/**
 * 业务异常基类，涉及业务逻辑的异常需继承该类
 * <p>
 * 业务异常的构造需提供code、message，分别对应错误码及错误信息.
 * 
 * @author nbflow
 */
public class BusinessException extends RuntimeException {

    /** * The Constant serialVersionUID. */
    private static final long serialVersionUID = 7398199132252047458L;
    private int code = 0;

    public BusinessException() {
        super("参数错误");
    }

    /**
     * @param message
     * @param cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * @param message
     */
    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    /**
     * @param cause
     */
    public BusinessException(Throwable cause) {
        super(cause);
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }
}
