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
import ngo.friendship.syncapp.model.AppSync;
import ngo.friendship.syncapp.model.Response;
import ngo.friendship.syncapp.model.SyncTable;
import ngo.friendship.syncapp.repo.LocalRepo;
import ngo.friendship.syncapp.repo.MainRepo;
import static ngo.friendship.syncapp.util.Constant.TBL_MASTER;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@PropertySource(value = "classpath:application.properties")
public class DownloadFromMasterService {

    private static final Logger log = LoggerFactory.getLogger(DownloadFromMasterService.class);

    @Value("${data.sync.handshake.token}")
    private String API_BEARER_TOKEN;

    @Autowired
    private Bootstrap component;

    @Autowired
    private LocalRepo localRepo;

    @Autowired
    private MainRepo mainRepo;

    public void start(AppSync app) {
        log.info("########## MasterTableDownloadService ###########");
        String targetUrl = localRepo.getSvrAddress();
        if (!targetUrl.isEmpty()) {
            Date startTime = Calendar.getInstance().getTime();
            log.info("##########################################################");
            log.info("Master table download service start at {}", startTime);
            localRepo.getSyncableTables(TBL_MASTER).forEach((item) -> downloadTable(item, app));
            Date endTime = Calendar.getInstance().getTime();
            log.info("Master table download service end at {}. Duration: {} ms", endTime, (endTime.getTime() - startTime.getTime()));
            log.info("##########################################################");
        }
    }

    public void downloadTable(SyncTable table, AppSync app) {
        table.setStartTime(Calendar.getInstance().getTimeInMillis());
        log.info("Start downloading table: {}", table.toStringStartDownload());

        DataSource dataSource = component.getDataSource(table.getDbName());
        if (dataSource == null) {
            log.warn("DataSource is null for table: {}", table.toString());
            return;
        }

        try {
            long maxT = mainRepo.getMaxSTrackingIndex(dataSource, table);
            table.setVersionNo(maxT);
            table.setClientCode(app.getCode());

            String downloadUri = table.getDownloadUri();
            log.info("Sending download request for table: {} to URI: {}",
                    table.toString(), downloadUri);

            try ( CloseableHttpClient httpClient = buildHttpClient(downloadUri)) {

                HttpComponentsClientHttpRequestFactory requestFactory
                        = new HttpComponentsClientHttpRequestFactory(httpClient);

                RestTemplate restTemplate = new RestTemplate(requestFactory);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + API_BEARER_TOKEN);

                HttpEntity<SyncTable> requestEntity
                        = new HttpEntity<>(table, headers);

                ResponseEntity<Response> responseEntity
                        = restTemplate.postForEntity(downloadUri, requestEntity, Response.class);

                handleResponse(responseEntity, table, dataSource, app);
            }

        } catch (ResourceAccessException e) {
            log.warn("API Error: Unable to connect to the target URL. Message: {}", e.getMessage());
        } catch (RestClientException e) {
            log.warn("API Error: HTTP request exception occurred. Message: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("API Error: Unexpected exception occurred. Message: {}", e.getMessage());
        } finally {
            table.setEndTime(Calendar.getInstance().getTimeInMillis());
            log.info("End downloading table: {}", table.toStringEndDownload());
        }
    }

    private void handleResponse(ResponseEntity<Response> responseEntity, SyncTable table, DataSource dataSource, AppSync app) {
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Response responseBody = responseEntity.getBody();
            if (responseBody != null && responseBody.isOK()) {
                List<Map<String, Object>> rows = (List<Map<String, Object>>) responseBody.getData();
                log.info("Data fetched successfully for table: {}. Number of rows: {}", table.toString(), rows.size());
                if (!rows.isEmpty()) {
                    List<String> ids = mainRepo.upsert(dataSource, table, rows);
                    log.info("Data saved successfully for table: {}. Number of rows: {}", table.toString(), ids.size());
                }
                if (rows.size() == table.getChunkSize()) {
                    log.info("Chunk size matched, calling download again for table: {}", table.toString());
                    downloadTable(table, app);
                }
            } else {
                log.warn("Download request error for table: {}. Error: {}", table.toString(), responseBody != null ? responseBody.toStringError() : "Response body is null");
            }
        } else {
            log.warn("Download request error for table: {}. HTTP status: {}", table.toString(), responseEntity.getStatusCode());
        }
    }

    private CloseableHttpClient buildHttpClient(String url) throws Exception {
        if (isHttps(url)) {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new TrustAllCertificates()}, null);

            return HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
        }

        // HTTP → no SSL
        return HttpClients.createDefault();
    }

    private boolean isHttps(String url) {
        return url != null && url.toLowerCase().startsWith("https://");
    }

}
