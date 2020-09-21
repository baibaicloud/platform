/*
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2015-2018 loon.com. All rights reserved.
 * <p>
 * This software is the confidential and proprietary information of loon.
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the agreements you entered into with loon.
 * 
 * Modified history:
 *   loon  2016年11月4日 下午6:15:14  created
 */
package com.loon.bridge.core.db;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 所有实体类的抽象父类
 *
 * @author nbflow
 */
@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable {

    @TableId(type = IdType.ID_WORKER)
    private Long id;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

}
