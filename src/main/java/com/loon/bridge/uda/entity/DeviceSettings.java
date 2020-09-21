/**
 * 
 */
package com.loon.bridge.uda.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loon.bridge.core.comenum.Status;
import com.loon.bridge.core.db.BaseEntity;

/**
 * @author nbflow
 *
 */
@TableName("t_device_settings")
public class DeviceSettings extends BaseEntity {
	
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long deviceId;
    private Status autoRemote;
    private Status autoRun;

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
     * @return the autoRemote
     */
    public Status getAutoRemote() {
        return autoRemote;
    }

    /**
     * @param autoRemote the autoRemote to set
     */
    public void setAutoRemote(Status autoRemote) {
        this.autoRemote = autoRemote;
    }

    /**
     * @return the autoRun
     */
    public Status getAutoRun() {
        return autoRun;
    }

    /**
     * @param autoRun the autoRun to set
     */
    public void setAutoRun(Status autoRun) {
        this.autoRun = autoRun;
    }

}
