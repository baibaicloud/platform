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
 *   nbflow  2020年4月27日 下午11:19:22  created
 */
package com.loon.bridge.service.commons;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class ServerInfoService {

    @Value("${frptunnel.server.address}")
    private String FRP_TUNNEL_SERVER_ADDRESS;

    @Value("${frptunnel.server.port}")
    private Integer FRP_TUNNEL_SERVER_PORT;

    public Map<String, Object> getBaseInfo() {
        Map<String, Object> retInfo = new HashMap<String, Object>();
        retInfo.put("frp_tunnel_server_address", FRP_TUNNEL_SERVER_ADDRESS);
        retInfo.put("frp_tunnel_server_port", FRP_TUNNEL_SERVER_PORT);
        return retInfo;
    }
}
