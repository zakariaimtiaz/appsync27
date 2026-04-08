/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.controller;

import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import ngo.friendship.syncapp.repo.LocalRepo;
import ngo.friendship.syncapp.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AccessController extends CommonController {

    @Autowired
    private LocalRepo localRepo;    

    @RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
    public String loginForm(Model model) {
        model.addAttribute("PAGE_TITLE", "LOGIN | appSync");
        model.addAttribute("APPLICATION_VERSION", Constant.APPLICATION_VERSION);

        return "login_page";
    }

    @RequestMapping(value = {"/try-login"}, method = {RequestMethod.POST})
    public String tryLogin(Model model, @RequestParam Map<String, String> reqParam, HttpSession httpSession, HttpServletRequest request) {

        String username = reqParam.get("username");
        String password = reqParam.get("password");

        // Get the user details if valid
        Map<String, Object> user = localRepo.isValidUser(username, password);
        if (user != null) {
            // Retrieve user information
            String loginId = (String) user.get("login_id");
            String name = (String) user.get("user_name");
            String email = (String) user.get("email");

            // Store user information in the session
            httpSession.setAttribute(Constant.USER_SESSION, Constant.SESSION_VALUE + UUID.randomUUID().toString());
            httpSession.setAttribute(Constant.LOGIN_ID, loginId);
            httpSession.setAttribute(Constant.USER_NAME, name);
            httpSession.setAttribute(Constant.USER_EMAIL, email);

            String _url = "";
            try {
                _url = (httpSession.getAttribute(Constant.PATH_URL) != null) ? httpSession.getAttribute(Constant.PATH_URL).toString() : "";
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (_url.equals("")) {
                return "redirect:/AppProperties/readme";
            } else {
                return "redirect:" + _url;
            }

        } else {
            model.addAttribute("loginError", "Invalid username or password");
            return "login_page"; // Return to the login page with an error message
        }
    }

    @RequestMapping(value = {"/logout"}, method = {RequestMethod.GET})
    public String tryLogout(Model model, HttpSession httpSession, HttpServletRequest request) {

        try {
            httpSession.removeAttribute(Constant.USER_SESSION);
            httpSession.removeAttribute(Constant.LOGIN_ID);
            httpSession.removeAttribute(Constant.USER_NAME);
            httpSession.removeAttribute(Constant.USER_EMAIL);
            httpSession.invalidate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "redirect:/login";
    }

}
