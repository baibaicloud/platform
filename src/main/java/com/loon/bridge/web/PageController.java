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
 *   Administrator  2019年6月16日 下午9:50:56  created
 */
package com.loon.bridge.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 *
 * @author nbflow
 */
@Controller
class PageController {

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String mainPage() {
        return "signup.html";
    }

    @GetMapping("/login")
    public ModelAndView login(String error) {
        ModelAndView modelAndView = new ModelAndView("login.html");
        modelAndView.addObject("error", error);
        return modelAndView;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String indexPage() {
        return "index.html";
    }

    @RequestMapping(value = "/webdesktop", method = RequestMethod.GET)
    public String webdesktop() {
        return "webdesktop.html";
    }

    @RequestMapping(value = "/view/resourcemanage", method = RequestMethod.GET)
    public String resourceManagePage() {
        return "view/resource-manage.html";
    }

    @RequestMapping(value = "/view/accountmanage", method = RequestMethod.GET)
    public String accountManagePage() {
        return "view/account-manage.html";
    }

    @RequestMapping(value = "/view/switchenterprise", method = RequestMethod.GET)
    public String switchenTerprisePage() {
        return "view/switch-enterprise.html";
    }

    @RequestMapping(value = "/view/setting", method = RequestMethod.GET)
    public String settingPage() {
        return "view/setting.html";
    }

    @RequestMapping(value = "/view/tunnel", method = RequestMethod.GET)
    public String tunnelPage() {
        return "view/tunnel.html";
    }

    @RequestMapping(value = "/view/addne", method = RequestMethod.GET)
    public String addnePage() {
        return "view/addne.html";
    }

    @RequestMapping(value = "/view/about", method = RequestMethod.GET)
    public String aboutPage() {
        return "view/about.html";
    }

    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public String error404() {
        return "404.html";
    }

    @RequestMapping(value = "/resetpw", method = RequestMethod.GET)
    public String resetpwPage(HttpServletRequest request, HttpServletResponse response) {
        return "resetpw.html";
    }

}
