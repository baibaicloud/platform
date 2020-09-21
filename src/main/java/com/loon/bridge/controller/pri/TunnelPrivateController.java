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
 *   Loon  2019年11月2日 下午11:58:03  created
 */
package com.loon.bridge.controller.pri;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.loon.bridge.service.session.Client;
import com.loon.bridge.service.session.SessionService;
import com.loon.bridge.service.tunnel.TunnelPrivateService;

/**
 * 
 *
 * @author nbflow
 */
@Controller
public class TunnelPrivateController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SessionService sessionService;

    @Autowired
    private TunnelPrivateService tunnelPrivateService;

    @RequestMapping(value = "/tunnel/port/check")
    @ResponseBody
    public Object checkProxy(HttpServletRequest request) {

        try {
            String proxyname = request.getParameter("proxyname");
            int port = Integer.valueOf(request.getParameter("remoteport"));
            return tunnelPrivateService.checkProxyPort(proxyname, port);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "error";
        }
    }

    @RequestMapping(value = "/tunnel/token/check")
    @ResponseBody
    public Object checkToken(HttpServletRequest request) {

        try {
            String token = request.getParameter("token");
            Client client = sessionService.getClientByToken(token);
            if (client == null) {
                logger.error("client proxy auth error,token:{}", token);
                return "error";
            }

            logger.info("client proxy auth success,token:{}", token);
            return "ok";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "error";
        }
    }

}
