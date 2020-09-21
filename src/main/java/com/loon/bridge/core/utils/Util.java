/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2008-2014 loon. All rights reserved.
 * <p>
 * This software is the confidential and proprietary information of loon.
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the agreements you entered into with loon.
 * 
 * Modified history:
 *   Loon  2019年11月14日 下午11:25:17  created
 */
package com.loon.bridge.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * 
 *
 * @author nbflow
 */
public class Util {

    /**
     * 临时自增编号
     */
    private volatile static int autoindex = 10000;

    /**
     * 字符串转Long List
     * 
     * @param ids
     * @return
     */
    public static List<Long> toArrayLong(String ids) {
        List<Long> retIds = new ArrayList<Long>();
        if (StringUtils.isEmpty(ids)) {
            return retIds;
        }

        String[] tempids = ids.split(",");
        for (String item : tempids) {
            if (StringUtils.isEmpty(item)) {
                continue;
            }
            retIds.add(Long.valueOf(item));
        }

        return retIds;
    }

    /**
     * 创建UUID
     * 
     * @return
     */
    public static String UUID() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 创建UUID
     * 
     * @return
     */
    public static String UUID8() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * 获取临时自增id
     * 
     * @return
     */
    public synchronized static String getAutoIndex() {
        if (autoindex > 999999) {
            autoindex = 10000;
        }
        return String.valueOf(++autoindex);
    }

}
