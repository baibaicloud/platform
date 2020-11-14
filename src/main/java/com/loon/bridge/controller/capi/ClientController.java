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
package com.loon.bridge.controller.capi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.web.Result;
import com.loon.bridge.service.ack.ACKService;
import com.loon.bridge.service.commons.ServerInfoService;
import com.loon.bridge.service.device.DeviceService;
import com.loon.bridge.service.file.FileInfo;
import com.loon.bridge.service.file.FileService;
import com.loon.bridge.service.remotecpe.NatService;
import com.loon.bridge.service.session.Client;
import com.loon.bridge.service.session.Message;
import com.loon.bridge.service.session.SessionService;
import com.loon.bridge.service.session.UserSessionStatus;
import com.loon.bridge.service.tunnel.TunnelService;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.Device;

/**
 * 
 *
 * @author nbflow
 */
@Controller
public class ClientController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${fileupload.files.path}")
    private String UPLOAD_FILES_PATH;

    @Autowired
    private ServerInfoService serverInfoService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ACKService ackService;

    @Autowired
    private NatService natService;

    @Autowired
    private TunnelService tunnelService;

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/capi/client/commons", method = {RequestMethod.POST})
    @ResponseBody
    public Object commons(HttpServletRequest request) {

        try {
            Map<String, String> params = RequestUtil.getBody(request);
            Client client = getClientByToken(params);
            Object obj = null;
            switch (params.get("event")) {
                case "get_settings":
                    obj = userService.getSettings(client);
                    break;
                case "update_settings":
                    obj = userService.updateSettings(client, params);
                    break;
                case "remote_ready_ack":
                    obj = ackService.ack(client, params);
                    break;
                case "get_remote_status":
                    obj = natService.getNatInfoByDeviceSN(client);
                    break;
                case "bind_get_key":
                    obj = deviceService.getBindDeviceKey(client, params);
                    break;
                case "bind_ack_key":
                    obj = deviceService.bindDeviceKeyACK(client, params);
                    break;
                case "get_auth_bind_list":
                    obj = deviceService.getAuthBindList(client, params);
                    break;
                case "unauth_bind":
                    obj = deviceService.unbindDevice(client, params);
                    break;
                case "get_tunnel_list":
                    obj = tunnelService.getTunnelList(client, params);
                    break;
                case "files_delete":
                    obj = fileService.deleteFile(client, params);
                    break;
                default:
                    break;
            }
            return Result.Success(obj);
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/capi/file/upload", method = {RequestMethod.POST})
    @ResponseBody
    public Object upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            Client client = getClientByToken(request);
            String uuid = request.getParameter("uuid");
            if (StringUtils.isEmpty(uuid)) {
                return Result.Error("not find uuid");
            }
            fileService.uploadFileFromClient(client, file, uuid);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/capi/file/download", method = {RequestMethod.GET})
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {

        Client client = getClientByToken(request);
        String fileId = request.getParameter("fileId");
        String uuid = request.getParameter("uuid");
        FileInfo fileinfo = fileService.getFileInfo(client, fileId, uuid);
        if (fileinfo == null) {
            throw new BusinessException("not find file info");
        }

        File downloadfile = new File(UPLOAD_FILES_PATH + "/" + fileinfo.getFileId());
        if (!downloadfile.exists()) {
            throw new BusinessException("not exists file info");
        }
        try {

            response.addHeader("Content-Length", fileinfo.getFileSize() + "");
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(fileinfo.getFileName(), "UTF-8"));

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

    @RequestMapping(value = "/capi/client/status/get", method = {RequestMethod.POST})
    @ResponseBody
    public Object getUserStatus(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            Client client = getClientByToken(params);
            UserSessionStatus userStatus = sessionService.getUserStatus(client);
            return Result.Success(userStatus);
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/capi/client/message/get", method = {RequestMethod.POST})
    @ResponseBody
    public Object getMessage(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            Message msg = sessionService.getClientMessageByToken(params.get("token"));
            return Result.Success(msg);
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/capi/client/info/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object updateInfo(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            Client client = getClientByToken(params);

            Device update = new Device();
            update.setId(client.getDevice().getId());
            update.setCpu(params.get("cpu"));
            update.setMemony(params.get("memony"));
            update.setName(params.get("name"));
            update.setIp(params.get("ip"));

            deviceService.updateBySN(update);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/capi/client/login", method = {RequestMethod.POST})
    @ResponseBody
    public Object login(HttpServletRequest request) {

        try {
            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            String token = sessionService.makeUserToken(params.get("sn"));
            Map<String, Object> ret = new HashMap<String, Object>();
            ret.put("token", token);
            ret.put("server_info", serverInfoService.getBaseInfo());
            return Result.Success(ret);
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    /**
     * 获取设备登录信息
     * 
     * @param params
     * @return
     */
    private Client getClientByToken(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            throw new BusinessException("授权信息错误，请重新登录", 1);
        }

        Client client = this.sessionService.getClientByToken(params.get("token"));
        if (client == null) {
            throw new BusinessException("授权信息错误，请重新登录", 1);
        }

        return client;
    }

    private Client getClientByToken(HttpServletRequest request) {

        Client client = this.sessionService.getClientByToken(request.getParameter("token"));
        if (client == null) {
            throw new BusinessException("授权信息错误，请重新登录", 1);
        }

        return client;
    }

}
