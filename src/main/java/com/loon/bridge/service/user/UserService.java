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
 *   Loon  2019年11月3日 下午10:05:36  created
 */
package com.loon.bridge.service.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.loon.bridge.core.comenum.Status;
import com.loon.bridge.core.comenum.UserType;
import com.loon.bridge.core.commo.AuthInfo;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.TaskEngine;
import com.loon.bridge.core.utils.Util;
import com.loon.bridge.service.device.DeviceService;
import com.loon.bridge.service.enterprise.EnterpriseService;
import com.loon.bridge.service.nenode.NenodeService;
import com.loon.bridge.service.session.Client;
import com.loon.bridge.uda.entity.DeviceSettings;
import com.loon.bridge.uda.entity.Enterprise;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.uda.mapper.UserMapper;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class UserService implements InitializingBean {

    public static final int USERNAME_MIN_LEN = 6;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Value("${user.reset.password.timeout}")
    private Integer RESET_PASSWORD_TIMEOUT;

    @Value("${spring.mail.username}")
    private String SPRING_MAIL_USERNAME;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private NenodeService nenodeService;

    @Autowired
    private EnterpriseService enterpriseService;

    /**
     * 存放临时重置密码key
     */
    private Map<String, RestPasswordInfo> resetPassword = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        TaskEngine.getInstance().schedule(new TimerTask() {
            @Override
            public void run() {
                resetPasswordTimeout();
            }
        }, 1000, 10000);
    }

    /**
     * 检测过期key
     */
    private void resetPasswordTimeout() {
        List<String> delList = new ArrayList<>();
        long curtime = System.currentTimeMillis();
        resetPassword.forEach((key, info) -> {
            long temp = curtime - info.getCtime().getTime();
            temp = temp / 1000;
            if (temp > RESET_PASSWORD_TIMEOUT) {
                delList.add(key);
            }
        });

        delList.forEach((key) -> {
            resetPassword.remove(key);
            logger.info("reste password timeout,key:{}", key);
        });
    }

    /**
     * 
     * 
     * @param user
     * @return
     */
    public User signup(Long enterpriseId, User user) {

        checkUserInfo(user);

        User tempuser = this.getUserByEmail(user.getEmail());
        if (tempuser != null) {
            throw new BusinessException("邮箱已注册");
        }

        tempuser = this.getUserByUsername(user.getUsername());
        if (tempuser != null) {
            throw new BusinessException("用户名称已被使用");
        }

        String password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(password);
        user.setCtime(new Date());
        user.setOrgid(UUID.randomUUID().toString());
        userMapper.insert(user);

        nenodeService.createNenodeDefault(user);

        logger.info("sigup user success,username:{},email:{}", user.getUsername(), user.getEmail());

        return user;
    }

    /**
     * 检测用户参数
     * 
     * @param user
     */
    private void checkUserInfo(User user) {
        if (StringUtils.isEmpty(user.getEmail()) || user.getEmail().indexOf("@") == -1) {
            throw new BusinessException("参数错误");
        }

        if (StringUtils.isEmpty(user.getUsername()) || user.getUsername().length() < USERNAME_MIN_LEN) {
            throw new BusinessException("参数错误");
        }

        if (StringUtils.isEmpty(user.getPassword()) || user.getPassword().length() < USERNAME_MIN_LEN) {
            throw new BusinessException("参数错误");
        }
    }

    /**
     * 
     * 
     * @param email
     * @return
     */
    public User getUserByEmail(String email) {
        User user = new User();
        user.setEmail(email);
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.setEntity(user);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 
     * 
     * @param username
     * @return
     */
    public User getUserByUsername(String username) {
        User user = new User();
        user.setUsername(username);
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.setEntity(user);
        return userMapper.selectOne(queryWrapper);
    }


    /**
     * 根据id获取用户信息
     * 
     * @param id
     * @return
     */
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 更新最后登录时间
     * 
     * @param uid
     */
    public void updateLastDate(Long uid) {
        User entity = new User();
        entity.setId(uid);
        entity.setLastdate(new Date());
        userMapper.updateById(entity);
    }

    /**
     * 更新用户信息
     * 
     * @param curuser
     * @param oldPassword
     * @param updateUser
     */
    public void updateUserInfo(User curuser, String oldPassword, User updateUser) {

        User entity = new User();
        if (!StringUtils.isEmpty(oldPassword)) {
            if (StringUtils.isEmpty(updateUser.getPassword()) || updateUser.getPassword().length() < 6) {
                throw new BusinessException("参数错误");
            }

            if (!curuser.getPassword().equals(oldPassword)) {
                throw new BusinessException("旧密码错误");
            }
            entity.setPassword(updateUser.getPassword());
        }

        entity.setId(curuser.getId());
        entity.setSelfname(updateUser.getSelfname());

        userMapper.updateById(entity);
        logger.info("update user info success,uid:{}", curuser.getId());
    }

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送重置密码连接
     * 
     * @param user
     */
    public void sendResetPasswordEmail(String email) {

        User user = this.getUserByEmail(email);
        if (user == null) {
            throw new BusinessException("此邮箱未注册账号");
        }

        String uuid = Util.UUID();
        resetPassword.put(uuid, new RestPasswordInfo(user));

        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());// 收信人
            message.setSubject("密码重置");// 主题
            message.setText("验证码:" + uuid + "\n验证码" + (RESET_PASSWORD_TIMEOUT / 60) + "分钟后过期");
            message.setFrom(SPRING_MAIL_USERNAME);// 发信人
            mailSender.send(message);
            logger.info("send reset key to email success,uid:{},uuid:{},url:{}", user.getId(), uuid);
        } catch (MailException e) {
            logger.error(e.getMessage(), e);
            resetPassword.remove(uuid);
            throw new BusinessException("重置密码失败，请重试");
        }
    }

    /**
     * 根据uuid获取密码重置信息
     * 
     * @param uuid
     * @return
     */
    public RestPasswordInfo getRestPasswordInfo(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            return null;
        }
        return resetPassword.get(uuid);
    }

    /**
     * 重置密码
     * 
     * @param user
     * @param uuid
     * @param password
     */
    public void sendResetPasswordACK(String uuid, String password) {

        if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(password)) {
            throw new BusinessException("参数错误");
        }

        RestPasswordInfo restPasswordInfo = resetPassword.get(uuid);
        if (restPasswordInfo == null) {
            throw new BusinessException("参数错误");
        }
        
        User entity = new User();
        entity.setId(restPasswordInfo.getUser().getId());
        entity.setPassword(password);
        userMapper.updateById(entity);
        resetPassword.remove(uuid);

        logger.info("user reset password ack success,uid:{},uuid:{}", restPasswordInfo.getUser().getId(), uuid);
    }

    /**
     * 获取用户配置信息
     * 
     * @param user
     * @param sn
     * @return
     */
    public Object getSettings(Client client) {

        DeviceSettings settings = deviceService.getDeviceSettings(client.getSn());

        Map<String, Object> ret = new HashMap<>();
        ret.put("autoRemote", settings.getAutoRemote());
        ret.put("autoRun", settings.getAutoRun());
        return ret;
    }

    /**
     * 更新用户配置
     * 
     * @param user
     * @param sn
     * @param params
     * @return
     */
    public Object updateSettings(Client client, Map<String, String> params) {
        DeviceSettings deviceSettings = new DeviceSettings();
        deviceSettings.setAutoRemote(Status.valueOf(params.get("autoRemote")));
        deviceSettings.setAutoRun(Status.valueOf(params.get("autoRun")));
        deviceService.updateDeviceSettings(client.getSn(), deviceSettings);
        return null;
    }

    /**
     * 获取用户切面（个人，企业）类型
     * 
     * @param user
     */
    public List<AuthInfo> getSwitchInfoList(User user) {
        List<AuthInfo> list = new ArrayList<AuthInfo>();

        AuthInfo temp = new AuthInfo();
        temp.setName("个人");
        temp.setId(user.getId());
        temp.setType(UserType.SOLE);
        list.add(temp);

        List<Enterprise> enlist = enterpriseService.getEnterpriseListByUid(user.getId());
        enlist.forEach((item) -> {
            AuthInfo tempitem = new AuthInfo();
            tempitem.setName(item.getTitle());
            tempitem.setId(item.getId());
            tempitem.setType(UserType.ENTERPRISE);
            list.add(tempitem);
        });

        return list;
    }

    /**
     * 根据用户id列表获取用户
     * 
     * @param uids
     * @return
     */
    public List<User> getUserListByIds(List<Long> uids) {
        List<User> users = userMapper.selectBatchIds(uids);
        users.forEach((item) -> {
            item.setPassword(null);
        });

        return users;
    }

}
