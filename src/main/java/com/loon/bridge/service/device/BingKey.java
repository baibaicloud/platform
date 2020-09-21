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
 *   Loon  2019年12月20日 下午10:34:43  created
 */
package com.loon.bridge.service.device;

import java.util.Date;

import com.loon.bridge.core.comenum.UserType;

/**
 * 
 *
 * @author nbflow
 */
public class BingKey {

    private Long deviceId;
    private String sn;
    private String priuuid;

    /**
     * 服务端下发给设备的UUID
     */
    private String uuid;
    private Integer code;
    private Date ctime;
    private Integer timeout;
    private Long targetId;
    private String targetName;
    private UserType userType;

    /**
     * @return the sn
     */
    public String getSn() {
        return sn;
    }

    /**
     * @param sn the sn to set
     */
    public void setSn(String sn) {
        this.sn = sn;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the ctime
     */
    public Date getCtime() {
        return ctime;
    }

    /**
     * @param ctime the ctime to set
     */
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    /**
     * @return the timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the deviceId
     */
    public Long getDeviceId() {
        return deviceId;
    }

    /**
     * @param deviceId the deviceId to set
     */
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return the targetId
     */
    public Long getTargetId() {
        return targetId;
    }

    /**
     * @param targetId the targetId to set
     */
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    /**
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * @return the priuuid
     */
    public String getPriuuid() {
        return priuuid;
    }

    /**
     * @param priuuid the priuuid to set
     */
    public void setPriuuid(String priuuid) {
        this.priuuid = priuuid;
    }

    /**
     * @return the targetName
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * @param targetName the targetName to set
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    /**
     * @return the authUserType
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * @param userType the authUserType to set
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

}
