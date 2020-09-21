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
 *   Loon  2019年12月14日 下午4:20:56  created
 */
package com.loon.bridge.service.ack;

/**
 * 
 *
 * @author nbflow
 */
public interface FutureTimeoutCallback {

    void run(Object obj);
}
