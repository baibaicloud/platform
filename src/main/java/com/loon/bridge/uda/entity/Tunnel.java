/**
 * 
 */
package com.loon.bridge.uda.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loon.bridge.core.comenum.Status;
import com.loon.bridge.core.comenum.TunnelType;
import com.loon.bridge.core.db.BaseEntity;

/**
 * @author nbflow
 *
 */
@TableName("t_tunnel")
public class Tunnel extends BaseEntity {
	
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long uid;
    private Long deviceId;
    private String localIp;
    private Integer localPort;
    private Integer remotePort;
    private String remarks;
    private TunnelType tunnetType;
    private Status isactive;
    private Date ctime;

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
     * @return the tunnetType
     */
    public TunnelType getTunnetType() {
        return tunnetType;
    }

    /**
     * @param tunnetType the tunnetType to set
     */
    public void setTunnetType(TunnelType tunnetType) {
        this.tunnetType = tunnetType;
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
     * @return the localPort
     */
    public Integer getLocalPort() {
        return localPort;
    }

    /**
     * @param localPort the localPort to set
     */
    public void setLocalPort(Integer localPort) {
        this.localPort = localPort;
    }

    /**
     * @return the remotePort
     */
    public Integer getRemotePort() {
        return remotePort;
    }

    /**
     * @param remotePort the remotePort to set
     */
    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
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
     * @return the isactive
     */
    public Status getIsactive() {
        return isactive;
    }

    /**
     * @param isactive the isactive to set
     */
    public void setIsactive(Status isactive) {
        this.isactive = isactive;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the localIp
     */
    public String getLocalIp() {
        return localIp;
    }

    /**
     * @param localIp the localIp to set
     */
    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

}
