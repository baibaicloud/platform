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
 *   Loon  2019年11月21日 下午10:33:01  created
 */
package com.loon.bridge.service.hacker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class HackerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void append(Long uid, String msg) {
        logger.error(">>><<<,uid:{},msg:{}", uid, msg);
        throw new BusinessException("请不要非法操作，严重者永久封号并报警");
    }

    public void append(User user, String msg) {
        append(user.getId(), msg);
    }
}
