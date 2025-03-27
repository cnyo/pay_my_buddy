package com.yoann.pay_my_buddy.unit;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PayMyBuddyApplication.class)
public class ScheduledTaskTest {
    @Autowired
    ScheduledTask scheduledTasks;

    @MockitoBean
    ScheduledTaskService scheduledTaskService;

    @Test
    public void backupShouldSuccess() {
        when(scheduledTaskService.isBackupSuccess(anyString())).thenReturn(true);

        boolean result = scheduledTasks.backupDataBaseTask();

        assertThat(result).isTrue();
    }

    @Test
    public void backupShouldFailWhenIOException() throws IOException, InterruptedException {
        when(scheduledTaskService.backupDataBase(anyString())).thenThrow(IOException.class);

        boolean result = scheduledTasks.backupDataBaseTask();

        assertThat(result).isFalse();
    }

    @Test
    public void backupShouldFailWhenInterruptedException() throws IOException, InterruptedException {
        when(scheduledTaskService.backupDataBase(anyString())).thenThrow(InterruptedException.class);

        boolean result = scheduledTasks.backupDataBaseTask();

        assertThat(result).isFalse();
    }
}
