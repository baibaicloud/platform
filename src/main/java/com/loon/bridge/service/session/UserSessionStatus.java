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
 *   Loon  2019年12月12日 下午10:07:04  created
 */
package com.loon.bridge.service.session;

/**
 * 
 *
 * @author nbflow
 */
public class UserSessionStatus {

    private Boolean remoteIng = false;
    private String remoteSelfname;

    /**
     * @return the remoteIng
     */
    public Boolean getRemoteIng() {
        return remoteIng;
    }

    /**
     * @param remoteIng the remoteIng to set
     */
    public void setRemoteIng(Boolean remoteIng) {
        this.remoteIng = remoteIng;
    }

    /**
     * @return the remoteSelfname
     */
    public String getRemoteSelfname() {
        return remoteSelfname;
    }

    /**
     * @param remoteSelfname the remoteSelfname to set
     */
    public void setRemoteSelfname(String remoteSelfname) {
        this.remoteSelfname = remoteSelfname;
    }

}
