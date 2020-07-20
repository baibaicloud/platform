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
 *   Administrator  2019年9月21日 上午9:49:08  created
 */
package com.loon.bridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.loon.bridge.service.guacamole.LoonServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    @Bean
    public LoonServerEndpointExporter serverEndpointExporter() {
        return new LoonServerEndpointExporter();
    }

}