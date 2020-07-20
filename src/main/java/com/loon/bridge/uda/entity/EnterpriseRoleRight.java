/**
 * 
 */
package com.loon.bridge.uda.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loon.bridge.core.db.BaseEntity;

/**
 * @author nbflow 企业角色对应的权限
 *
 */
@TableName("t_enterprise_role_right")
public class EnterpriseRoleRight extends BaseEntity {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long eid;
    private Long roleId;
    private String target;
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
     * @return the target
     */
    public String getTarget() {
        return target;
    }
    
    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

}
