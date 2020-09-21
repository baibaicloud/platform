/**
 * 
 */
package com.loon.bridge.uda.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loon.bridge.core.comenum.Status;
import com.loon.bridge.core.db.BaseEntity;

/**
 * @author nbflow 企业角色
 *
 */
@TableName("t_enterprise_role")
public class EnterpriseRole extends BaseEntity {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long eid;
    private String title;
    private Status isdefault;
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
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @return the isdefault
     */
    public Status getIsdefault() {
        return isdefault;
    }

    /**
     * @param isdefault the isdefault to set
     */
    public void setIsdefault(Status isdefault) {
        this.isdefault = isdefault;
    }

}
