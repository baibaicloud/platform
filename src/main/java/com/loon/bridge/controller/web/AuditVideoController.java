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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.loon.bridge.core.aop.RightTarget;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.web.Result;
import com.loon.bridge.service.auditvideo.AuditVideoService;
import com.loon.bridge.uda.entity.AuditVideo;
import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
@Controller
@RequestMapping("/auditvideo")
public class AuditVideoController extends BaseWebController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AuditVideoService auditVideoService;

    @RequestMapping(value = "/list/get", method = {RequestMethod.GET})
    @ResponseBody
    @RightTarget(value = "browser_audit_video")
    public Object getTunnelList(HttpServletRequest request) {

        try {

            String username = request.getParameter("username");
            String deviceName = request.getParameter("deviceName");
            String stime = request.getParameter("stime");
            String etime = request.getParameter("etime");

            int draw = Integer.valueOf(request.getParameter("draw"));
            int size = Integer.valueOf(request.getParameter("length"));
            int page = Integer.valueOf(request.getParameter("start")) / size + 1;

            IPage<AuditVideo> pageInfo = auditVideoService.getAuditVideoList(username, deviceName, stime, etime, page, size);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            int autoindex = Integer.valueOf(request.getParameter("start")) + 1;
            for (AuditVideo item : pageInfo.getRecords()) {
                Map<String, Object> tempitem = new HashMap<String, Object>();
                tempitem.put("ctime", item.getCtime());
                tempitem.put("deviceName", item.getDeviceName());
                tempitem.put("username", item.getUsername());
                tempitem.put("uuid", item.getPath());
                tempitem.put("id", item.getId());
                tempitem.put("autoid", autoindex++);
                list.add(tempitem);
            }

            Map<String, Object> retInfo = new HashMap<String, Object>();
            retInfo.put("draw", draw);
            retInfo.put("recordsTotal", pageInfo.getTotal());
            retInfo.put("recordsFiltered", pageInfo.getTotal());
            retInfo.put("data", list);

            return retInfo;
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/video/del", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "browser_audit_video")
    public Object delVideo(HttpServletRequest request) {

        try {
            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            Long id = Long.valueOf(params.get("id"));

            auditVideoService.delAuditVideoByIdTargetid(user, id, RequestUtil.getSecurityUser().getTargetId());
            return new Result();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/file/get", method = {RequestMethod.GET})
    @RightTarget(value = "browser_audit_video")
    public void getFile(HttpServletRequest request, HttpServletResponse response) {

        try {
            String uuid = request.getParameter("uuid");
            File file = auditVideoService.getFileByuuidCuruser(uuid);

            if (file == null) {
                return;
            }

            response.setContentType("application/force-download");
            response.addHeader("Content-Length", file.length() + "");
            response.addHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(file.getName(), "UTF-8"));
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
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
        }
    }

}
