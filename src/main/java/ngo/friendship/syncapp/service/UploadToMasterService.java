package ngo.friendship.syncapp.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.sql.DataSource;
import ngo.friendship.syncapp.Bootstrap;
import ngo.friendship.syncapp.config.TrustAllCertificates;
import ngo.friendship.syncapp.model.SyncTable;
import ngo.friendship.syncapp.repo.MainRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ngo.friendship.syncapp.model.AppSync;
import ngo.friendship.syncapp.model.Response;
import ngo.friendship.syncapp.repo.LocalRepo;
import static ngo.friendship.syncapp.util.Constant.TBL_CLIENT;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@PropertySource(value = "classpath:application.properties")
public class UploadToMasterService {

    private static final Logger log = LoggerFactory.getLogger(UploadToMasterService.class);

    @Value("${data.sync.handshake.token}")
    private String API_BEARER_TOKEN;

    @Autowired
    private Bootstrap component;

    @Autowired
    private LocalRepo localRepo;

    @Autowired
    private MainRepo mainRepo;

    public void start(AppSync app) { 
        log.info("########## ClientTableUploadService ###########");
        String targetUrl = localRepo.getSvrAddress();
        if (!targetUrl.isEmpty()) {
            Date startTime = Calendar.getInstance().getTime();
            log.info("##########################################################");
            log.info("Client table upload service start at-{}", startTime);
            localRepo.getSyncableTables(TBL_CLIENT).forEach(item -> uploadTable(item, app));
            Date endTime = Calendar.getInstance().getTime();
            log.info("Client table upload service end at-{} Need[{} ms]", endTime, (endTime.getTime() - startTime.getTime()));
            log.info("##########################################################");
        }
    }

    public void uploadTable(SyncTable table, AppSync app) {
        table.setStartTime(Calendar.getInstance().getTimeInMillis());
        log.info("Start uploading table: {}", table.toStringStartUpload());

        DataSource dataSource = component.getDataSource(table.getDbName());
        if (dataSource != null) {
            try {
                table.setClientCode(app.getCode());
                List<Map<String, Object>> rows = mainRepo.getTableData(dataSource, table);
                log.info("{} unsent data found: {}", table.toString(), rows.size());
                if (!rows.isEmpty()) {
                    sendUploadRequest(table, rows);
                } else {
                    table.setEndTime(Calendar.getInstance().getTimeInMillis());
                    log.info("End uploading table: {}", table.toStringEndUpload());
                }
            } catch (Exception ex) {
                log.error("Error processing table {}: {}", table.toString(), ex.getMessage(), ex);
            }
        } else {
            log.warn("DataSource is null for table: {}", table.toString());
            table.setEndTime(Calendar.getInstance().getTimeInMillis());
            log.info("End uploading table: {}", table.toStringEndUpload());
        }
    }

    private void sendUploadRequest(SyncTable table, List<Map<String, Object>> rows) {
        log.info("Sending upload request for table: {} to URI: {}", table.toString(), table.getUploadUri());
        try ( CloseableHttpClient httpClient = createHttpClient()) {
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
            HttpHeaders headers = createHeaders();
            HttpEntity<List<Map<String, Object>>> requestEntity = new HttpEntity<>(rows, headers);
            ResponseEntity<Response> responseEntity = restTemplate.postForEntity(table.getUploadUri(), requestEntity, Response.class);

            handleResponse(responseEntity, table, rows);
        } catch (ResourceAccessException e) {
            log.warn("API Error: Unable to connect to the target URL. Message: {}", e.getMessage(), e);
        } catch (RestClientException e) {
            log.warn("API Error: An HTTP request exception occurred. Message: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.warn("API Error: An unexpected exception occurred. Message: {}", e.getMessage(), e);
        } finally {
            table.setEndTime(Calendar.getInstance().getTimeInMillis());
            log.info("End uploading table: {}", table.toStringEndUpload());
        }
    }

    private CloseableHttpClient createHttpClient() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new TrustAllCertificates()}, null);
        return HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_BEARER_TOKEN);
        return headers;
    }

    private void handleResponse(ResponseEntity<Response> responseEntity, SyncTable table, List<Map<String, Object>> rows) {
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Response responseBody = responseEntity.getBody();
            if (responseBody != null && responseBody.isOK()) {
                List<String> comRef = (List<String>) responseBody.getData();
                log.info("Data uploaded successfully for table: {}. Number of rows: {}", table.toString(), comRef.size());
                mainRepo.updateSentStatusComplete(component.getDataSource(table.getDbName()), table, comRef);
                log.info("Sent flag status updated for table: {}. Number of rows: {}", table.toString(), comRef.size());
                uploadTable(table, localRepo.getAppSync());
            } else {
                log.warn("Upload request error for table: {}. Error: {}", table.toString(), responseBody != null ? responseBody.toStringError() : "Response body is null");
            }
        } else {
            log.warn("Upload request error for table: {}. HTTP status: {}", table.toString(), responseEntity.getStatusCode());
        }
    }
}
