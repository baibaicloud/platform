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
 *   Loon  2019年12月14日 下午4:19:28  created
 */
package com.loon.bridge.service.ack;

import java.util.concurrent.CompletableFuture;

import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
public class CompletableFutureInfo {

    private User user;
    private String uuid;
    private CompletableFuture<Object> future;

    public CompletableFutureInfo(String uuid, User user, CompletableFuture<Object> future) {
        this.uuid = uuid;
        this.user = user;
        this.future = future;
    }

    public Object get(String timeoutmsg) {
        try {
            Object obj = future.get();
            if (obj instanceof String && obj.toString().equals(ACKService.ERROR)) {
                throw new BusinessException(timeoutmsg);
            }
            return obj;
        } catch (Exception e) {
            throw new BusinessException(timeoutmsg);
        }
    }

    /**
     * @return the future
     */
    public CompletableFuture<Object> getFuture() {
        return future;
    }

    /**
     * @param future the future to set
     */
    public void setFuture(CompletableFuture<Object> future) {
        this.future = future;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

}
