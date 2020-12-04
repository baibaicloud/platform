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
 *   nbflow  2020年11月27日 下午11:02:30  created
 */
package com.loon.bridge.service.auditvideo;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.DateUtil;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.service.device.DeviceService;
import com.loon.bridge.service.log.LogService;
import com.loon.bridge.service.remotecpe.NatInfo;
import com.loon.bridge.uda.entity.AuditVideo;
import com.loon.bridge.uda.entity.Device;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.uda.mapper.AuditVideoMapper;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class AuditVideoService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${auditvideo.filepath}")
    private String AUDIT_VIDEO_FILE_PATH;

    @Autowired
    private AuditVideoMapper auditVideoMapper;

    @Autowired
    private LogService logService;

    @Autowired
    private DeviceService deviceService;

    /**
     * 获取录像列表
     * 
     * @param username
     * @param deviceName
     * @param stime
     * @param etime
     * @param page
     * @param limit
     * @return
     */
    public IPage<AuditVideo> getAuditVideoList(String username, String deviceName, String stime, String etime, int page, int size) {
        QueryWrapper<AuditVideo> queryWrapper = new QueryWrapper<AuditVideo>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("targetid", RequestUtil.getSecurityUser().getTargetId());
        
        if (!StringUtils.isEmpty(username)) {
            queryWrapper.like("username", username);
        }

        if (!StringUtils.isEmpty(deviceName)) {
            queryWrapper.like("deviceName", deviceName);
        }

        Date startDate = DateUtil.strToDate(stime);
        Date endDate = DateUtil.strToDate(etime);
        
        if (endDate != null && startDate != null) {
            queryWrapper.between("ctime", startDate, endDate);
        }else {
            if(startDate != null) {
                queryWrapper.ge("ctime", startDate);
            }

            if (endDate != null) {
                queryWrapper.le("ctime", endDate);
            }
        }
        

        Page<AuditVideo> pageInfo = new Page<AuditVideo>(page, size);
        return auditVideoMapper.selectPage(pageInfo, queryWrapper);
    }

    /**
     * 删除录像
     * 
     * @param id
     * @param targetId
     */
    public void delAuditVideoByIdTargetid(User oper, Long id, Long targetId) {
        AuditVideo tempAuditVideo = auditVideoMapper.selectById(id);
        if (tempAuditVideo == null) {
            throw new BusinessException("参数错误");
        }

        if (!tempAuditVideo.getTargetid().equals(targetId)) {
            throw new BusinessException("无权限删除");
        }

        auditVideoMapper.deleteById(id);
        logService.addLog(oper, "审计录像", "删除了录像,设备名称:{}", tempAuditVideo.getDeviceName());
    }

    /**
     * 添加录像记录
     * 
     * @param tempNatInfo
     */
    public void addAuditVideo(NatInfo natInfo) {

        Device device = deviceService.getDeviceById(natInfo.getDeviceId());
        AuditVideo addAuditVideo = new AuditVideo();
        addAuditVideo.setCtime(new Date());
        addAuditVideo.setDeviceName(device.getName());
        addAuditVideo.setSn(device.getSn());
        addAuditVideo.setTargetid(natInfo.getTargetId());
        addAuditVideo.setUsername(natInfo.getUsername());
        addAuditVideo.setPath(natInfo.getUuid());
        
        auditVideoMapper.insert(addAuditVideo);
        logger.info("add audit video success,target:{},id:{}", natInfo.getTargetId(), addAuditVideo.getId());
    }

    /**
     * 根据uuid获取录像
     * 
     * @param uuid
     * @return
     */
    public File getFileByuuidCuruser(String uuid) {
        QueryWrapper<AuditVideo> queryWrapper = new QueryWrapper<AuditVideo>();
        queryWrapper.eq("path", uuid);
        queryWrapper.eq("targetid", RequestUtil.getSecurityUser().getTargetId());
        AuditVideo tempAuditVideo = auditVideoMapper.selectOne(queryWrapper);
        if (tempAuditVideo == null) {
            return null;
        }

        File file = new File(AUDIT_VIDEO_FILE_PATH + "/" + tempAuditVideo.getPath());
        if (file.exists()) {
            return file;
        }

        return null;
    }

}
