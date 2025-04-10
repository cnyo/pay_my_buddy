package com.yoann.pay_my_buddy.unit.service;

import com.yoann.pay_my_buddy.PayMyBuddyApplication;
import com.yoann.pay_my_buddy.schedulingtasks.ScheduledTask;
import com.yoann.pay_my_buddy.service.ScheduledTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PayMyBuddyApplication.class)
public class ScheduledTaskTest {
//    @Autowired
//    private ScheduledTask scheduledTasks;
//
//    @MockitoBean
//    private ScheduledTaskService scheduledTaskService;
//
//    @Test
//    public void backupShouldFailWhenIOException() throws IOException, InterruptedException {
//        when(scheduledTaskService.backupDataBase(anyString(), anyString())).thenThrow(IOException.class);
//
//        boolean result = scheduledTasks.backupDataBaseTask();
//
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    public void backupShouldFailWhenInterruptedException() throws IOException, InterruptedException {
//        when(scheduledTaskService.backupDataBase(anyString(), anyString())).thenThrow(InterruptedException.class);
//
//        boolean result = scheduledTasks.backupDataBaseTask();
//
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    public void backupShouldFailWhenAlreadyExistsBackupFile() throws IOException, InterruptedException {
//        when(scheduledTaskService.backupDataBase(anyString(), anyString())).thenThrow(FileAlreadyExistsException.class);
//
//        boolean result = scheduledTasks.backupDataBaseTask();
//
//        assertThat(result).isFalse();
//    }
}
