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
 *   Loon  2020年1月9日 上午12:04:22  created
 */
package com.loon.bridge.service.lastuse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.loon.bridge.uda.entity.DeviceUser;
import com.loon.bridge.uda.entity.DeviceUserLastUse;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.uda.mapper.DeviceUserLastUseMapper;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class DeviceUserLastUseService {

    @Value("${device.lastuse.max.count}")
    private Integer DEVICE_LASTUSE_MAX_COUNT;

    @Value("${device.lastuse.del.count}")
    private Integer DEVICE_LASTUSE_DEL_COUNT;

    @Autowired
    private DeviceUserLastUseMapper deviceUserLastUseMapper;

    /**
     * 最近使用列表
     * 
     * @param user
     * @return
     */
    public List<DeviceUserLastUse> getDeviceUserLastUseListByUser(User user) {
        DeviceUserLastUse entity = new DeviceUserLastUse();
        entity.setUid(user.getId());

        QueryWrapper<DeviceUserLastUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        queryWrapper.orderByDesc("lasttime");

        List<DeviceUserLastUse> list = deviceUserLastUseMapper.selectList(queryWrapper);
        if (list.size() < DEVICE_LASTUSE_MAX_COUNT) {
            return list;
        }

        List<Long> ids = new ArrayList<Long>();
        int i = list.size() - DEVICE_LASTUSE_DEL_COUNT;
        for (; i < list.size(); i++) {
            ids.add(list.get(i).getId());
        }
        deviceUserLastUseMapper.deleteBatchIds(ids);

        return deviceUserLastUseMapper.selectList(queryWrapper);
    }

    /**
     * 追加使用记录
     * 
     * @param user
     * @param deviceUser
     */
    public void append(User user, DeviceUser deviceUser) {

        // 这里先永久跳过
        if (user == null || deviceUser == null) {
            return;
        }

        DeviceUserLastUse deviceUserLastUse = getDeviceUserLastUseByDeviceUser(user, deviceUser);
        if (deviceUserLastUse == null) {
            deviceUserLastUse = new DeviceUserLastUse();
            deviceUserLastUse.setUid(user.getId());
            deviceUserLastUse.setDeviceUserId(deviceUser.getId());
            deviceUserLastUse.setLasttime(new Date());
            deviceUserLastUseMapper.insert(deviceUserLastUse);
            return;
        }

        DeviceUserLastUse update = new DeviceUserLastUse();
        update.setId(deviceUserLastUse.getId());
        update.setLasttime(new Date());
        deviceUserLastUseMapper.updateById(update);
    }

    /**
     * 获取最近使用记录
     * 
     * @param user
     * @param deviceUser
     * @return
     */
    private DeviceUserLastUse getDeviceUserLastUseByDeviceUser(User user, DeviceUser deviceUser) {
        DeviceUserLastUse entity = new DeviceUserLastUse();
        entity.setUid(user.getId());
        entity.setDeviceUserId(deviceUser.getId());
        QueryWrapper<DeviceUserLastUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return deviceUserLastUseMapper.selectOne(queryWrapper);
    }

    /**
     * 删除最近使用记录
     * 
     * @param user
     * @param deviceUser
     */
    public void deleteLastuse(User user, DeviceUser deviceUser) {
        DeviceUserLastUse temp = getDeviceUserLastUseByDeviceUser(user, deviceUser);
        if (temp == null) {
            return;
        }

        deviceUserLastUseMapper.deleteById(temp.getId());
    }
}
