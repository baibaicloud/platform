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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.web.Result;
import com.loon.bridge.service.file.FileInfo;
import com.loon.bridge.service.file.FileService;
import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
@Controller
@RequestMapping("file")
public class FileController extends BaseWebController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${fileupload.files.path}")
    private String UPLOAD_FILES_PATH;

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    @ResponseBody
    public Object upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        try {
            User user = getCurUser();
            String uuid = request.getParameter("uuid");
            fileService.uploadFile(user, uuid, file);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/download", method = {RequestMethod.GET})
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {

        try {

            User user = getCurUser();
            String fileId = request.getParameter("fileId");
            String uuid = request.getParameter("uuid");
            FileInfo fileinfo = fileService.getFileInfoByUserFileidUuid(user, fileId, uuid);
            if (fileinfo == null) {
                throw new BusinessException("not find file info");
            }

            File downloadfile = new File(UPLOAD_FILES_PATH + "/" + fileinfo.getFileId());
            if (!downloadfile.exists()) {
                throw new BusinessException("not exists file info");
            }

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

    @RequestMapping(value = "/list/get", method = {RequestMethod.POST})
    @ResponseBody
    public Object getFileInfoList(HttpServletRequest request) {

        try {
            User user = getCurUser();
            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            String uuid = params.get("uuid");
            List<FileInfo> list = fileService.getFileInfoList(user, uuid);
            return Result.Success(list);
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.POST})
    @ResponseBody
    public Object deleteFileInfo(HttpServletRequest request) {

        try {
            User user = getCurUser();
            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            String uuid = params.get("uuid");
            String fileId = params.get("fileId");

            fileService.deleteFileInfo(user, uuid, fileId);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

}
