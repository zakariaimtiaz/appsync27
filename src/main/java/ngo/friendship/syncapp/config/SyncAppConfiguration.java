package ngo.friendship.syncapp.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import ngo.friendship.syncapp.util.Constant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import static ngo.friendship.syncapp.util.Constant.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;

@PropertySource("classpath:application.properties")
@Configuration
@EnableScheduling
public class SyncAppConfiguration extends WebMvcConfigurerAdapter {

    @Value("${app.db.location}")
    private String dbLocation;

    @Value("${app.db.name}")
    private String dbName;

    private String dbFilePath;

    /**
     * Initialize database configuration on startup
     */
    @PostConstruct
    public void init() {
        // Build the complete database file path
        dbFilePath = dbLocation + File.separator + dbName + Constant.SQLITE_EXT;

        // Ensure the SQLite driver is loaded
        loadSqliteDriver();
    }

    /**
     * Explicitly load SQLite JDBC driver
     */
    private void loadSqliteDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQLite JDBC driver. Make sure sqlite-jdbc is in the classpath.");
            e.printStackTrace();
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    @Bean(name = "sqlite")
    @Primary
    public DataSource appSync27DataSource() throws IOException {
        System.out.println("Configuring SQLite DataSource...");

        try {
            // Validate configuration
            if (dbLocation == null || dbLocation.trim().isEmpty()) {
                throw new IllegalArgumentException("Database location is not configured");
            }
            if (dbName == null || dbName.trim().isEmpty()) {
                throw new IllegalArgumentException("Database name is not configured");
            }

            // Ensure database directory exists
            File dbFile = new File(dbFilePath);
            File parentDir = dbFile.getParentFile();

            if (parentDir != null && !parentDir.exists()) {
                System.out.println("Creating database directory: " + parentDir.getAbsolutePath());
                boolean created = parentDir.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create database directory: " + parentDir.getAbsolutePath());
                }
            }

            // Build connection URL
            String jdbcUrl = "jdbc:sqlite:" + dbFilePath;
            System.out.println("JDBC URL: " + jdbcUrl);

            // Check if database file exists
            boolean dbExists = dbFile.exists();

            // Create database if it doesn't exist
            if (!dbExists) {
                createNewDatabase(jdbcUrl, dbFile);
            } else {
                validateExistingDatabase(jdbcUrl);
            }

            // Create and return DataSource
            DataSource dataSource = DataSourceBuilder.create()
                    .driverClassName("org.sqlite.JDBC")
                    .url(jdbcUrl)
                    .build();

            return dataSource;

        } catch (SQLException e) {
            System.err.println("Failed to create SQLite database: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to create SQLite database: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Unexpected error configuring DataSource: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("DataSource configuration failed: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new database file and initialize schema
     */
    private void createNewDatabase(String jdbcUrl, File dbFile) throws SQLException, IOException {
        try ( Connection conn = DriverManager.getConnection(jdbcUrl)) {
            System.out.println("SQLite database file created: " + dbFile.getAbsolutePath());

            // Create tables from schema file
            executeSqlScript(conn, "sql/schema.sql");

            // Insert initial data from file
            executeSqlScript(conn, "sql/data.sql");

            System.out.println("New SQLite database initialized successfully");
        }
    }

    /**
     * Validate existing database connection
     */
    private void validateExistingDatabase(String jdbcUrl) throws SQLException {
        try ( Connection conn = DriverManager.getConnection(jdbcUrl)) {
            // Test connection with a simple query
            try ( Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT 1");
            }
            System.out.println("Existing database connection validated successfully");
        }
    }

    /**
     * Execute SQL statements from a file
     */
    private void executeSqlScript(Connection conn, String resourcePath)
            throws SQLException, IOException {

        System.out.println("Executing SQL script: " + resourcePath);

        conn.setAutoCommit(true);

        String sqlScript = readResourceFile(resourcePath);
        if (sqlScript == null || sqlScript.trim().isEmpty()) {
            System.err.println("SQL script is empty or not found: " + resourcePath);
            return;
        }

        //REMOVE COMMENTS FIRST
        sqlScript = sqlScript
                .replaceAll("(?m)^\\s*--.*$", "")
                .replaceAll("(?s)/\\*.*?\\*/", "");

        String[] statements = sqlScript.split(";");

        try ( Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                sql = sql.trim();
                if (sql.isEmpty()) {
                    continue;
                }

                stmt.execute(sql);
                System.out.println("Executed: " + getFirstWords(sql, 8));
            }
        }

        System.out.println("Finished executing: " + resourcePath);
    }

    /**
     * Read a resource file from classpath
     */
    private String readResourceFile(String resourcePath) throws IOException {
        StringBuilder content = new StringBuilder();

        try ( InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);  BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

        } catch (NullPointerException e) {
            System.err.println("Resource not found: " + resourcePath);
            return null;
        }

        return content.toString();
    }

    /**
     * Smart SQL statement splitting (handles semicolons in strings/comments)
     */
    private String[] splitSqlStatements(String sqlScript) {
        // Simple implementation - split by semicolon that's not in quotes
        // For production, use a proper SQL parser
        return sqlScript.split(";(?=(?:[^']*'[^']*')*[^']*$)");
    }

    /**
     * Get first N words of SQL for logging
     */
    private String getFirstWords(String sql, int maxWords) {
        if (sql.length() <= 50) {
            return sql;
        }

        String[] words = sql.split("\\s+");
        StringBuilder result = new StringBuilder();
        int count = Math.min(words.length, maxWords);

        for (int i = 0; i < count; i++) {
            result.append(words[i]).append(" ");
        }

        return result.toString().trim() + "...";
    }

//##########################################################################################################
    /**
     * Configures the JdbcTemplate for SQLite.
     */
    @Bean(name = "sqliteTemplate")
    public JdbcTemplate appSync27JdbcTemplate(@Qualifier("sqlite") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    /**
     * Loads a map of database types and their corresponding driver class names.
     */
    @Bean(name = DB_TYPE)
    public Map<String, String> loadDbList() {
        Map<String, String> dbList = new HashMap<>();
        dbList.put(MYSQL, "com.mysql.cj.jdbc.Driver");
        dbList.put(ORACLE, "oracle.jdbc.OracleDriver");
        dbList.put(POSTGRES, "org.postgresql.Driver");
        dbList.put(MSSQL, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dbList.put(SQLITE, "org.sqlite.JDBC");
        return dbList;
    }

    /**
     * Loads a list of application types.
     */
    @Bean(name = APP_TYPE)
    public List<String> appType() {
        List<String> list = new ArrayList<>();
        list.add(SERVER);
        list.add(CLIENT);
        return list;
    }

}
