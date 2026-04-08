/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import ngo.friendship.syncapp.Bootstrap;
import ngo.friendship.syncapp.config.DbConnectionChecker;
import ngo.friendship.syncapp.model.Response;
import ngo.friendship.syncapp.repo.LocalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@RequestMapping("/DbManager")
public class DbManagerController extends CommonController {
    @Autowired
    @Qualifier("DB_TYPE")
    private Map<String, String> dblist;

    @Autowired
    private LocalRepo localRepo;

    @Autowired
    private Bootstrap syncComponent;
    
    @Autowired
    DbConnectionChecker connectionChecker;

    @RequestMapping("/index")
    public String objectIndex(Model model) {
        model.addAttribute("PAGE_TITLE", "Database Config | appSync");
        return "db_manager_index";
    }

    @RequestMapping(value = "/get-records", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    Response getObjectInfo(@RequestBody Map<String, String> request) {
        try {
            Map<String, Object> obj = new HashMap<>();
            obj.put("DATA", localRepo.getDbConfig());
            obj.put("LIST", dblist.keySet());
            return Response.OK(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.ERROR(ex.getMessage());
        }
    }

    @RequestMapping(value = "/manage-object/{type}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    Response manageObjectInfo(@PathVariable("type") String type, @RequestBody Map<String, Object> request) {
        try {
            List<Map<String, Object>> datas = localRepo.manageDbConfig(request, type);
            syncComponent.loadAllDataSource();
            return Response.OK(datas);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.ERROR(ex.getMessage());
        }
    }

    @RequestMapping(value = "/test-connection", method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json")
    public @ResponseBody
    Response testConnection(@RequestBody Map<String, Object> request) {
        try {
            String url = (String) request.get("DB_URL");
            String username = (String) request.get("DB_USER_NAME");
            String password = (String) request.get("DB_PASSWORD");
            Map<String, Object> result = connectionChecker.checkDbConnectXT(url, username, password);
            boolean isValid = Boolean.parseBoolean(result.get("isValid").toString());
            if (isValid) {
                return Response.OK(result.get("message").toString());
            } else {
                return Response.ERROR("Connection failed: " + result.get("message").toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.ERROR("Connection failed: " + ex.getMessage());
        }
    }

}
