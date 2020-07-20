/**
 * 
 */
package com.loon.bridge.uda.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loon.bridge.core.db.BaseEntity;

/**
 * @author nbflow
 *
 */
@TableName("t_nenode")
public class Nenode extends BaseEntity {
	
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private String title;
    private Long operId;
    private Long pid;
    private String path;
    private Integer orderIndex;

    /**
     * @return the operId
     */
    public Long getOperId() {
        return operId;
    }

    /**
     * @param operId the operId to set
     */
    public void setOperId(Long operId) {
        this.operId = operId;
    }

    /**
     * @return the pid
     */
    public Long getPid() {
        return pid;
    }

    /**
     * @param pid the pid to set
     */
    public void setPid(Long pid) {
        this.pid = pid;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
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
     * @return the orderIndex
     */
    public Integer getOrderIndex() {
        return orderIndex;
    }

    /**
     * @param orderIndex the orderIndex to set
     */
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

}
