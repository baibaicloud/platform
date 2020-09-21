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
 *   Loon  2019年11月16日 下午9:12:13  created
 */
package com.loon.bridge.config;

/**
 * 
 *
 * @author nbflow
 */
public class Constants {

    /** 日期格式化模板. */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** The default encoding */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /** * The Constant SECOND. */
    public static final long SECOND = 1000;

    /** * The Constant MINUTE. */
    public static final long MINUTE = 60 * SECOND;

    /** * The Constant HOUR. */
    public static final long HOUR = 60 * MINUTE;

    /** * The Constant DAY. */
    public static final long DAY = 24 * HOUR;

    /** * The Constant WEEK. */
    public static final long WEEK = 7 * DAY;
    
    /** ack timeout */
    public static final int ACK = 8;

    /** 默认id */
    public static final Long DEFAULT_ID = -1L;
}
