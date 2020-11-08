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
 *   nbflow  2020年11月2日 下午9:22:35  created
 */
package com.loon.bridge.service.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.TaskEngine;
import com.loon.bridge.core.utils.Util;
import com.loon.bridge.service.remotecpe.NatInfo;
import com.loon.bridge.service.remotecpe.NatService;
import com.loon.bridge.service.session.Client;
import com.loon.bridge.service.session.Message;
import com.loon.bridge.service.session.SessionService;
import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class FileService implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${fileupload.files.path}")
    private String UPLOAD_FILES_PATH;

    @Value("${fileupload.timeout.max.sec}")
    private Integer FILE_TIMEOUT_MAX;

    @Value("${fileupload.timeout.interval.sec}")
    private Integer FILE_TIMEOUT_INTERVAL;

    /** 文件列表 */
    private Map<String, FileInfo> files = new ConcurrentHashMap<String, FileInfo>();

    /** 文件列表,针对客户端文件上传 ,key:uuid */
    private Map<String, List<FileInfo>> filesFromClient = new ConcurrentHashMap<String, List<FileInfo>>();

    @Autowired
    private SessionService sessionService;

    @Autowired
    private NatService natService;

    @Override
    public void afterPropertiesSet() throws Exception {

        FileUtils.cleanDirectory(new File(UPLOAD_FILES_PATH));

        TaskEngine.getInstance().schedule(new TimerTask() {
            @Override
            public void run() {
                checkTimeout();
            }
        }, 0, FILE_TIMEOUT_INTERVAL * 1000);
    }

    private void checkTimeout() {
        Date curtime = new Date();
        List<String> delids = new ArrayList<String>();
        for (String key : files.keySet()) {
            FileInfo file = files.get(key);
            if (file == null) {
                delids.add(key);
                continue;
            }

            long temptime = (curtime.getTime() - file.getCtime().getTime()) / 1000;
            if (temptime > FILE_TIMEOUT_MAX) {
                delids.add(key);
            }
        }
        
        delids.forEach(key -> {
            deleteFileHandler(files.get(key));
        });

        logger.info("del files:{}", delids);
    }

    /**
     * 上传文件
     * 
     * @param user
     * @param file
     */
    public void uploadFile(User user, String uuid, MultipartFile multipartFile) {
        try {

            NatInfo natInfo = natService.getPortInfoByUUID(user.getUsername(), uuid);
            if (natInfo == null) {
                throw new BusinessException("uuid 非法");
            }

            FileInfo fileinfo = createFileInfo(multipartFile, uuid);
            files.put(fileinfo.getFileId(), fileinfo);

            Map<String, Object> body = new HashMap<String, Object>();
            body.put("fileId", fileinfo.getFileId());
            body.put("fileName", fileinfo.getFileName());
            body.put("fileSize", fileinfo.getFileSize());

            Message msg = new Message();
            msg.setEvent(Message.Type.DOWNLOAD_FILE);
            msg.setBody(body);
            sessionService.sendMsg(natInfo.getDeviceId(), msg);

            logger.info("oper {} upload file success,uuid:{}", user.getId(), fileinfo.getFileId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 根据MultipartFile创建文件信息
     * 
     * @param multipartFile
     * @return
     * @throws IOException
     */
    private FileInfo createFileInfo(MultipartFile multipartFile, String uuid) throws IOException {
        String fileId = Util.UUID();
        FileInfo fileinfo = new FileInfo();
        fileinfo.setCtime(new Date());
        fileinfo.setFileId(fileId);
        fileinfo.setFileName(multipartFile.getResource().getFilename());
        fileinfo.setFileSize(multipartFile.getSize());
        fileinfo.setUuid(uuid);
        Files.copy(multipartFile.getInputStream(), Paths.get(UPLOAD_FILES_PATH + "/" + fileId));
        return fileinfo;
    }

    private void deleteFileHandler(FileInfo fileInfo) {

        if (fileInfo == null) {
            return;
        }

        File tempfile = new File(UPLOAD_FILES_PATH + "/" + fileInfo.getFileId());
        tempfile.delete();
        files.remove(fileInfo.getFileId());
    }

    /**
     * 客户端删除文件
     * 
     * @param client
     * @param params
     * @return
     */
    public Object deleteFile(Client client, Map<String, String> params) {
        String fileId = params.get("fileId");
        String uuid = params.get("uuid");
        if (StringUtils.isEmpty(fileId) || StringUtils.isEmpty(uuid)) {
            return null;
        }
        
        FileInfo fileinfo = files.get(fileId);
        if(fileinfo == null) {
            return null;
        }
        
        if (!fileinfo.getUuid().equals(uuid)) {
            return null;
        }

        deleteFileHandler(fileinfo);
        return null;
    }

    public FileInfo getFileInfo(Client client, String fileId, String uuid) {

        FileInfo fileinfo = files.get(fileId);
        if (fileinfo == null) {
            return null;
        }

        if (!fileinfo.getUuid().equals(uuid)) {
            return null;
        }

        return files.get(fileId);
    }

    public void uploadFileFromClient(Client client, MultipartFile file, String uuid) {
        try {
            List<FileInfo> list = filesFromClient.get(uuid);
            if (list == null) {
                list = new CopyOnWriteArrayList<FileInfo>();
                filesFromClient.put(uuid, list);
            }

            FileInfo item = createFileInfo(file, uuid);
            list.add(item);
            logger.info("client upload file success,id:{}, fileid:{}", client.getToken(), item.getFileId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public List<FileInfo> getFileInfoList(User user, String uuid) {
        NatInfo natInfo = natService.getPortInfoByUUID(user.getUsername(), uuid);
        if (natInfo == null) {
            throw new BusinessException("uuid 非法");
        }
        
        List<FileInfo> list = filesFromClient.get(uuid);
        if (list == null) {
            return new ArrayList<FileInfo>();
        }
        return list;
    }

    public void deleteFileInfo(User user, String uuid, String fileId) {
        NatInfo natInfo = natService.getPortInfoByUUID(user.getUsername(), uuid);
        if (natInfo == null) {
            throw new BusinessException("uuid 非法");
        }

        List<FileInfo> list = filesFromClient.get(uuid);
        for (FileInfo item : list) {
            if (item.getFileId().equals(fileId) && item.getUuid().equals(uuid)) {
                list.remove(item);
                File tempfile = new File(UPLOAD_FILES_PATH + "/" + item.getFileId());
                if (tempfile.exists()) {
                    tempfile.delete();
                }
                logger.info("user {} del file success", user.getId(), fileId);
                break;
            }
        }
    }

    public FileInfo getFileInfoByUserFileidUuid(User user, String fileId, String uuid) {
        NatInfo natInfo = natService.getPortInfoByUUID(user.getUsername(), uuid);
        if (natInfo == null) {
            throw new BusinessException("uuid 非法");
        }

        List<FileInfo> list = filesFromClient.get(uuid);
        for (FileInfo item : list) {
            if (item.getFileId().equals(fileId) && item.getUuid().equals(uuid)) {
                return item;
            }
        }

        return null;
    }

    /**
     * 清空远程会话的所有文件
     * 
     * @param uuid
     */
    public void clearFilesByUuid(String uuid) {
        List<FileInfo> list = filesFromClient.remove(uuid);
        if (list == null || list.isEmpty()) {
            return;
        }

        for (FileInfo item : list) {
            File tempfile = new File(UPLOAD_FILES_PATH + "/" + item.getFileId());
            if (tempfile.exists()) {
                tempfile.delete();
                logger.info("del all file by uuid:{},fileid:{}", uuid, item.getFileId());
            }
        }
        logger.info("clean all file by uuid:{}", uuid);

    }

}
