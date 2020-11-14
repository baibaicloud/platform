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
 *   nbflow  2020年10月28日 下午8:30:24  created
 */
package com.loon.bridge.config;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

/**
 * 
 *
 * @author nbflow
 */
@Configuration
public class TomcatConfig {

    @Value("${spring.server.MaxFileSize}")
    private String MaxFileSize;

    @Value("${spring.server.MaxRequestSize}")
    private String MaxRequestSize;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 单个数据大小
        factory.setMaxFileSize(DataSize.of(Long.parseLong(MaxFileSize), DataUnit.MEGABYTES)); // KB,MB
        /// 总上传数据大小
        factory.setMaxRequestSize(DataSize.of(Long.parseLong(MaxRequestSize), DataUnit.MEGABYTES));
        return factory.createMultipartConfig();
    }
}
