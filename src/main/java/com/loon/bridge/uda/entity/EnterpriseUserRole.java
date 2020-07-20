/**
 * 
 */
package com.loon.bridge.uda.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loon.bridge.core.db.BaseEntity;

/**
 * @author nbflow 企业用户对应的角色
 *
 */
@TableName("t_enterprise_user_role")
public class EnterpriseUserRole extends BaseEntity {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long eid;
    private Long uid;
    private Long roleId;
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
     * @return the roleId
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * @param roleId the roleId to set
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}
