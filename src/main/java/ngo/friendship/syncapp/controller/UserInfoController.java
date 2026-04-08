/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ngo.friendship.syncapp.controller;


import java.util.Map;
import javax.servlet.http.HttpSession;
import ngo.friendship.syncapp.repo.LocalRepo;
import ngo.friendship.syncapp.util.Constant;
import ngo.friendship.syncapp.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Imtiaz
 */

@Controller
@RequestMapping("/UserInfo")
public class UserInfoController extends CommonController {
    
    @Autowired
    private LocalRepo localRepo;
    
    @RequestMapping(value="/change-password", method=RequestMethod.GET )
    public String objectIndex(Model model){
        model.addAttribute("PAGE_TITLE", "User Info | appSync");
        return "change_password_page";
    }

    @RequestMapping(value = {"/change-password"}, method = {RequestMethod.POST})
    public String changePassword(Model model, @RequestParam Map<String, String> reqParam, HttpSession httpSession) {
        String loginId = (String) httpSession.getAttribute(Constant.LOGIN_ID);
        String currentPassword = reqParam.get("currentPassword");
        String newPassword = reqParam.get("newPassword");
        String confirmPassword = reqParam.get("confirmPassword");

        // Check if new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("changePasswordError", "New password and confirmation do not match.");
            return "change_password_page"; // Return to the change password page with an error
        }

        try {
            // Assuming isValidUserForPasswordChange checks the current password in the database
            if (localRepo.isValidUserForPasswordChange(loginId, currentPassword)) {
                // Hash the new password
                String hashedPassword = PasswordUtils.hashPassword(newPassword);
                // Update the password in the database
                boolean isUpdated = localRepo.updatePassword(loginId, hashedPassword);
                if (isUpdated) {
                    model.addAttribute("changePasswordSuccess", "Password changed successfully.");
                } else {
                    model.addAttribute("changePasswordError", "Failed to update password. Please try again.");
                }
            } else {
                model.addAttribute("changePasswordError", "Current password is incorrect.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("changePasswordError", "An error occurred while changing the password.");
        }

        return "change_password_page"; // Return to the change password page with an error message
    }

    
}

