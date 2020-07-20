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
 *   Administrator  2019年9月21日 上午10:54:09  created
 */
package com.loon.bridge.service.guacamole;

import javax.websocket.server.ServerContainer;

import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 
 *
 * @author nbflow
 */
public class LoonServerEndpointExporter extends ServerEndpointExporter {

    public ServerContainer getServerContainer() {
        return super.getServerContainer();
    }
}
