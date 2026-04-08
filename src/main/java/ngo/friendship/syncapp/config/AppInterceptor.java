/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ngo.friendship.syncapp.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ngo.friendship.syncapp.util.Constant;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author sarker
 */
@Component
public class AppInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            String requestURI = request.getRequestURI();

            if (req.getSession().getAttribute(Constant.USER_SESSION) == null) {
                res.sendRedirect(request.getContextPath() + "/login");
            }
            return true;
        } catch (Exception ex) {
            //System.out.println(ex.getMessage());
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView mav) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet. - POST HANDLE"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception excptn) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet. - AFTER HANDLE"); //To change body of generated methods, choose Tools | Templates.
    }

}
