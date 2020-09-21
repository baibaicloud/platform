/**
 * 
 */
package com.loon.bridge.uda.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loon.bridge.core.db.BaseEntity;

/**
 * @author nbflow
 *
 */
@TableName("t_device_last_use")
public class DeviceUserLastUse extends BaseEntity {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long uid;
    private Long deviceUserId;
    private Date lasttime;

    /**
     * @return the uid
     */
    public Long getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * @return the deviceUserId
     */
    public Long getDeviceUserId() {
        return deviceUserId;
    }

    /**
     * @param deviceUserId the deviceUserId to set
     */
    public void setDeviceUserId(Long deviceUserId) {
        this.deviceUserId = deviceUserId;
    }

    /**
     * @return the lasttime
     */
    public Date getLasttime() {
        return lasttime;
    }

    /**
     * @param lasttime the lasttime to set
     */
    public void setLasttime(Date lasttime) {
        this.lasttime = lasttime;
    }

}
