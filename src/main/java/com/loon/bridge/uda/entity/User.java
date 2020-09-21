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
@TableName("t_user")
public class User extends BaseEntity {
	
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private UserType authUserType;

    private String orgid;
    private String email;
    private String username;
    private String selfname;
    private String password;
    private Date ctime;
    private Date lastdate;

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

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
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
     * @return the lastdate
     */
    public Date getLastdate() {
        return lastdate;
    }

    /**
     * @param lastdate the lastdate to set
     */
    public void setLastdate(Date lastdate) {
        this.lastdate = lastdate;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the orgid
     */
    public String getOrgid() {
        return orgid;
    }

    /**
     * @param orgid the orgid to set
     */
    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    /**
     * @return the selfname
     */
    public String getSelfname() {
        return selfname;
    }

    /**
     * @param selfname the selfname to set
     */
    public void setSelfname(String selfname) {
        this.selfname = selfname;
    }

    /**
     * @return the authUserType
     */
    public UserType getAuthUserType() {
        return authUserType;
    }

    /**
     * @param authUserType the authUserType to set
     */
    public void setAuthUserType(UserType authUserType) {
        this.authUserType = authUserType;
    }
	
}
