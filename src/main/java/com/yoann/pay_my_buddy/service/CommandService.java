package com.yoann.pay_my_buddy.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Service for executing system-level commands.
 *
 * This service is specifically designed to perform PostgreSQL database backups
 * by executing the `pg_dump` utility using the provided credentials.
 */
@Service
public class CommandService {

    private static final Logger log = LogManager.getLogger(CommandService.class);

    /**
     * Executes a PostgreSQL database backup command using `pg_dump`.
     * <p>
     * The backup is performed by running a system process. The database
     * credentials and output file path are provided as parameters.
     * </p>
     *
     * @param username the PostgreSQL username
     * @param password the PostgreSQL password
     * @param path     the absolute path to the output backup file (e.g., "/tmp/backup.sql")
     * @return the exit code of the process (0 if successful)
     * @throws RuntimeException if an error occurs while executing the command
     *                          or if the process returns a non-zero exit code
     */
    public int executeBackupCmd(String username, String password, String path) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(
                    "pg_dump",
                    String.format( "--dbname=postgresql://%s:%s@localhost:5432/pay_my_buddy", username, password),
                    "--dbname=postgresql://"+username+":"+password+"@localhost:5432/pay_my_buddy",
                    "-f",
                    path
            );
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
//        List<String> results = readOutput(process.getInputStream());

            int outCode = process.waitFor();

            if (outCode != 0) {
                String errorMessage = new String(process.getErrorStream().readAllBytes());
                throw new RuntimeException(errorMessage);
            }

            return outCode;
        } catch (Exception e) {
            log.error("Error while executing backup command: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
