/**
 * 
 */
package com.loon.bridge.uda.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loon.bridge.core.db.BaseEntity;

/**
 * @author nbflow 企业用户
 *
 */
@TableName("t_enterprise_user")
public class EnterpriseUser extends BaseEntity {
	
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long eid;
    private Long uid;
    private String username;
    private Date ctime;
    
    /**
     * @return the eid
     */
    public Long getEid() {
        return eid;
    }
    
    /**
     * @param eid the eid to set
     */
    public void setEid(Long eid) {
        this.eid = eid;
    }
    
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
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
