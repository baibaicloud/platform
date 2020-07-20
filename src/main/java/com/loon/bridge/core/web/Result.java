/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2008-2014 loon. All rights reserved.
 * <p>
 * This software is the confidential and proprietary information of loon.
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the agreements you entered into with loon.
 * 
 * Modified history:
 *   Loon  2019年11月4日 下午11:32:21  created
 */
package com.loon.bridge.core.web;

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.loon.bridge.core.exception.BusinessException;

/**
 * 
 *
 * @author nbflow
 */
public class Result {

    private boolean success = true;
    private String message = "";
    private Object content = null;
    private Integer code = 0;

    public Result() {

    }

    public static Result Success() {
        return new Result();
    }

    public static Result Success(Object content) {
        Result temp = new Result();
        temp.setContent(content);
        return temp;
    }

    public static Result SuccessPage(IPage<?> page, Object content) {
        Map<String, Object> retInfo = new HashMap<>();
        retInfo.put("records", content);
        retInfo.put("total", page.getTotal());
        retInfo.put("size", page.getSize());
        retInfo.put("current", page.getCurrent());
        retInfo.put("pages", page.getPages());
        Result temp = new Result();
        temp.setContent(retInfo);
        return temp;
    }

    public static Result Error() {
        return Error("未知错误");
    }

    public static Result Error(String message) {
        Result temp = new Result();
        temp.setSuccess(false);
        temp.setMessage(message);
        return temp;
    }

    public static Result Error(String message, int code) {
        Result temp = new Result();
        temp.setSuccess(false);
        temp.setMessage(message);
        temp.setCode(code);
        return temp;
    }

    public static Result Error(BusinessException exception) {
        Result temp = new Result();
        temp.setSuccess(false);
        temp.setMessage(exception.getMessage());
        temp.setCode(exception.getCode());
        return temp;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the content
     */
    public Object getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(Object content) {
        this.content = content;
    }

    /**
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Integer code) {
        this.code = code;
    }

}
