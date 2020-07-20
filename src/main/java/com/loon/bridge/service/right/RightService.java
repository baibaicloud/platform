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
 *   nbflow  2020年2月8日 下午8:38:37  created
 */
package com.loon.bridge.service.right;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loon.bridge.core.comenum.UserType;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.service.enterprise.EnterpriseService;
import com.loon.bridge.uda.entity.EnterpriseRole;
import com.loon.bridge.uda.entity.Right;
import com.loon.bridge.uda.mapper.RightMapper;
import com.loon.bridge.web.security.SecurityUser;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class RightService {

    @Autowired
    private RightMapper rightMapper;

    @Autowired
    private EnterpriseService enterpriseService;

    public List<Right> getRightList() {
        return rightMapper.selectList(null);
    }

    /**
     * 获取用户权限列表
     * 
     * @param type
     * @param uid
     * @return
     */
    public List<String> getEnterpriseRoleRightListByCurUser(Long uid) {
        SecurityUser securityUser = RequestUtil.getSecurityUser();
        if (securityUser.getType() == UserType.SOLE) {
            List<Right> allrights = rightMapper.selectList(null);
            List<String> retList = new ArrayList<String>();
            for (Right item : allrights) {
                retList.add(item.getTarget());
            }
            return retList;
        }

        EnterpriseRole role = enterpriseService.getEnterpriseRoleByEidUid(securityUser.getTargetId(), uid);
        if (role == null) {
            return new ArrayList<>();
        }

        return enterpriseService.getEnterpriseRoleRightListByRoleId(role.getId());
    }
}
