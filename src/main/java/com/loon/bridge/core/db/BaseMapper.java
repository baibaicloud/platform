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
 *   loon  2016年11月4日 下午6:13:44  created
 */
package com.loon.bridge.core.db;

/**
 * mapper的基础接口，定义了基本的CRUD操作；该接口的子接口都直接映射到mybatis的mapping文件，并由mybatis扫描生成代理实现
 * <p>
 * 该接口继承自mybaits-plus定义的接口{@link com.baomidou.mybatisplus.mapper.BaseMapper}，提供了基本的CRUD方法。<br>
 * 系统需要支持分布式部署，id需要确保全局唯一，BaseMapper的id泛型使用Long类型，统一由{@code IdWorker}自动生成。
 * 
 * @param <T> 实体类型
 * @author nbflow
 */
public interface BaseMapper<T extends BaseEntity> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

}
