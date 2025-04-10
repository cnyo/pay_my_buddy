package com.yoann.pay_my_buddy.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ScheduledTaskService {
    private final String CREATE_CONNECTION_USER_TABLE = "CREATE TABLE public.connection_user";
    private final long MINI_BYTES_BACKUP_FILE = 1000;
    private final int LIMIT_NBR_BACKUP = 4;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Autowired
    private ReaderService readerService;

    @Autowired
    private CommandService commandService;

    private static final Logger log = LogManager.getLogger(ScheduledTaskService.class);

    public int backupDataBase(String directory, String path) throws InterruptedException, IOException, FileAlreadyExistsException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = LocalDateTime.now().format(formatter);
        String pathToBackup = Path.of(directory, String.format("%s_%s", path, date)).normalize().toString();

        File backupFile = new File(pathToBackup);

        if (backupFile.exists()) {
            throw new FileAlreadyExistsException(pathToBackup);
        }

        purgeOldestFile(pathToBackup, LIMIT_NBR_BACKUP);

        int outCode = commandService.executeBackupCmd(databaseUsername, databasePassword, pathToBackup);

        if (!isContentBackupFileValid(backupFile.getPath()) || isFileSizeValid(backupFile)) {
            String msg = "Backup file does not exist";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        return outCode;
    }

    public boolean isContentBackupFileValid(String backupPath) throws RuntimeException {
        log.debug("isContentBackupFileValid start");

        try (BufferedReader reader = readerService.createFileBufferedReader(backupPath)) {
            log.debug("reader backup file created");

            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("reading line {}", lineNumber);

                if (line.contains(CREATE_CONNECTION_USER_TABLE)) {
                    log.debug("content backup file is valid", lineNumber);
                    return true;
                }

                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            log.error("Backup file not found", e);
            throw new RuntimeException("Backup file not found");
        } catch (Exception e) {
            log.error("Error while reading backup file", e);
            throw new RuntimeException(e);
        }

        return false;
    }

    public boolean isFileSizeValid(File file) {
        return file.length() > MINI_BYTES_BACKUP_FILE;
    }

    /**
     * Supprime le plus ancien fichier de backup
     *
     * @param directory
     * @param fileNumberLimit
     * @return
     */
    public long purgeOldestFile(String directory, int fileNumberLimit) {
        File[] files = new File(directory).listFiles();

        if (files == null) {
            return 0;
        }

        long nbrFiles = files.length;
        long oldestDate = Long.MAX_VALUE;
        File oldestFile = null;

        if (nbrFiles >= fileNumberLimit ) {
            for (File file : files) {
                if (file.lastModified() < oldestDate) {
                    oldestDate = file.lastModified();
                    oldestFile = file;
                }
            }

            if (oldestFile != null) {
                oldestFile.delete();
            }
        }

        return nbrFiles;
    }
}
