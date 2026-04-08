package ngo.friendship.syncapp;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import ngo.friendship.syncapp.model.AppSync;
import ngo.friendship.syncapp.repo.LocalRepo;
import ngo.friendship.syncapp.service.DownloadFromMasterService;
import ngo.friendship.syncapp.service.UploadToMasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@PropertySource("classpath:application.properties")
@Component
public class Bootstrap {

    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    private final ScheduledExecutorService scheduledExecutorService
            = Executors.newSingleThreadScheduledExecutor();

    private final ExecutorService executorService
            = Executors.newSingleThreadExecutor();

    private volatile boolean shuttingDown = false;
    private Future<?> syncTask;

    @Autowired
    private DownloadFromMasterService downloadService;
    @Autowired
    private UploadToMasterService uploadService;
    @Autowired
    private LocalRepo localRepo;

    @Autowired
    @Qualifier("DB_TYPE")
    private Map<String, String> dblist;

    @Value("${data.sync.cron.enable}")
    private boolean dataSyncCronEnable;

    private static final Map<String, DataSource> DS_REPO
            = new ConcurrentHashMap<>();

    /* ---------------------------------------------------- */
    @PostConstruct
    public void init() {
        scheduledExecutorService.schedule(() -> {
            if (!shuttingDown) {
                loadAllDataSource();
            }
        }, 5, TimeUnit.SECONDS);
    }

    /* ---------------------------------------------------- */
    public DataSource getDataSource(String sourceName) {
        DataSource ds = DS_REPO.get(sourceName);
        if (ds == null) {
            log.warn("{} database not available", sourceName);
        }
        return ds;
    }

    private DataSource getDataSource(
            String driver, String url, String user, String pass) {

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        return ds;
    }

    public void loadAllDataSource() {
        if (shuttingDown) {
            return;
        }

        DS_REPO.clear();

        List<Map<String, Object>> dbConfigs
                = localRepo.getAllActiveDbComnfig();

        for (Map<String, Object> row : dbConfigs) {
            String name = (String) row.get("CONFIG_NAME");

            DataSource ds = getDataSource(
                    dblist.get((String) row.get("DB_TYPE")),
                    (String) row.get("DB_URL"),
                    (String) row.get("DB_USER_NAME"),
                    (String) row.get("DB_PASSWORD")
            );

            DS_REPO.put(name, ds);
        }
    }

    /* ---------------------------------------------------- */
    @Scheduled(cron = "${data.sync.cron.prop}")
    public void start() {
        if (shuttingDown || !dataSyncCronEnable) {
            return;
        }

        if (syncTask != null && !syncTask.isDone()) {
            return;
        }

        syncTask = executorService.submit(() -> {
            try {
                Date startTime = Calendar.getInstance().getTime();
                log.info("Scheduler executed at {}", startTime);

                AppSync app = localRepo.getAppSync();
                if (app.isActive() && app.isClient()) {
                    downloadService.start(app);
                    TimeUnit.SECONDS.sleep(5);
                    uploadService.start(app);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Data sync error", e);
            }
        });
    }

    /* ---------------------------------------------------- */
    @PreDestroy
    public void shutdown() {
        shuttingDown = true;

        if (syncTask != null) {
            syncTask.cancel(true);
        }

        scheduledExecutorService.shutdownNow();
        executorService.shutdownNow();

        DS_REPO.clear();

        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Exception e) {
            log.warn("MySQL cleanup thread issue", e);
        }
    }
}
