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
 *   Loon  2019年12月14日 下午4:01:28  created
 */
package com.loon.bridge.service.ack;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.loon.bridge.core.utils.Util;
import com.loon.bridge.service.session.Client;
import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class ACKService {

    public static final String ERROR = "error";

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, CompletableFutureInfo> acks = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 创建异步确认
     * 
     * @param data
     * @param timeoutCallback
     * @return
     */
    public CompletableFutureInfo make(User user) {
        String uuid = Util.UUID();
        CompletableFuture<Object> future = new CompletableFuture<Object>();
        CompletableFutureInfo info = new CompletableFutureInfo(uuid, user, future);
        acks.put(uuid, info);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                CompletableFutureInfo tempCompletableFutureInfo = info;
                try {
                    future.get(8, TimeUnit.SECONDS);
                    acks.remove(tempCompletableFutureInfo.getUuid());
                    logger.debug("uid ack success,uid:{},uuid:{}", tempCompletableFutureInfo.getUser().getId(), tempCompletableFutureInfo.getUuid());
                } catch (Exception e) {
                    acks.remove(tempCompletableFutureInfo.getUuid());
                    tempCompletableFutureInfo.getFuture().complete(ACKService.ERROR);
                    logger.debug("uid ack timeout,uid:{},uuid:{}", tempCompletableFutureInfo.getUser().getId(), tempCompletableFutureInfo.getUuid());
                }
            }
        });

        logger.debug("uid make ack success,uid:{},uuid:{}", user.getId(), uuid);
        return info;
    }

    public Object ack(Client client, Map<String, String> params) {

        String uuid = params.get("ackuuid");
        CompletableFutureInfo info = acks.get(uuid);
        if (info == null) {
            return null;
        }

        String acktype = params.get("acktype");
        if (StringUtils.isEmpty(acktype)) {
            acktype = "yes";
        }
        info.getFuture().complete(acktype);
        logger.debug("user ack success,sn:{},uuid:{}", client.getSn(), uuid);

        return null;
    }

}

