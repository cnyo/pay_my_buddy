package com.yoann.pay_my_buddy.schedulingtasks;

import com.yoann.pay_my_buddy.service.ScheduledTaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

//    @Value("${pay_my_buddy.backup.path}")
//    private String BACKUP_PATH;

    private final String BACKUP_PATH = "../backup/backup.dump";

    private final ScheduledTaskService scheduledTaskService;

    private static final Logger log = LogManager.getLogger(ScheduledTask.class);

    public ScheduledTask(ScheduledTaskService scheduledTaskService) {
        this.scheduledTaskService = scheduledTaskService;
    }

    @Scheduled(fixedRate = 5000)
    public boolean backupDataBaseTask() {
        try {
            scheduledTaskService.backupDataBase(BACKUP_PATH);
            log.info("Execute Backup DataBase Task successfully");

            return true;
        } catch (InterruptedException e) {
            log.error("Execute Backup DataBase Task failed because interrupted exception", e);

            return false;
        } catch (Exception e) {
            log.error("Execute Backup DataBase Task failed", e);

            return false;
        }
    }

}
