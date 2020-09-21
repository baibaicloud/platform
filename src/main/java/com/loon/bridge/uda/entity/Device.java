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
@TableName("t_device")
public class Device extends BaseEntity {
	
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private Boolean isonline;

    @TableField(exist = false)
    private Boolean isremote;

    @TableField(exist = false)
    private String usename;

    private String sn;
    private String ip;
    private String name;
    private String devdesc;
    private String cpu;
    private String memony;
    private Date ctime;

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the cpu
     */
    public String getCpu() {
        return cpu;
    }

    /**
     * @param cpu the cpu to set
     */
    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    /**
     * @return the memony
     */
    public String getMemony() {
        return memony;
    }

    /**
     * @param memony the memony to set
     */
    public void setMemony(String memony) {
        this.memony = memony;
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
     * @return the isonline
     */
    public Boolean getIsonline() {
        return isonline;
    }

    /**
     * @param isonline the isonline to set
     */
    public void setIsonline(Boolean isonline) {
        this.isonline = isonline;
    }

    /**
     * @return the devdesc
     */
    public String getDevdesc() {
        return devdesc;
    }

    /**
     * @param devdesc the devdesc to set
     */
    public void setDevdesc(String devdesc) {
        this.devdesc = devdesc;
    }

    /**
     * @return the isremote
     */
    public Boolean getIsremote() {
        return isremote;
    }

    /**
     * @param isremote the isremote to set
     */
    public void setIsremote(Boolean isremote) {
        this.isremote = isremote;
    }

    /**
     * @return the usename
     */
    public String getUsename() {
        return usename;
    }

    /**
     * @param usename the usename to set
     */
    public void setUsename(String usename) {
        this.usename = usename;
    }

}
