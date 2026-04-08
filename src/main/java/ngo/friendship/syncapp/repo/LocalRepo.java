/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.repo;

import ngo.friendship.syncapp.config.DbConnectionChecker;
import ngo.friendship.syncapp.model.AppSync;
import ngo.friendship.syncapp.model.SyncTable;
import ngo.friendship.syncapp.util.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Imtiaz
 */
@Repository
public class LocalRepo {

    @Autowired
    DbConnectionChecker connectionChecker;

    private static final Logger log = LoggerFactory.getLogger(LocalRepo.class);
    private static final String TBL_TYPE_MC = "MC";

    @Autowired
    @Qualifier("sqliteTemplate")
    private JdbcTemplate sqlite;

    public List<SyncTable> getSyncableTables(String tableType) {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT st.TBL_ID,st.TBL_CODE,st.TBL_NAME,st.TBL_TYPE,dc.SCHEMA,st.TBL_PRIMARY_COLUMN, ");
        sql.append(" st.SYNC_PRIORITY , dc.CONFIG_NAME,dc.DB_TYPE ,sc.SERVER_ADDRESS ,st.CHUNK_SIZE, ");
        sql.append(" st.SELECT_SQL ,st.UPSERT_SQL, st.TBL_S_TRACKING_COLUMN, st.TBL_S_TRACKING_SQL,  ");
        sql.append(" st.TBL_C_TRACKING_COLUMN, st.TBL_PRIMARY_COLUMN_TYPE ");
        sql.append(" FROM sync_table st ");
        sql.append(" LEFT JOIN db_config dc ON st.DB_CONFIG_ID=dc.DB_CONFIG_ID ");
        sql.append(" LEFT JOIN server_config sc ON sc.SERVER_ID=st.SERVER_ID ");
        sql.append(" WHERE st.STATE = 1 AND ( UPPER(st.TBL_TYPE)=UPPER(?)  ");
        sql.append(" OR UPPER(st.TBL_TYPE) = UPPER('" + TBL_TYPE_MC + "') ) ");
        sql.append(" ORDER BY st.SYNC_PRIORITY  ASC ");
        List<SyncTable> rows = sqlite.query(sql.toString(),
                new Object[]{tableType}, (rs, rowNum)
                        -> new SyncTable(
                        rs.getLong("TBL_ID"),
                        rs.getString("TBL_CODE"),
                        rs.getString("TBL_NAME"),
                        rs.getString("TBL_TYPE"),
                        rs.getString("SCHEMA"),
                        rs.getString("SERVER_ADDRESS"),
                        rs.getString("CONFIG_NAME"),
                        rs.getString("DB_TYPE"),
                        rs.getString("TBL_PRIMARY_COLUMN"),
                        rs.getString("TBL_PRIMARY_COLUMN_TYPE"),
                        rs.getLong("SYNC_PRIORITY"),
                        rs.getLong("CHUNK_SIZE"),
                        rs.getString("SELECT_SQL"),
                        rs.getString("UPSERT_SQL"),
                        rs.getString("TBL_S_TRACKING_COLUMN"),
                        rs.getString("TBL_S_TRACKING_SQL"),
                        rs.getString("TBL_C_TRACKING_COLUMN")
                ));
        return rows;
    }

    public SyncTable getSyncableTable(String tblCode, String tblType) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT st.TBL_ID,st.TBL_CODE,st.TBL_NAME,st.TBL_TYPE,dc.SCHEMA,st.TBL_PRIMARY_COLUMN, ");
            sql.append(" st.SYNC_PRIORITY , dc.CONFIG_NAME,dc.DB_TYPE ,sc.SERVER_ADDRESS ,st.CHUNK_SIZE, ");
            sql.append(" st.SELECT_SQL ,st.UPSERT_SQL, st.TBL_S_TRACKING_COLUMN,st.TBL_S_TRACKING_SQL,  ");
            sql.append(" st.TBL_C_TRACKING_COLUMN, st.TBL_PRIMARY_COLUMN_TYPE  ");
            sql.append(" FROM sync_table st ");
            sql.append(" LEFT JOIN db_config dc ON st.DB_CONFIG_ID=dc.DB_CONFIG_ID ");
            sql.append(" LEFT JOIN server_config sc ON sc.SERVER_ID=st.SERVER_ID ");
            sql.append(" WHERE st.STATE =1 AND ( UPPER(st.TBL_TYPE)=UPPER(?)  ");
            sql.append(" OR UPPER(st.TBL_TYPE) = UPPER('" + TBL_TYPE_MC + "') ) ");
            sql.append(" AND st.TBL_CODE=?  ");
            sql.append(" ORDER BY st.SYNC_PRIORITY  ASC ");
            return sqlite.queryForObject(sql.toString(),
                    new Object[]{tblType, tblCode},
                    (rs, rowNum)
                            -> new SyncTable(
                            rs.getLong("TBL_ID"),
                            rs.getString("TBL_CODE"),
                            rs.getString("TBL_NAME"),
                            rs.getString("TBL_TYPE"),
                            rs.getString("SCHEMA"),
                            rs.getString("SERVER_ADDRESS"),
                            rs.getString("CONFIG_NAME"),
                            rs.getString("DB_TYPE"),
                            rs.getString("TBL_PRIMARY_COLUMN"),
                            rs.getString("TBL_PRIMARY_COLUMN_TYPE"),
                            rs.getLong("SYNC_PRIORITY"),
                            rs.getLong("CHUNK_SIZE"),
                            rs.getString("SELECT_SQL"),
                            rs.getString("UPSERT_SQL"),
                            rs.getString("TBL_S_TRACKING_COLUMN"),
                            rs.getString("TBL_S_TRACKING_SQL"),
                            rs.getString("TBL_C_TRACKING_COLUMN")
                    ));

        } catch (DataAccessException ex) {
            log.error(">>>>>>>>>>>>>>>>>" + ex.getMessage());
        }
        return null;
    }

    public List<Map<String, Object>> getAllActiveDbComnfig() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select DB_CONFIG_ID,CONFIG_NAME,DB_URL,DB_USER_NAME,DB_PASSWORD,DB_TYPE," +
                "STATE FROM db_config where STATE=1 ");
        return sqlite.queryForList(sql.toString());
    }

    public Map<String, Object> isValidUser(String loginId, String password) {
        String sql = "SELECT id, login_id, user_name, email, password FROM user_info WHERE login_id = ? AND state = 1";
        try {
            Map<String, Object> user = sqlite.queryForMap(sql, loginId);

            // Validate the provided password with the stored hashed password
            String storedHash = (String) user.get("password");
            String hashedPassword = PasswordUtils.hashPassword(password);
            if (hashedPassword.equals(storedHash)) {
                // Return user details if the password matches
                return user;
            } else {
                return null; // Password did not match
            }
        } catch (EmptyResultDataAccessException e) {
            return null; // User not found
        }
    }

    public boolean isValidUserForPasswordChange(String loginId, String currentPassword) {
        String sql = "SELECT password FROM user_info WHERE login_id = ? AND state = 1";
        try {
            String storedHash = sqlite.queryForObject(sql, String.class, loginId);
            // Hash the provided current password
            String hashedCurrentPassword = PasswordUtils.hashPassword(currentPassword);
            return hashedCurrentPassword.equals(storedHash);
        } catch (EmptyResultDataAccessException e) {
            return false; // User not found or password does not match
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Handle any other exceptions
        }
    }

    public boolean updatePassword(String loginId, String newHashedPassword) {
        String sql = "UPDATE user_info SET password = ?, updated_at = CURRENT_TIMESTAMP WHERE login_id = ?";
        try {
            int rowsUpdated = sqlite.update(sql, newHashedPassword, loginId);
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Handle exceptions if the update fails
        }
    }

    // App Configuration sql code *** START
    public AppSync getAppSync() {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("   select ID,NAME,CODE,TYPE,STATE FROM app_properties  ");
            // log.trace("SQL::", sql.toString());
            return sqlite.queryForObject(sql.toString(), (rs, rowNum)
                    -> new AppSync(
                    rs.getLong("ID"),
                    rs.getString("NAME"),
                    rs.getString("CODE"),
                    rs.getString("TYPE"),
                    rs.getLong("STATE")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new AppSync();
    }

    public int updateProperties(AppSync properties) {
        StringBuilder sql = new StringBuilder();
        sql.append("   REPLACE INTO app_properties (ID,NAME,CODE,TYPE,STATE ) VALUES (?,?,?,?,?) ");
        return sqlite.update(sql.toString(), properties.getId(), properties.getName(), properties.getCode(), properties.getType(), properties.getState());
    }

    // App Configuration sql code *** END
    // Server manager sql code *** START
    public String getSvrAddress() {
        StringBuilder sql = new StringBuilder();
        sql.append("   select SERVER_ADDRESS from server_config where STATE = 1 limit 1  ");
        return sqlite.queryForObject(sql.toString(), String.class);
    }

    public List<Map<String, Object>> getSvrConfig() {
        StringBuilder sql = new StringBuilder();
        sql.append("   select SERVER_ID, SERVER_NAME, SERVER_ADDRESS, STATE from server_config order by SERVER_ID  ");
        return sqlite.queryForList(sql.toString());
    }

    public List<Map<String, Object>> manageSvrConfig(Map<String, Object> request, String type) {
        if (type.equalsIgnoreCase("NEW")) {
            String _sqlIn = " insert into server_config (SERVER_NAME, SERVER_ADDRESS, STATE) values(?,?, 1); ";
            this.sqlite.update(_sqlIn,
                    request.get("SERVER_NAME").toString(),
                    request.get("SERVER_ADDRESS").toString()
            );

        } else if (type.equalsIgnoreCase("UPDATE")) {

            String _sqlUp = " UPDATE server_config set SERVER_NAME=?, SERVER_ADDRESS=?, STATE=? where SERVER_ID=?; ";
            this.sqlite.update(_sqlUp,
                    request.get("SERVER_NAME").toString(),
                    request.get("SERVER_ADDRESS").toString(),
                    (int) request.get("STATE"),
                    (int) request.get("SERVER_ID")
            );
        }
        return null;
    }

    public boolean isClient(String clientCode) {
        long count = sqlite.queryForObject("   select COUNT(*) from client where CODE=? and STATE=1   ", new Object[]{clientCode}, Integer.class);
        return count == 1 ? true : false;
    }

    public List<Map<String, Object>> getClientConfig() {
        StringBuilder sql = new StringBuilder();
        sql.append("   select ID, NAME, CODE, STATE from client order by ID  ");

        return sqlite.queryForList(sql.toString());
    }

    public List<Map<String, Object>> manageClientConfig(Map<String, Object> request, String type) {
        if (type.equalsIgnoreCase("NEW")) {
            String _sqlIn = " insert into client (NAME, CODE, STATE) values(?,?, 1); ";
            this.sqlite.update(_sqlIn,
                    request.get("NAME").toString(),
                    request.get("CODE").toString()
            );
        } else if (type.equalsIgnoreCase("UPDATE")) {
            String _sqlUp = " UPDATE client set NAME=?, CODE=?, STATE=? where ID=?; ";
            this.sqlite.update(_sqlUp,
                    request.get("NAME").toString(),
                    request.get("CODE").toString(),
                    (int) request.get("STATE"),
                    (int) request.get("ID")
            );
        }
        return null;
    }

    // Client config sql code *** END
    // Database manager sql code *** START
    public List<Map<String, Object>> getDbConfig() {
        StringBuilder sql = new StringBuilder();
        sql.append("   select DB_CONFIG_ID, CONFIG_NAME, DB_URL, DB_USER_NAME, DB_PASSWORD, DB_TYPE,SCHEMA,   ");
        sql.append("   STATE, DB_CONNECTION_STATE from db_config order by DB_CONFIG_ID  ");
        return sqlite.queryForList(sql.toString());
    }

    public Map<String, Object> getDbConfigById(int DB_CONFIG_ID) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DB_CONFIG_ID, CONFIG_NAME, DB_URL, DB_USER_NAME, DB_PASSWORD, DB_TYPE, SCHEMA, STATE, DB_CONNECTION_STATE ");
        sql.append("FROM db_config WHERE DB_CONFIG_ID = ?");

        try {
            return sqlite.queryForMap(sql.toString(), DB_CONFIG_ID);
        } catch (EmptyResultDataAccessException e) {
            // Handle the case where no record is found
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally, handle other exceptions
            return null;
        }
    }

    public List<Map<String, Object>> manageDbConfig(Map<String, Object> request, String type) {
        int DB_CONFIG_ID = 0;
        String DB_URL = request.get("DB_URL").toString();
        String DB_USER_NAME = request.get("DB_USER_NAME").toString();
        String DB_PASSWORD = request.get("DB_PASSWORD").toString();

        if (type.equalsIgnoreCase("NEW")) {
            String _sqlIn = " insert into db_config (CONFIG_NAME, DB_URL, DB_USER_NAME, DB_PASSWORD, DB_TYPE,SCHEMA, STATE) values(?,?,?,?,?, ?,1); ";
            this.sqlite.update(_sqlIn,
                    request.get("CONFIG_NAME").toString(),
                    DB_URL,
                    DB_USER_NAME,
                    DB_PASSWORD,
                    request.get("DB_TYPE").toString(),
                    request.get("SCHEMA").toString()
            );
            // Fetch the last inserted ID
            String getLastIdSql = "SELECT last_insert_rowid() AS DB_CONFIG_ID;";
            Map<String, Object> result = this.sqlite.queryForMap(getLastIdSql);
            if (result != null && result.containsKey("DB_CONFIG_ID")) {
                DB_CONFIG_ID = (int) result.get("DB_CONFIG_ID");
            }
        } else if (type.equalsIgnoreCase("UPDATE")) {
            DB_CONFIG_ID = (int) request.get("DB_CONFIG_ID");
            String _sqlUp = " UPDATE db_config set CONFIG_NAME=?, DB_URL=?, DB_USER_NAME=?, DB_PASSWORD=?, DB_TYPE=?,SCHEMA=?, STATE=? where DB_CONFIG_ID=?; ";
            this.sqlite.update(_sqlUp,
                    request.get("CONFIG_NAME").toString(),
                    DB_URL,
                    DB_USER_NAME,
                    DB_PASSWORD,
                    request.get("DB_TYPE").toString(),
                    request.get("SCHEMA").toString(),
                    (int) request.get("STATE"),
                    DB_CONFIG_ID
            );
        }

        Map<String, Object> result = connectionChecker.checkDbConnectXT(DB_URL, DB_USER_NAME, DB_PASSWORD);
        boolean isValid = Boolean.parseBoolean(result.get("isValid").toString());

        if (isValid) {
            updateConnectionState(DB_CONFIG_ID, 1);
        } else {
            updateConnectionState(DB_CONFIG_ID, 0);
        }

        return null;
    }

    // Database manager sql code *** END
    // Sync table sql code *** START
    public List<Map<String, Object>> getSyncTableConfig() {
        StringBuilder sql = new StringBuilder();
        sql.append("  select st.TBL_ID, st.TBL_CODE, st.TBL_NAME, st.TBL_TYPE, ");
        sql.append("  st.SERVER_ID,dbc.CONFIG_NAME,sc.SERVER_NAME , st.DB_CONFIG_ID,  ");
        sql.append("  st.STATE, st.SYNC_PRIORITY, st.CHUNK_SIZE ,st.SELECT_SQL ,st.UPSERT_SQL, ");
        sql.append("  st.TBL_S_TRACKING_COLUMN,st.TBL_S_TRACKING_SQL, st.TBL_C_TRACKING_COLUMN, ");
        sql.append("  st.TBL_PRIMARY_COLUMN,st.TBL_PRIMARY_COLUMN_TYPE ");
        sql.append("  from sync_table st ");
        sql.append("  left join db_config dbc on st.DB_CONFIG_ID=dbc.DB_CONFIG_ID ");
        sql.append("  left join server_config sc on st.SERVER_ID=sc.SERVER_ID ");
        sql.append("  order by st.SYNC_PRIORITY ASC,st.TBL_TYPE DESC ");
        return sqlite.queryForList(sql.toString());
    }

    public List<Map<String, Object>> manageSyncTableConfig(Map<String, Object> request, String type) {
        String tblName = request.get("TBL_NAME").toString();
        Integer dbConfigId = Integer.parseInt(request.get("DB_CONFIG_ID").toString());
        String primaryKeyName = request.get("TBL_PRIMARY_COLUMN").toString();
        String primaryKeyType = getTablePrimaryKeyType(dbConfigId, tblName, primaryKeyName);

        if (type.equalsIgnoreCase("NEW")) {
            String _sqlIn = " insert into sync_table (TBL_CODE, TBL_NAME, TBL_TYPE, SERVER_ID, DB_CONFIG_ID, " +
                    "TBL_PRIMARY_COLUMN, TBL_PRIMARY_COLUMN_TYPE, SYNC_PRIORITY, CHUNK_SIZE,STATE,SELECT_SQL ,UPSERT_SQL," +
                    "TBL_S_TRACKING_COLUMN, TBL_S_TRACKING_SQL, TBL_C_TRACKING_COLUMN) " +
                    "values(?,?,?,?,?, ?, ?, ?, ?,1,?,?,?,?,?); ";
            this.sqlite.update(_sqlIn,
                    request.get("TBL_CODE").toString(),
                    tblName,
                    request.get("TBL_TYPE").toString(),
                    Integer.parseInt(request.get("SERVER_ID").toString()),
                    dbConfigId,
                    primaryKeyName,
                    primaryKeyType,
                    Integer.parseInt(request.get("SYNC_PRIORITY").toString()),
                    Integer.parseInt(request.get("CHUNK_SIZE").toString()),
                    request.get("SELECT_SQL"),
                    request.get("UPSERT_SQL"),
                    request.get("TBL_S_TRACKING_COLUMN"),
                    request.get("TBL_S_TRACKING_SQL"),
                    request.get("TBL_C_TRACKING_COLUMN")
            );
        } else if (type.equalsIgnoreCase("UPDATE")) {
            try {
                String _sqlUp = " UPDATE sync_table set TBL_CODE=?, TBL_NAME=?, TBL_TYPE=?, SERVER_ID=?," +
                        " DB_CONFIG_ID=?, TBL_PRIMARY_COLUMN=?, TBL_PRIMARY_COLUMN_TYPE=?, STATE=?, SYNC_PRIORITY=?, CHUNK_SIZE=?," +
                        "SELECT_SQL=? ,UPSERT_SQL=?, TBL_S_TRACKING_COLUMN=?, TBL_S_TRACKING_SQL=?, TBL_C_TRACKING_COLUMN=? " +
                        "where TBL_ID=?; ";
                this.sqlite.update(_sqlUp,
                        request.get("TBL_CODE").toString(),
                        tblName,
                        request.get("TBL_TYPE").toString(),
                        Integer.parseInt(request.get("SERVER_ID").toString()),
                        dbConfigId,
                        primaryKeyName,
                        primaryKeyType,
                        Integer.parseInt(request.get("STATE").toString()),
                        Integer.parseInt(request.get("SYNC_PRIORITY").toString()),
                        Integer.parseInt(request.get("CHUNK_SIZE").toString()),
                        request.get("SELECT_SQL"),
                        request.get("UPSERT_SQL"),
                        request.get("TBL_S_TRACKING_COLUMN"),
                        request.get("TBL_S_TRACKING_SQL"),
                        request.get("TBL_C_TRACKING_COLUMN"),
                        Integer.parseInt(request.get("TBL_ID").toString())
                );
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception: " + e.getMessage());
            }
        }

        return null;
    }

    // Sync table sql code *** END
    public List<Map<String, Object>> getRecordList(String tblCode, String clCode) {

        StringBuilder sql = new StringBuilder();
        sql.append("  select rd.ID, rd.TBL_CODE, datetime(rd.ACTION_TIME/1000, 'unixepoch') ACTION_TIME," +
                " rd.TIME_NEED, rd.NO_OF_FETCH, rd.NO_OF_COM, rd.STATUS, rd.NOTE, rd.UU_ID, " +
                "rd.CLIENT_CODE, rd.TYPE, tbl.TBL_NAME, cl.NAME CLIENT_NAME " +
                "from record rd ");
        sql.append("  left join sync_table tbl on rd.TBL_CODE=tbl.TBL_CODE ");
        sql.append("  left join client cl on rd.CLIENT_CODE=cl.CODE ");
        sql.append("  where rd.TBL_CODE=? and rd. CLIENT_CODE=?");
        return sqlite.queryForList(sql.toString(), tblCode, clCode);
    }

    public boolean updateConnectionState(int dbConfigId, int connectionState) {
        try {
            String sql = "UPDATE db_config SET DB_CONNECTION_STATE = ? WHERE DB_CONFIG_ID = ?";
            int rowsUpdated = sqlite.update(sql, connectionState, dbConfigId);
            return rowsUpdated > 0;
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            return false;
        }
    }

    public int checkDbConnect(int DB_CONFIG_ID) {
        try {
            return sqlite.queryForObject(" select DB_CONFIG_ID from client where DB_CONFIG_ID=?  ", new Object[]{DB_CONFIG_ID}, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }


    private String getTablePrimaryKeyType(int dbConfigId, String tableName, String primaryKey) {
        try {
            // Retrieve database configuration
            Map<String, Object> obj = getDbConfigById(dbConfigId);

            // Check if configuration exists
            if (obj == null || obj.isEmpty()) {
                System.out.println("Database configuration not found for ID: " + dbConfigId);
                return null;
            }
            String DB_TYPE = (String) obj.get("DB_TYPE");
            if ("POSTGRESQL".equalsIgnoreCase(DB_TYPE)) {
                return null;
            }

            String DB_URL = (String) obj.get("DB_URL");
            String DB_USER_NAME = (String) obj.get("DB_USER_NAME");
            String DB_PASSWORD = (String) obj.get("DB_PASSWORD");
            String DB_SCHEMA = (String) obj.get("SCHEMA");

            // Get table fields for the specified table
            Map<String, String> tableFields = getTableFieldsFromDatabase(
                    DB_URL, DB_USER_NAME, DB_PASSWORD, DB_TYPE, DB_SCHEMA, tableName
            );

            // Check if table exists and has fields
            if (tableFields == null || tableFields.isEmpty()) {
                System.out.println("Table '" + tableName + "' not found or has no columns");
                return null;
            }

            // Get the type of the specified primary key column
            String primaryKeyType = tableFields.get(primaryKey);

            if (primaryKeyType == null) {
                System.out.println("Primary key column '" + primaryKey + "' not found in table '" + tableName + "'");
                return null;
            }

            return primaryKeyType;

        } catch (SQLException e) {
            System.out.println("SQLException in getTablePrimaryKeyType: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("Exception in getTablePrimaryKeyType: " + e.getMessage());
            return null;
        }
    }

    private Map<String, String> getTableFieldsFromDatabase(
            String dbUrl, String username, String password, String dbType,
            String configuredSchema, String tableName) throws SQLException {

        Map<String, String> fields = new LinkedHashMap<>();

        try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
            DatabaseMetaData metaData = connection.getMetaData();

            // Get metadata including catalog/schema info
            Map<String, Object> metadata = new HashMap<>();
            connectionChecker.addConnectionMetadata(metadata, connection, dbType);

            // Extract catalog and schema from metadata
            String catalog = (String) metadata.get("catalog");
            String schema = configuredSchema != null ? configuredSchema : (String) metadata.get("schema");

            // Handle special cases for different databases
            if ("POSTGRESQL".equalsIgnoreCase(dbType) && schema == null) {
                schema = "public"; // Default schema for PostgreSQL
            }

            if ("SQLSERVER".equalsIgnoreCase(dbType) && schema == null) {
                schema = "dbo"; // Default schema for SQL Server
            }

            // Get columns for the specified table
            try (ResultSet columns_rs = metaData.getColumns(catalog, schema, tableName, "%")) {
                while (columns_rs.next()) {
                    String columnName = columns_rs.getString("COLUMN_NAME");
                    String columnType = columns_rs.getString("TYPE_NAME");

                    // Add to map - column name as key, type as value
                    fields.put(columnName, columnType);
                }
            }
        }

        return fields;
    }


}
