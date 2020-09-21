/**
 * 
 */
package com.loon.bridge.uda.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loon.bridge.core.comenum.UserType;
import com.loon.bridge.core.db.BaseEntity;

/**
 * @author nbflow
 *
 */
@TableName("t_device_user")
public class DeviceUser extends BaseEntity {
	
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long deviceId;
    private Long nodeId;

    /**
     * uid or eid
     */
    private Long targetId;
    private String sn;
    private Date ctime;
    private UserType userType;

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
     * @return the uid
     */
    public Long getTargetId() {
        return targetId;
    }

    /**
     * @param targetId the uid to set
     */
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

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
     * @return the nodeId
     */
    public Long getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId the nodeId to set
     */
    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return the userType
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * @param userType the userType to set
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

}
