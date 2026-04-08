/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ngo.friendship.syncapp.model.AppSync;
import ngo.friendship.syncapp.repo.LocalRepo;
import ngo.friendship.syncapp.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * created: 17 Dec 2017
 * @author Shahadat
 * load/config all global variable as well as context path
 */

public class CommonController {
    
    @Autowired
    private LocalRepo localRepo;

    @ModelAttribute
    public void addCommonObjects(Model model, HttpServletRequest request, HttpServletResponse resp, HttpSession httpSession) {
        model.addAttribute("BASE_URL", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/");
        model.addAttribute("APP", request.getContextPath());
        model.addAttribute("PAGE_TITLE", "appSync");
        model.addAttribute("CURL", request.getRequestURL().toString());
        try {
            if (httpSession.getAttribute(Constant.USER_NAME) != null) {
                AppSync ap = localRepo.getAppSync();

                model.addAttribute("IS_SERVER", ap.isServer());
                model.addAttribute("IS_CLIENT", ap.isClient());
                model.addAttribute("IS_ACTIVE", ap.isActive());

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
