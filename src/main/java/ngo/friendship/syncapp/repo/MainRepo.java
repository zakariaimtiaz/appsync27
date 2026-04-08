package ngo.friendship.syncapp.repo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import ngo.friendship.syncapp.config.SqlBuilderComponent;
import ngo.friendship.syncapp.model.SyncTable;
import ngo.friendship.syncapp.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class for main database operations related to synchronization.
 * Provides methods to fetch, update, and upsert data in the database.
 * 
 */
@Repository
public class MainRepo {

    private static final Logger log = LoggerFactory.getLogger(MainRepo.class);

    @Autowired
    private SqlBuilderComponent sqlBuilder;

    /**
     * Fetches data from the specified table using the provided DataSource.
     * 
     * @param dataSource the DataSource to connect to the database
     * @param table the SyncTable containing table details
     * @return a list of maps representing the table data
     */
    public List<Map<String, Object>> getTableData(DataSource dataSource, SyncTable table) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> param = new HashMap<>();
        param.put("chunk_size", table.getChunkSize());
        param.put("version_no", table.getVersionNo());
        log.info(table.getSelectSql());
        return jdbcTemplate.queryForList(table.getSelectSql(), param);
    }

    /**
     * Updates the sent flag status to complete for the specified table.
     * 
     * @param dataSource the DataSource to connect to the database
     * @param table the SyncTable containing table details
     * @param comRef the list of completion references
     * @return true if the update was successful, false otherwise
     */
    @Transactional
    public boolean updateSentStatusComplete(DataSource dataSource, SyncTable table, List<String> comRef) {
        int[] updateCounts = new JdbcTemplate(dataSource).batchUpdate(sqlBuilder.getClientTableTaskSetCompleteTemplete(table),
                new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) {
                try {
                    ps.setString(1, comRef.get(i));
                } catch (SQLException ex) {
                    log.error("Error setting values in PreparedStatement", ex);
                }
            }

            @Override
            public int getBatchSize() {
                return comRef.size();
            }
        });
        return true;
    }

    /**
     * Performs an upsert operation on the specified table.
     * 
     * @param dataSource the DataSource to connect to the database
     * @param table the SyncTable containing table details
     * @param rows the list of rows to upsert
     * @return a list of completion references
     */
    @Transactional
    public List<String> upsert(DataSource dataSource, SyncTable table, List<Map<String, Object>> rows) {
        List<String> completeRef = new ArrayList<>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);

        for (Map<String, Object> row : rows) {
            log.debug("Upserting row: {}", row);
            if (template.update(table.getUpsertSql(), row) > 0) {
                try {
                    completeRef.add((String) row.get(table.getTblPrimaryColumn()));
                } catch (Exception e) {
                    completeRef.add(String.valueOf(row.get(table.getTblPrimaryColumn())));
                }
            }
        }
        return completeRef;
    }

    /**
     * Gets the maximum version number from the specified table.
     * 
     * @param dataSource the DataSource to connect to the database
     * @param table the SyncTable containing table details
     * @return the maximum version number
     */
    public long getMaxSTrackingIndex(DataSource dataSource, SyncTable table) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sqlStr = table.getTblSTrackingSql();
        if (sqlStr == null || sqlStr.isEmpty()) {
            String key = table.getTblSTrackingColumn();
            if (key == null || key.isEmpty()) {
                key = Constant.VERSION_NO;
            }
            sqlStr = "SELECT MAX(" + key.toUpperCase() + ") FROM " + table.getTblNameWithSchema();
        }
        Long versionNo = jdbcTemplate.queryForObject(sqlStr, Long.class);
        return versionNo == null ? 0 : versionNo;
    }
}
