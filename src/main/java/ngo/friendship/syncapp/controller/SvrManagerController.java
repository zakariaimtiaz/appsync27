/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.controller;

import java.util.Map;
import javax.servlet.http.HttpSession;
import ngo.friendship.syncapp.model.Response;
import ngo.friendship.syncapp.repo.LocalRepo;
import ngo.friendship.syncapp.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Imtiaz
 */

@Controller
@RequestMapping("/SvrManager")
public class SvrManagerController extends CommonController{
    
    @Autowired
    private LocalRepo localRepo;
    
    @RequestMapping("/index")
    public String objectIndex(Model model){
        model.addAttribute("PAGE_TITLE", "Server Config | appSync");
        
        return "svr_manager_index";
    }
    
    @RequestMapping(value = "/get-records", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody Response getObjectInfo(@RequestBody Map<String, String> request) {
        try{
            return Response.OK(localRepo.getSvrConfig());
        } catch(Exception ex){
            ex.printStackTrace();
            return Response.ERROR(ex.getMessage());
        }
    }
    
    @RequestMapping(value = "/manage-object/{type}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody Response manageObjectInfo(@PathVariable("type") String type, @RequestBody Map<String, Object> request) {
        try{
            return Response.OK(localRepo.manageSvrConfig(request, type));
        } catch(Exception ex){
            ex.printStackTrace();
            return Response.ERROR(ex.getMessage());
        }
    }
    
}
