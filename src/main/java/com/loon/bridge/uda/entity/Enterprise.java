/**
 * 
 */
package com.loon.bridge.uda.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loon.bridge.core.db.BaseEntity;

/**
 * @author nbflow 企业
 */
@TableName("t_enterprise")
public class Enterprise extends BaseEntity {
	
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private Long cuid;
    private Date ctime;
    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @return the cuid
     */
    public Long getCuid() {
        return cuid;
    }
    
    /**
     * @param cuid the cuid to set
     */
    public void setCuid(Long cuid) {
        this.cuid = cuid;
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

}
