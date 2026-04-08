/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ngo.friendship.syncapp.config;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import ngo.friendship.syncapp.model.DatabaseConfig;
import org.springframework.stereotype.Service;

@Service
public class DbConnectionChecker {

    public Map<String, Object> checkDbConnectXT(String url, String username, String password) {
        Map<String, Object> result = new HashMap<>();

        // Call utility method to fix SSL issues
        fixSSLConfiguration();

        String dbType = detectDbType(url);

        try {
            // Get database configuration
            DatabaseConfig config = getDatabaseConfig(dbType);

            // Load driver
            Class.forName(config.getDriverClass());

            // Create connection properties
            Properties connectionProps = createConnectionProperties(username, password, config);

            try ( Connection connection = DriverManager.getConnection(url, connectionProps)) {
                if (connection != null && !connection.isClosed()) {
                    // Test with a simple query
                    if (config.getTestQuery() != null && !config.getTestQuery().isEmpty()) {
                        testConnectionWithQuery(connection, config.getTestQuery());
                    }

                    result.put("isValid", true);
                    result.put("message", "Connection Successful!");
                    result.put("databaseType", dbType);
                    result.put("databaseName", config.getName());

                    // Get additional metadata
                    addConnectionMetadata(result, connection, dbType);
                }
            }
        } catch (ClassNotFoundException ex) {
            result.put("isValid", false);
            result.put("message", "Driver not found: " + ex.getMessage());
            result.put("suggestion", "Add the JDBC driver JAR to classpath");
        } catch (SQLException ex) {
            result.put("isValid", false);
            result.put("message", "SQL Error: " + ex.getMessage());
            result.put("sqlState", ex.getSQLState());
            result.put("errorCode", ex.getErrorCode());
        } catch (Exception ex) {
            result.put("isValid", false);
            result.put("message", "Error: " + ex.getMessage());
        }

        return result;
    }

    private DatabaseConfig getDatabaseConfig(String dbType) {
        DatabaseConfig config = new DatabaseConfig();

        switch (dbType.toUpperCase()) {
            case "MYSQL":
                config.setName("MySQL");
                config.setDriverClass("com.mysql.cj.jdbc.Driver");
                config.setTestQuery("SELECT 1");
                config.setDefaultProperties(createProperties(
                        "useSSL=false",
                        "serverTimezone=UTC",
                        "allowPublicKeyRetrieval=true",
                        "connectTimeout=5000",
                        "socketTimeout=10000"
                ));
                break;

            case "POSTGRESQL":
                config.setName("PostgreSQL");
                config.setDriverClass("org.postgresql.Driver");
                config.setTestQuery("SELECT 1");
                config.setDefaultProperties(createProperties(
                        "ssl=false",
                        "connectTimeout=5",
                        "socketTimeout=10",
                        "tcpKeepAlive=true"
                ));
                break;

            case "SQLSERVER":
                config.setName("SQL Server");
                config.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                config.setTestQuery("SELECT 1");
                config.setDefaultProperties(createProperties(
                        "encrypt=false",
                        "trustServerCertificate=true",
                        "loginTimeout=5",
                        "socketTimeout=10000"
                ));
                break;

            case "ORACLE":
                config.setName("Oracle");
                config.setDriverClass("oracle.jdbc.OracleDriver");
                config.setTestQuery("SELECT 1 FROM DUAL");
                config.setDefaultProperties(createProperties(
                        "oracle.net.CONNECT_TIMEOUT=5000",
                        "oracle.jdbc.ReadTimeout=10000"
                ));
                break;

            case "SQLITE":
                config.setName("SQLite");
                config.setDriverClass("org.sqlite.JDBC");
                config.setTestQuery("SELECT 1");
                config.setDefaultProperties(createProperties());
                break;

            default:
                config.setName("Unknown Database");
                config.setDriverClass(""); // Will throw ClassNotFoundException
                config.setTestQuery(null);
                config.setDefaultProperties(new Properties());
                break;
        }

        return config;
    }

    /**
     * Creates Properties object from variable arguments of key=value strings
     *
     * @param props String array in format "key=value"
     * @return Properties object
     */
    private Properties createProperties(String... props) {
        Properties properties = new Properties();

        if (props != null) {
            for (String prop : props) {
                if (prop != null && prop.contains("=")) {
                    String[] keyValue = prop.split("=", 2);
                    if (keyValue.length == 2) {
                        properties.setProperty(keyValue[0].trim(), keyValue[1].trim());
                    }
                }
            }
        }

        // Add common default properties
        properties.setProperty("connectTimeout", "5000");
        properties.setProperty("socketTimeout", "10000");

        return properties;
    }

    /**
     * Alternative method using Map for creating properties
     */
    private Properties createPropertiesFromMap(Map<String, String> propertiesMap) {
        Properties properties = new Properties();

        if (propertiesMap != null) {
            properties.putAll(propertiesMap);
        }

        // Add default timeout properties
        properties.put("connectTimeout", "5000");
        properties.put("socketTimeout", "10000");

        return properties;
    }

    /**
     * Creates connection properties combining user credentials with
     * database-specific properties
     */
    private Properties createConnectionProperties(String username, String password, DatabaseConfig config) {
        Properties connectionProps = new Properties();

        // Add database-specific default properties
        if (config.getDefaultProperties() != null) {
            connectionProps.putAll(config.getDefaultProperties());
        }

        // Add user credentials
        if (username != null) {
            connectionProps.setProperty("user", username);
        }
        if (password != null) {
            connectionProps.setProperty("password", password);
        }

        return connectionProps;
    }

    private String detectDbType(String url) {
        if (url == null) {
            return "UNKNOWN";
        }

        String urlLower = url.toLowerCase();

        if (urlLower.startsWith("jdbc:mysql:")) {
            return "MYSQL";
        }
        if (urlLower.startsWith("jdbc:postgresql:")) {
            return "POSTGRESQL";
        }
        if (urlLower.startsWith("jdbc:sqlserver:") || urlLower.startsWith("jdbc:microsoft:sqlserver:")) {
            return "SQLSERVER";
        }
        if (urlLower.startsWith("jdbc:oracle:") || urlLower.startsWith("jdbc:oracle:thin:")) {
            return "ORACLE";
        }
        if (urlLower.startsWith("jdbc:sqlite:")) {
            return "SQLITE";
        }
        if (urlLower.startsWith("jdbc:redshift:")) {
            return "REDSHIFT";
        }

        return "UNKNOWN";
    }

    private void testConnectionWithQuery(Connection connection, String testQuery) throws SQLException {
        if (testQuery != null && !testQuery.trim().isEmpty()) {
            try ( Statement stmt = connection.createStatement();  ResultSet rs = stmt.executeQuery(testQuery)) {
                // Query executed successfully
                if (rs.next()) {
                    // Optional: Verify we got expected result
                    // For most databases, SELECT 1 returns 1
                }
            }
        }
    }

    public void addConnectionMetadata(Map<String, Object> result, Connection connection, String dbType) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            result.put("databaseProductName", metaData.getDatabaseProductName());
            result.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            result.put("driverName", metaData.getDriverName());
            result.put("driverVersion", metaData.getDriverVersion());
            result.put("jdbcVersion", metaData.getJDBCMajorVersion() + "." + metaData.getJDBCMinorVersion());

            // Get catalog/schema based on database type
            switch (dbType.toUpperCase()) {
                case "ORACLE":
                    result.put("schema", metaData.getUserName());
                    break;
                case "POSTGRESQL":
                case "SQLSERVER":
                default:
                    result.put("catalog", connection.getCatalog());
                    break;
            }

        } catch (SQLException e) {
            // Metadata retrieval failed, but connection is still valid
            result.put("metadataWarning", "Could not retrieve metadata: " + e.getMessage());
        }
    }

    public String[] getTableTypesForDatabase(String dbType) {
        switch (dbType.toUpperCase()) {
            case "MYSQL":
            case "MARIADB":
                return new String[]{"TABLE", "BASE TABLE"};
            case "POSTGRESQL":
                return new String[]{"TABLE"};
            case "SQLSERVER":
                return new String[]{"TABLE", "BASE TABLE"};
            case "ORACLE":
                return new String[]{"TABLE"};
            default:
                return new String[]{"TABLE", "BASE TABLE"};
        }
    }

    // Utility method to fix SSL configuration
    private void fixSSLConfiguration() {
        try {
            // Disable SSL certificate validation
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            System.out.println("Failed to configure SSL: " + e.getMessage());
        }
    }

}
