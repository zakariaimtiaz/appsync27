/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngo.friendship.syncapp.api;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import ngo.friendship.syncapp.Bootstrap;
import ngo.friendship.syncapp.model.Response;
import ngo.friendship.syncapp.model.SyncTable;
import ngo.friendship.syncapp.repo.LocalRepo;
import ngo.friendship.syncapp.repo.MainRepo;
import static ngo.friendship.syncapp.util.Constant.CLIENT_CODE;
import static ngo.friendship.syncapp.util.Constant.TBL_CLIENT;
import static ngo.friendship.syncapp.util.Constant.TBL_CODE;
import static ngo.friendship.syncapp.util.Constant.TBL_MASTER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Imtiaz
 */
@RestController
@RequestMapping("/sync")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    @Autowired
    HttpServletRequest request;

    @Autowired
    LocalRepo localRepo;

    @Autowired
    MainRepo mainRepo;

    @Autowired
    private Bootstrap component;

    @RequestMapping(value = "/download", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Response downloadMasterTableData(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        try {
            session.invalidate();
            String clientCode = request.get("CLIENT_CODE").toString();
            String tableCode = request.get("TBL_CODE").toString();
            long versionNo = Long.parseLong(request.get("VERSION_NO").toString());
            long chunkSize = Long.parseLong(request.get("CHUNK_SIZE").toString());

            log.info(String.format("Download request from %s for %s [Version No: %s][Chunk size: %s]", clientCode, tableCode, versionNo, chunkSize));
            if (localRepo.isClient(clientCode)) {
                log.info("Client identified");
                SyncTable table = localRepo.getSyncableTable(request.get("TBL_CODE").toString(), TBL_MASTER);
                if (table != null) {
                    log.info("SyncTable found...." + table.getDbName());
                    table.setChunkSize(chunkSize);
                    table.setVersionNo(versionNo);
                    DataSource dataSource = component.getDataSource(table.getDbName());
                    if (dataSource != null) {
                        List<Map<String, Object>> rows = mainRepo.getTableData(dataSource, table);
                        log.info(String.format("%s successfully data fetch [ %s ]", table.toString(), rows.size()));
                        return Response.OK(rows);
                    } else {
                        log.warn(table.toString() + " datasource is null");
                        return Response.ERROR("Datasource is null");
                    }
                } else {
                    log.warn(String.format("Table not registered", tableCode));
                    return Response.ERROR("Table not registered");
                }

            } else {
                log.warn(String.format("Client not registered", clientCode));
                return Response.ERROR("Client not registered");
            }
        } catch (NumberFormatException ex) {
            log.warn(String.format("Exceptions: ", ex.getMessage()));
            return Response.ERROR(ex.getMessage());
        }
    }

    @RequestMapping(value = "/upload/{" + CLIENT_CODE + "}/{" + TBL_CODE + "}",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Response uploadClientTableData(
            @PathVariable(CLIENT_CODE) String clientCode,
            @PathVariable(TBL_CODE) String tableCode,
            @RequestBody List<Map<String, Object>> datas,
            HttpSession session) {

        try {
            session.invalidate();
            log.info(String.format("upload request from %s for %s [%s rows]", clientCode, tableCode, datas.size()));
            if (localRepo.isClient(clientCode)) {
                SyncTable table = localRepo.getSyncableTable(tableCode, TBL_CLIENT);
                if (table != null) {
                    DataSource dataSource = component.getDataSource(table.getDbName());
                    if (dataSource != null) {
                        List<String> comRef = mainRepo.upsert(dataSource, table, datas);
                        log.info(String.format("%s upsert  successfully  [%s rows]", table.toString(), comRef.size()));
                        return Response.OK(comRef);
                    } else {
                        log.warn(table.toString() + " datasource is null");
                        return Response.ERROR("Datasource is null");
                    }
                } else {
                    log.warn(String.format("Table not registered", tableCode));
                    return Response.ERROR("Table not registered");
                }

            } else {
                log.warn(String.format("Client not registered", clientCode));
                return Response.ERROR("Client not registered");
            }
        } catch (Exception ex) {
            log.warn(String.format("Exceptions: ", ex.getMessage()));
            return Response.ERROR(ex.getMessage());
        }

    }

}
