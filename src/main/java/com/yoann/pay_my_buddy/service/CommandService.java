package com.yoann.pay_my_buddy.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CommandService {

    private static final Logger log = LogManager.getLogger(CommandService.class);

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
