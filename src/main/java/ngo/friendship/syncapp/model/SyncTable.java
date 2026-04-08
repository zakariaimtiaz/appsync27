/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 *
 * @author Imtiaz
 */
public class SyncTable {

    @JsonIgnore
    private long id;

    @JsonProperty("CLIENT_CODE")
    private String clientCode;

    @JsonProperty("TBL_CODE")
    private String tblCode;

    @JsonProperty("TBL_NAME")
    private String tblName;

    @JsonProperty("SCHEMA")
    private String schema;

    @JsonProperty("TBL_TYPE")
    private String tblType;

    @JsonIgnore
    private String serverAddress;
    @JsonIgnore
    private String dbName;
    @JsonIgnore
    private String dbType;
    @JsonIgnore
    private String tblPrimaryColumn;
    @JsonIgnore
    private String tblPrimaryColumnType;
    @JsonIgnore
    private String tblSTrackingColumn;
    @JsonIgnore
    private String tblSTrackingSql;
    @JsonIgnore
    private String tblCTrackingColumn;

    @JsonIgnore
    private String selectSql;

    @JsonIgnore
    private String upsertSql;

    @JsonIgnore
    private long priority;

    @JsonProperty("CHUNK_SIZE")
    private long chunkSize;

    @JsonProperty("VERSION_NO")
    private long versionNo;

    public SyncTable() {

    }

    public SyncTable(long id, String tblCode, String tblName, String tblType, String schema, String serverAddress,
                     String dbName, String dbType, String tblPrimaryColumn, String tblPrimaryColumnType,
                     long priority, long chunkSize, String selectSql, String upsertSql, String tblSTrackingColumn,
                     String tblSTrackingSql, String tblCTrackingColumn) {
        this.id = id;
        this.tblCode = tblCode;
        this.tblName = tblName;
        this.tblType = tblType;
        this.schema = schema;
        this.serverAddress = serverAddress;
        this.dbName = dbName;
        this.dbType = dbType;
        this.tblPrimaryColumn = tblPrimaryColumn;
        this.tblPrimaryColumnType = tblPrimaryColumnType;
        this.tblSTrackingColumn = tblSTrackingColumn;
        this.tblSTrackingSql = tblSTrackingSql;
        this.tblCTrackingColumn = tblCTrackingColumn;
        this.priority = priority;
        this.chunkSize = chunkSize;
        this.selectSql = selectSql;
        this.upsertSql = upsertSql;
    }

    public SyncTable(String tblCode, String tblName, String tblType, String schema, String dbType,
                     String tblPrimaryColumn, String tblPrimaryColumnType, long chunkSize) {
        this.tblCode = tblCode;
        this.tblName = tblName;
        this.tblType = tblType;
        this.schema = schema;
        this.dbType = dbType;
        this.tblPrimaryColumn = tblPrimaryColumn;
        this.tblPrimaryColumnType = tblPrimaryColumnType;
        this.chunkSize = chunkSize;
    }

    @Override
    public String toString() {
        return "[" + tblCode + "]" + tblName; //To change body of generated methods, choose Tools | Templates.
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTblCode() {
        return tblCode;
    }

    public void setTblCode(String tblCode) {
        this.tblCode = tblCode;
    }

    public String getTblName() {
        return tblName;
    }

    public String getTblNameWithSchema() {
        return (schema != null && schema.trim().length() > 0) ? schema + "." + tblName : tblName;
    }

    public void setTblName(String tblName) {
        this.tblName = tblName;
    }

    public String getTblType() {
        return tblType;
    }

    public void setTblType(String tblType) {
        this.tblType = tblType;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getTblPrimaryColumn() {
        return tblPrimaryColumn;
    }

    public void setTblPrimaryColumn(String tblPrimaryColumn) {
        this.tblPrimaryColumn = tblPrimaryColumn;
    }

    public String getTblPrimaryColumnType() {
        return tblPrimaryColumnType;
    }

    public void setTblPrimaryColumnType(String tblPrimaryColumnType) {
        this.tblPrimaryColumnType = tblPrimaryColumnType;
    }

    public String getTblSTrackingColumn() {
        return tblSTrackingColumn;
    }

    public void setTblSTrackingColumn(String tblSTrackingColumn) {
        this.tblSTrackingColumn = tblSTrackingColumn;
    }

    public String getTblSTrackingSql() {
        return tblSTrackingSql;
    }

    public void setTblSTrackingSql(String tblSTrackingSql) {
        this.tblSTrackingSql = tblSTrackingSql;
    }

    public String getTblCTrackingColumn() {
        return tblCTrackingColumn;
    }

    public void setTblCTrackingColumn(String tblCTrackingColumn) {
        this.tblCTrackingColumn = tblCTrackingColumn;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public long getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(long versionNo) {
        this.versionNo = versionNo;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSchema() {
        return schema;
    }

    public void setSelectSql(String selectSql) {
        this.selectSql = selectSql;
    }

    public void setUpsertSql(String upsertSql) {
        this.upsertSql = upsertSql;
    }

    public String getSelectSql() {
        return loadTableName(selectSql);
    }

    public String getUpsertSql() {
        return loadTableName(upsertSql);
    }

    private String loadTableName(String sql) {
        if (sql != null && sql.contains(":table")) {
            sql = sql.replace(":table", getTblNameWithSchema());
        }
        return sql;
    }

    public String getDownloadUri() {
        return serverAddress + String.format("/sync/download");
    }

    public String getUploadUri() {
        return serverAddress + String.format("/sync/upload/%s/%s", clientCode, tblCode);
    }

    @JsonIgnore
    private long startTime;
    @JsonIgnore
    private long endTime;
    @JsonIgnore
    private long noOfFatch;
    @JsonIgnore
    private long noOfComplete;
    @JsonIgnore
    private long status;
    @JsonIgnore
    private long note;

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setNoOfFatch(long noOfFatch) {
        this.noOfFatch = noOfFatch;
    }

    public void setNoOfComplete(long noOfComplete) {
        this.noOfComplete = noOfComplete;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public void setNote(long note) {
        this.note = note;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getNoOfFatch() {
        return noOfFatch;
    }

    public long getNoOfComplete() {
        return noOfComplete;
    }

    public long getStatus() {
        return status;
    }

    public long getNote() {
        return note;
    }

    public String toStringStartDownload() {
        return toStringStart("Download");
    }

    public String toStringStartUpload() {
        return toStringStart("Upload");
    }

    public String toStringEndDownload() {
        return toStringEnd("Download");
    }

    public String toStringEndUpload() {
        return toStringEnd("Upload");
    }

    private String toStringStart(String txt) {
        return String.format("==========================%s--%s Start at %s=======================", toString(), txt, (new Date(startTime)).toString());
    }

    public String toStringEnd(String txt) {
        return String.format("==========================%s--%s End at %s Need %d=======================", toString(), txt, (new Date(startTime)).toString(), (endTime - startTime));
    }

}
