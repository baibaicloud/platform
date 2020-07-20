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
 *   Loon  2019年11月30日 下午11:23:54  created
 */
package com.loon.bridge.service.user;

import java.util.Date;

import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
public class RestPasswordInfo {

    private User user;
    private Date ctime;

    public RestPasswordInfo(User user) {
        this.user = user;
        ctime = new Date();
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
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

}
