/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.controller;

import java.util.List;
import java.util.Map;
import ngo.friendship.syncapp.model.AppSync;
import ngo.friendship.syncapp.model.Response;
import ngo.friendship.syncapp.repo.LocalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
@RequestMapping("/AppProperties")
@PropertySource("classpath:application.properties")
public class AppManagerController extends CommonController {

    @Value("${logging.file}")
    private String loggingFileLocation;

    @Autowired
    private LocalRepo localRepo;

    @Autowired
    @Qualifier("APP_TYPE")
    private List<String> appList;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String objectIndex(Model model) {
        model.addAttribute("PAGE_TITLE", "App Config | appSync");
        model.addAttribute("APP_TYPE", appList);

        return "app_manager_index";
    }

    @RequestMapping(value = "/get-records", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    Response getObjectInfo(@RequestBody Map<String, String> request) {
        AppSync ap = localRepo.getAppSync();
        ap.setTypeList(appList);

        try {
            return Response.OK(ap);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.ERROR(ex.getMessage());
        }
    }

    @RequestMapping(value = "/manage-object/{type}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    Response manageObjectInfo(@PathVariable("type") String type, @RequestBody AppSync properties) {
        try {
            return Response.OK(localRepo.updateProperties(properties));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.ERROR(ex.getMessage());
        }
    }

    @RequestMapping(value = "/readme", method = RequestMethod.GET)
    public String objectReadme(Model model) {
        model.addAttribute("PAGE_TITLE", "Read Me | appSync");
        model.addAttribute("APP_TYPE", appList);

        return "readme/index";
    }

    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    public String objectLogs(Model model) {
        model.addAttribute("PAGE_TITLE", "Logs | appSync");
        model.addAttribute("LOG_FILE_LOCATION", loggingFileLocation);

        return "/log_viewer";
    }

}
