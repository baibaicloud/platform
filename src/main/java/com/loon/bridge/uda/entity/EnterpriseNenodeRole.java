/**
 * 
 */
package com.loon.bridge.uda.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loon.bridge.core.db.BaseEntity;

/**
 * @author nbflow 企业角色对应的节点
 */
@TableName("t_enterprise_nenode_role")
public class EnterpriseNenodeRole extends BaseEntity {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long eid;
    private Long nenodeId;
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
     * @return the nenodeId
     */
    public Long getNenodeId() {
        return nenodeId;
    }

    /**
     * @param nenodeId the nenodeId to set
     */
    public void setNenodeId(Long nenodeId) {
        this.nenodeId = nenodeId;
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


}
