/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2008-2014 nbflow. All rights reserved.
 * <p>
 * This software is the confidential and proprietary information of nbflow.
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the agreements you entered into with nbflow.
 * 
 * Modified history:
 *   nbflow  2020年2月23日 下午3:19:37  created
 */
package com.loon.bridge.core.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 *
 * @author nbflow
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RightTarget {

    /**
     * 权限列表
     * 
     * @return right list
     */
    String value() default "";
}
