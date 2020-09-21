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
 *   nbflow  2020年5月3日 下午10:12:50  created
 */
package com.loon.bridge.service.tunnel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loon.bridge.core.comenum.FRPProxyType;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.service.remotecpe.NatInfo;
import com.loon.bridge.service.remotecpe.NatService;
import com.loon.bridge.service.session.Client;
import com.loon.bridge.service.session.SessionService;
import com.loon.bridge.uda.entity.Tunnel;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class TunnelPrivateService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SessionService sessionService;

    @Autowired
    private NatService natService;

    @Autowired
    private TunnelService tunnelService;

    /**
     * 判断代理信息是否合法
     * 
     * @param token
     * @param id
     */
    private void checkProxyRPPortHandler(String token, String id) {
        NatInfo natInfo = natService.getPortInfoByUUID(id);
        if (natInfo == null) {
            throw new BusinessException("checkProxyVNCRDPPortHandler error" + token + "," + id);
        }

        Client client = sessionService.getClientByToken(token);
        if (client == null) {
            throw new BusinessException("checkProxyVNCRDPPortHandler error" + token + "," + id);
        }

        if (!client.getDevice().getId().equals(natInfo.getDeviceId())) {
            throw new BusinessException("checkProxyVNCRDPPortHandler error" + token + "," + id);
        }
    }

    /**
     * 判断tunnel是否合法
     * 
     * @param token
     * @param id
     */
    private void checkProxyTunnelPortHandler(String token, String id) {
        Tunnel tunnel = tunnelService.getTunnelById(Long.valueOf(id));
        if (tunnel == null) {
            throw new BusinessException("checkProxyTunnelPortHandler error" + token + "," + id);
        }

        Client client = sessionService.getClientByToken(token);
        if (client == null) {
            throw new BusinessException("checkProxyTunnelPortHandler error" + token + "," + id);
        }

        if (!client.getDevice().getId().equals(tunnel.getDeviceId())) {
            throw new BusinessException("checkProxyTunnelPortHandler error" + token + "," + id);
        }
    }

    /**
     * 验证端口映射是否合法
     * 
     * @param proxyInfo
     * @return
     */
    public Object checkProxyPort(String proxyInfo, int port) {

        String[] proxyname = proxyInfo.split("_");
        FRPProxyType type = FRPProxyType.valueOf(proxyname[0]);
        String token = proxyname[1];
        String id = proxyname[2];
        if (type == FRPProxyType.RP) {
            this.checkProxyRPPortHandler(token, id);
        } else {
            this.checkProxyTunnelPortHandler(token, id);
        }

        logger.info("client proxy check success,proxyInfo:{}", proxyInfo);
        return "ok";
    }
}
