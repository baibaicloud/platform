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
 *   nbflow  2020年4月14日 下午9:47:22  created
 */
package com.loon.bridge.service.tunnel;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.loon.bridge.core.comenum.FRPProxyType;
import com.loon.bridge.core.comenum.Status;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.service.device.DeviceService;
import com.loon.bridge.service.log.LogService;
import com.loon.bridge.service.session.Client;
import com.loon.bridge.service.session.Message;
import com.loon.bridge.service.session.SessionService;
import com.loon.bridge.uda.entity.Tunnel;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.uda.mapper.TunnelMapper;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class TunnelService {

    @Value("${commons.check.porting.host}")
    private String CHECK_PORTING_HOST;

    @Autowired
    private LogService logService;

    @Autowired
    private TunnelMapper tunnelMapper;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DeviceService deviceService;

    /**
     * 根据设备id查询隧道列表
     * 
     * @param deviceId
     * @param offset
     * @param size
     * @return
     */
    public List<Tunnel> getTunnelListActiveByDeviceId(Long deviceId) {
        Tunnel entity = new Tunnel();
        entity.setDeviceId(deviceId);
        entity.setIsactive(Status.YES);
        QueryWrapper<Tunnel> queryWrapper = new QueryWrapper<Tunnel>();
        queryWrapper.setEntity(entity);
        queryWrapper.orderByDesc("id");

        return tunnelMapper.selectList(queryWrapper);
    }

    /**
     * 根据用户id查询隧道列表
     * 
     * @param uid
     * @param offset
     * @param size
     * @return
     */
    public IPage<Tunnel> getTunnelListByUid(Long uid, int offset, int size) {
        Tunnel entity = new Tunnel();
        entity.setUid(uid);
        QueryWrapper<Tunnel> queryWrapper = new QueryWrapper<Tunnel>();
        queryWrapper.setEntity(entity);
        queryWrapper.orderByDesc("id");
        
        Page<Tunnel> page = new Page<Tunnel>(offset, size);
        return tunnelMapper.selectPage(page, queryWrapper);
    }

    /**
     * 添加隧道端
     * 
     * @param oper
     * @param tunnel
     * @return
     */
    public Tunnel addTunnel(User oper, Tunnel tunnel) {
        
        if (tunnel.getDeviceId() == null) {
            throw new BusinessException("参数错误");
        }

        if (!deviceService.hasDeviceByCurUser(tunnel.getDeviceId())) {
            throw new BusinessException("参数错误");
        }

        if (tunnel.getLocalPort() < 0 || tunnel.getRemotePort() < 0) {
            throw new BusinessException("端口格式错误");
        }

        Tunnel temptunnel = getTunnelByRemotePort(tunnel.getRemotePort());
        if (temptunnel != null) {
            throw new BusinessException("[" + tunnel.getRemotePort() + "]远程端口已在记录中");
        }
        
        boolean useing = portuse(tunnel.getRemotePort());
        if (useing) {
            throw new BusinessException("[" + tunnel.getRemotePort() + "]远程端口被占用");
        }

        tunnel.setUid(RequestUtil.getSecurityUser().getTargetId());
        tunnel.setCtime(new Date());
        tunnelMapper.insert(tunnel);

        Message msg = new Message();
        msg.setEvent(Message.Type.TUNNEL_REFRESH_LIST);
        sessionService.asynSendMsg(tunnel.getDeviceId(), msg);

        logService.addLog(oper, "tunnel", "添加隧道服务,本地端口：{},远程端口:{}", tunnel.getLocalPort(), tunnel.getRemotePort());
        return null;
    }

    /**
     * 根据远程端口获取详细信息
     * 
     * @param remotePort
     * @return
     */
    private Tunnel getTunnelByRemotePort(int remotePort) {
        Tunnel entity = new Tunnel();
        entity.setRemotePort(remotePort);
        QueryWrapper<Tunnel> queryWrapper = new QueryWrapper<Tunnel>();
        queryWrapper.setEntity(entity);
        return tunnelMapper.selectOne(queryWrapper);
    }

    /**
     * 判断端口是否被使用
     * 
     * @param port
     * @return
     */
    private boolean portuse(int port) {
        boolean flag = false;
        try {
            InetAddress theAddress = InetAddress.getByName(CHECK_PORTING_HOST);
            Socket socket = new Socket(theAddress, port);
            socket.close();
            socket = null;
            flag = true;
        } catch (Exception e) {

        }

        return flag;
    }

    /**
     * del tunnel
     * 
     * @param oper
     * @param id
     */
    public void delTunnel(User oper, Long id) {

        Tunnel tunnel = this.tunnelMapper.selectById(id);
        if (tunnel == null) {
            throw new BusinessException("参数错误");
        }

        if (!deviceService.hasDeviceByCurUser(tunnel.getDeviceId())) {
            throw new BusinessException("参数错误");
        }

        this.tunnelMapper.deleteById(id);

        Message msg = new Message();
        msg.setEvent(Message.Type.TUNNEL_REFRESH_LIST);
        sessionService.asynSendMsg(tunnel.getDeviceId(), msg);
        logService.addLog(oper, "tunnel", "删除隧道服务,本地端口：{},远程端口:{}", tunnel.getLocalPort(), tunnel.getRemotePort());
    }

    /**
     * 切换协议状态
     * 
     * @param oper
     * @param id
     * @param status
     */
    public void switchTunnel(User oper, Long id, Status status) {

        Tunnel tunnel = this.tunnelMapper.selectById(id);
        if (tunnel == null) {
            return;
        }

        if (!deviceService.hasDeviceByCurUser(tunnel.getDeviceId())) {
            throw new BusinessException("参数错误");
        }

        Tunnel update = new Tunnel();
        update.setId(id);
        update.setIsactive(status);
        this.tunnelMapper.updateById(update);

        Message msg = new Message();
        msg.setEvent(Message.Type.TUNNEL_REFRESH_LIST);
        sessionService.asynSendMsg(tunnel.getDeviceId(), msg);

        logService.addLog(oper, "tunnel", "切换隧道服务状态,本地端口：{},远程端口:{},状态：{}", tunnel.getLocalPort(), tunnel.getRemotePort(), status);
    }

    /**
     * 获取隧道列表
     * 
     * @param client
     * @param params
     * @return
     */
    public Object getTunnelList(Client client, Map<String, String> params) {
        
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
        List<Tunnel> list = this.getTunnelListActiveByDeviceId(client.getDevice().getId());
        if (list.isEmpty()) {
            return retList;
        }
        
        for (Tunnel item : list) {
            Map<String, Object> tempitem = new HashMap<String, Object>();
            tempitem.put("id", FRPProxyType.TUNNEL.toString() + "_" + client.getToken() + "_" + item.getId());
            tempitem.put("tunnetType", item.getTunnetType());
            tempitem.put("localIp", item.getLocalIp());
            tempitem.put("localPort", item.getLocalPort());
            tempitem.put("remotePort", item.getRemotePort());
            retList.add(tempitem);
        }

        return retList;
    }

    /**
     * 根据id获取详细信息
     * 
     * @param id
     * @return
     */
    public Tunnel getTunnelById(Long id) {
        return tunnelMapper.selectById(id);
    }

}
