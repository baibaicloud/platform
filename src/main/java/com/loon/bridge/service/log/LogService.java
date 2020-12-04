/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2008-2014 nbflow. All rights reserved.
 * <p>
 * This software is the confidential and proprietary information of nbflow.
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the agreements you entered into with nbflow.
 * 
 * Modified history:
 *   nbflow  2020年4月17日 下午10:48:24  created
 */
package com.loon.bridge.service.log;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.Log;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.uda.mapper.LogMapper;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class LogService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private UserService userService;

    /**
     * 添加log
     * 
     * @param user
     * @param module
     * @param content
     */
    public void addLog(Long uid, String module, String content, Object... params) {
        User user = userService.getUserById(uid);
        if(user == null) {
            throw new BusinessException("用户找不到");
        }

        this.addLog(user, module, content, params);
    }

    /**
     * 添加log
     * 
     * @param user
     * @param module
     * @param content
     */
    public void addLog(User user, String module, String content, Object... params) {

        content = content.replace("{}", "%s");
        content = String.format(content, params);

        Log entity = new Log();
        entity.setUid(user.getId());
        entity.setUsername(user.getUsername());
        entity.setModule(module);
        entity.setContent(content);
        entity.setCtime(new Date());
        logMapper.insert(entity);

        logger.debug("username:{},uid:{}, module:{},content:{}", user.getUsername(), user.getId(), module, content);
    }
}
