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
package com.loon.bridge.controller.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 *
 * @author nbflow
 */
@Controller
@RequestMapping("commons")
public class CommonsController extends BaseWebController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${download.client.path}")
    private String DOWNLOAD_CLIENT_PATH;

    @RequestMapping(value = "/client/download", method = {RequestMethod.GET})
    public void downloadClient(HttpServletRequest request, HttpServletResponse response) {

        try {
            String type = request.getParameter("type");
            File downloadfile = null;
            File files = new File(DOWNLOAD_CLIENT_PATH);
            for (File file : files.listFiles()) {
                if (file.getName().indexOf(type) != -1) {
                    downloadfile = file;
                    break;
                }
            }

            if (downloadfile == null) {
                response.sendRedirect("/download?error");
                return;
            }

            response.setContentType("application/force-download");
            response.addHeader("Content-Length", downloadfile.length() + "");
            response.addHeader("Content-Disposition", "attachment;fileName=" + downloadfile.getName());
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(downloadfile);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response.sendRedirect("/download?error");
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            try {
                response.sendRedirect("/download?error");
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
            }
        }
    }
}
