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
 *   Administrator  2019年9月21日 上午10:32:39  created
 */
package com.loon.bridge.service.guacamole;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.GuacamoleSocket;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.InetGuacamoleSocket;
import org.apache.guacamole.net.SimpleGuacamoleTunnel;
import org.apache.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.apache.guacamole.websocket.GuacamoleWebSocketTunnelEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.loon.bridge.core.comenum.RemoteProtocolType;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.TaskEngine;
import com.loon.bridge.service.file.FileService;
import com.loon.bridge.service.remotecpe.NatInfo;
import com.loon.bridge.service.remotecpe.NatService;

/**
 * 
 *
 * @author nbflow
 */
public class LoonGuacamoleWebSocketTunnelEndpoint extends GuacamoleWebSocketTunnelEndpoint {

    private static Logger logger = LoggerFactory.getLogger(LoonGuacamoleWebSocketTunnelEndpoint.class);
    public static String GUAC_HOSTNAME = "";
    public static Integer GUAC_PORT = 0;
    public static NatService natService;
    public static FileService fileService;
    private static Map<String, Integer> pings = new ConcurrentHashMap<String, Integer>();

    /*
     * @see org.apache.guacamole.websocket.GuacamoleWebSocketTunnelEndpoint#createTunnel(javax.websocket.Session, javax.websocket.EndpointConfig)
     */
    @Override
    protected GuacamoleTunnel createTunnel(Session session, EndpointConfig endpointConfig) throws GuacamoleException {

        Map<String, String> params = getParams(session.getQueryString());
        if (params.isEmpty()) {
            throw new BusinessException("参数错误");
        }

        String uuid = params.get("uuid");
        if (StringUtils.isEmpty(uuid)) {
            throw new BusinessException("参数错误");
        }

        if (pings.containsKey(uuid)) {
            throw new BusinessException("参数错误");
        }

        NatInfo natInfo = natService.getPortInfoByUUID(session.getUserPrincipal().getName(), uuid);
        if (natInfo == null) {
            throw new BusinessException("参数错误");
        }

        GuacamoleConfiguration config = new GuacamoleConfiguration();
        config.setProtocol(natInfo.getTunnetType().toString().toLowerCase());
        config.setParameter("color-depth", natService.getGuacDepthColor());
        config.setParameter("port", natInfo.getRemotePort().toString());
        config.setParameter("hostname", natInfo.getServerAddress());
        config.setParameter("password", natInfo.getPassword());

        if (natInfo.getTunnetType() == RemoteProtocolType.VNC) {
            // set vnc params
        } else if (natInfo.getTunnetType() == RemoteProtocolType.RDP) {
            config.setParameter("username", natInfo.getUsername());
            config.setParameter("ignore-cert", "true");
            config.setParameter("security", "any");
        } else if (natInfo.getTunnetType() == RemoteProtocolType.SSH) {
            config.setParameter("username", natInfo.getUsername());
        }

        GuacamoleSocket socket = new ConfiguredGuacamoleSocket(new InetGuacamoleSocket(GUAC_HOSTNAME, GUAC_PORT), config);
        pings.put(uuid, natInfo.getRemotePort());
        return new SimpleGuacamoleTunnel(socket);
    }

    /**
     * 获取url参数
     * 
     * @param queryString
     * @return
     */
    private Map<String, String> getParams(String queryString) {
        Map<String, String> ret = new HashMap<String, String>();
        String[] paramsList = queryString.split("&");
        for (String item : paramsList) {
            String[] keyval = item.split("=");
            ret.put(keyval[0], keyval[1]);
        }
        return ret;
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);

        Map<String, String> params = getParams(session.getQueryString());
        if (params.isEmpty()) {
            return;
        }

        String uuid = params.get("uuid");
        if (StringUtils.isEmpty(uuid)) {
            return;
        }

        pings.remove(uuid);
        natService.gcPortByUUID(uuid);
        fileService.clearFilesByUuid(uuid);
    }

    public static void init() {
        TaskEngine.getInstance().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    pings.forEach((uuid, port) -> {
                        natService.ping(port);
                    });
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }, 1000, 5000);
    }

}
