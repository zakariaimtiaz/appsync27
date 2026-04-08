/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ngo.friendship.syncapp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import ngo.friendship.syncapp.Bootstrap;
import ngo.friendship.syncapp.repo.LocalRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @author Imtiaz
 */
public class CustomDataSourceLoader {

    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    @Autowired
    private LocalRepo localRepo;

    @Autowired
    @Qualifier("DB_TYPE")
    private Map<String, String> dblist;

    private static final Map<String, DataSource> DS_REPO = new HashMap<>();

    public void loadAllDataSource() {
        DS_REPO.clear();  // Clear the repository map

        List<Map<String, Object>> dbConfigs = localRepo.getAllActiveDbComnfig();

        for (Map<String, Object> row : dbConfigs) {
            String CONFIG_NAME = (String) row.get("CONFIG_NAME");
            String DB_DRIVER_CLASS = dblist.get((String) row.get("DB_TYPE"));
            String DB_URL = (String) row.get("DB_URL");
            String DB_USER_NAME = (String) row.get("DB_USER_NAME");
            String DB_PASSWORD = (String) row.get("DB_PASSWORD");
            DataSource dataSource = getDataSource(DB_DRIVER_CLASS, DB_URL, DB_USER_NAME, DB_PASSWORD);
            if (dataSource != null) {
                DS_REPO.put(CONFIG_NAME, dataSource);
            } else {
                log.warn("{} database connection not successful", CONFIG_NAME);
            }
        }

    }

    public DataSource getDataSource(String sourceName) {
        DataSource ds = DS_REPO.get(sourceName);
        if (ds == null) {
            log.warn("{} database not available", sourceName);
        }
        return ds;
    }

    private DataSource getDataSource(String driver, String url, String userName, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        return dataSource;
    }
}
