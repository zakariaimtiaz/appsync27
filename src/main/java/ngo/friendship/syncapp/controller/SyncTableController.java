/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.controller;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ngo.friendship.syncapp.model.Response;
import ngo.friendship.syncapp.repo.LocalRepo;
import ngo.friendship.syncapp.config.DbConnectionChecker;
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
@RequestMapping("/SyncTable")
public class SyncTableController extends CommonController {
    @Autowired
    private LocalRepo localRepo;

    @Autowired
    DbConnectionChecker connectionChecker;


    @RequestMapping("/index")
    public String objectIndex(Model model) {
        model.addAttribute("PAGE_TITLE", "Sync Table Config | appSync");

        return "sync_table_index";
    }

    @RequestMapping(value = "/get-records", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    Response getObjectInfo(@RequestBody Map<String, String> request) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("DATA", localRepo.getSyncTableConfig());
        obj.put("SERVER_LIST", localRepo.getSvrConfig());
        obj.put("CONFIG_LIST", localRepo.getDbConfig());

        try {
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
            return Response.OK(localRepo.manageSyncTableConfig(request, type));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.ERROR(ex.getMessage());
        }
    }

    @RequestMapping(value = "/get-table-names/{id}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    Response getTableNames(@PathVariable("id") int dbConfigId, @RequestBody Map<String, Object> request) {

        try {
            // Retrieve database configuration
            Map<String, Object> obj = localRepo.getDbConfigById(dbConfigId);
            String DB_URL = (String) obj.get("DB_URL");
            String DB_USER_NAME = (String) obj.get("DB_USER_NAME");
            String DB_PASSWORD = (String) obj.get("DB_PASSWORD");
            String DB_SCHEMA = (String) obj.get("SCHEMA");
            String DB_TYPE = (String) obj.get("DB_TYPE");

            int DB_CONNECTION_STATE = Integer.parseInt(obj.get("DB_CONNECTION_STATE").toString());

            if (DB_CONNECTION_STATE != 1) {
                return Response.ERROR("Database connection is not active");
            }

            // Get table names using the connection checker
            List<String> tableNames = getTableNamesFromDatabase(DB_URL, DB_USER_NAME, DB_PASSWORD, DB_TYPE);

            return Response.OK(tableNames);

        } catch (SQLException e) {
            return Response.ERROR("Database error: " + e.getMessage());
        } catch (Exception e) {
            return Response.ERROR("Error: " + e.getMessage());
        }
    }

    private List<String> getTableNamesFromDatabase(String dbUrl, String username, String password, String dbType) throws SQLException {
        List<String> tableNames = new ArrayList<>();

        try ( Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
            DatabaseMetaData metaData = connection.getMetaData();

            // Get metadata including catalog/schema info
            Map<String, Object> metadata = new HashMap<>();
            connectionChecker.addConnectionMetadata(metadata, connection, dbType);

            // Extract catalog and schema from metadata
            String catalog = (String) metadata.get("catalog");
            String schema = (String) metadata.get("schema");

            // Handle special cases for different databases
            if ("POSTGRESQL".equalsIgnoreCase(dbType) && schema == null) {
                schema = "public"; // Default schema for PostgreSQL
            }

            if ("SQLSERVER".equalsIgnoreCase(dbType) && schema == null) {
                schema = "dbo"; // Default schema for SQL Server
            }

            // Get table types based on database
            String[] tableTypes = connectionChecker.getTableTypesForDatabase(dbType);

            // Get tables
            try ( ResultSet tables = metaData.getTables(catalog, schema, "%", tableTypes)) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    tableNames.add(tableName);
                }
            }
        }

        return tableNames;
    }

}
