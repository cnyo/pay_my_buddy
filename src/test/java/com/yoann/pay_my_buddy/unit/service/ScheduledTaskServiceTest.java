package com.yoann.pay_my_buddy.unit.service;

import com.yoann.pay_my_buddy.PayMyBuddyApplication;
import com.yoann.pay_my_buddy.service.CommandService;
import com.yoann.pay_my_buddy.service.ReaderService;
import com.yoann.pay_my_buddy.service.ScheduledTaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PayMyBuddyApplication.class)
@TestPropertySource(properties = {
        "server.param1=srv1",
        "server.param2=srv2"
})
public class ScheduledTaskServiceTest {
//    private final String BACKUP_TEMP_DIRECTORY = "backup_test";
//    private final String DEFAULT_BACKUP_PATH = "../backup/test/backup.dump";
//    private final String TEST_BACKUP_PATH = "testBackupFile";
//
//    @InjectMocks
//    private ScheduledTaskService scheduledTaskService;
//
//    @Mock
//    private ReaderService mockFileReaderService;
//
//    @Mock
//    private ProcessBuilder mockProcessBuilder;
//
//    @Mock
//    private Process mockProcess;
//
//    @Mock
//    private BufferedReader mockBufferedReader;
//
//    @Mock
//    private File mockFile;
//
//    @Mock
//    private CommandService mockCommandService;
//
//    @AfterEach
//    public void tearDown() {
////        File backupFile = new File(DEFAULT_BACKUP_PATH);
////        boolean result = backupFile.delete();
////
////        System.out.println("Backup deleted: " + result);
//
//
////        Files.walk(tempDirectoryPath.toPath())
////                .sorted(Comparator.reverseOrder())
////                .map(Path::toFile)
////                .forEach(File::delete);
//    }
//
//    @Tag("Reading the backup file should confirm the validity SQL of the file")
//    @Test
//    public void readingBackupFileShouldConfirmValiditySqlFile() throws IOException {
//        Reader inputStringReader = new StringReader( "CREATE TABLE public.connection_user");
//        BufferedReader bufferedReader = new BufferedReader(inputStringReader);
//
//        when(mockFileReaderService.createFileBufferedReader(anyString())).thenReturn(bufferedReader);
//
//        boolean result = scheduledTaskService.isContentBackupFileValid(DEFAULT_BACKUP_PATH);
//
//        assertThat(result).isTrue();
//    }
//
//    @Tag("Reading the backup file should confirm the invalid SQL of the file")
//    @Test
//    public void readingBackupFileShouldInvalidateFile() throws IOException {
//        Reader inputStringReader = new StringReader( "CREATE TABLE public.connection_mail");
//        BufferedReader bufferedReader = new BufferedReader(inputStringReader);
//
//        when(mockFileReaderService.createFileBufferedReader(anyString())).thenReturn(bufferedReader);
//
//        boolean result = scheduledTaskService.isContentBackupFileValid(DEFAULT_BACKUP_PATH);
//
//        assertThat(result).isFalse();
//    }
//
//    @Tag("Reading none exists backup file should throw exception")
//    @Test
//    public void readingNoneExistsBackupFileShouldThrowException() throws FileNotFoundException {
//        when(mockFileReaderService.createFileBufferedReader(anyString())).thenThrow(new FileNotFoundException());
//
//        assertThrows(RuntimeException.class, () -> scheduledTaskService.isContentBackupFileValid(DEFAULT_BACKUP_PATH));
//    }
//
//    @Tag("The backup size must be valid")
//    @Test
//    public void whenBackupSizeIsValidShouldReturnTrue() throws IOException {
//        File file = File.createTempFile(TEST_BACKUP_PATH, ".tmp");
//        Files.write(file.toPath(), new byte[(int) 1001]);
//        file.deleteOnExit();
//
//        boolean result = scheduledTaskService.isFileSizeValid(file);
//
//        assertThat(result).isTrue();
//    }
//
//    @Tag("The backup size must be invalid")
//    @Test
//    public void whenBackupSizeIsInvalidShouldReturnFalse() throws IOException {
//        File file = File.createTempFile(TEST_BACKUP_PATH, ".tmp");
//        Files.write(file.toPath(), new byte[(int) 999]);
//        file.deleteOnExit();
//
//        boolean result = scheduledTaskService.isFileSizeValid(file);
//
//        assertThat(result).isFalse();
//    }
//
//    @Tag("Deleting the oldest file should be successful")
//    @Test
//    public void whenDeleteOldestBackupFileShouldReturnGoodNbrFile() throws IOException, InterruptedException {
//        Path path = Files.createTempDirectory(BACKUP_TEMP_DIRECTORY);
//        File tempDirectoryPath = path.toFile();
//        String tempDirectory = path.toFile().getAbsolutePath();
//
//        File.createTempFile("test_premier", ".tmp", tempDirectoryPath);
//        File.createTempFile("test_second", ".tmp", tempDirectoryPath);
//
//        long result = scheduledTaskService.purgeOldestFile(tempDirectory, 2);
//
//        assertThat(result).isEqualTo(2);
//
//        Files.walk(tempDirectoryPath.toPath())
//                .sorted(Comparator.reverseOrder())
//                .map(Path::toFile)
//                .forEach(File::delete);
//    }
//
//    @Tag("Deleting the oldest file should be successful")
//    @Test
//    public void whenNoBackupFileExistsShouldReturnGoodNbrFile() throws IOException, InterruptedException {
//        Path path = Files.createTempDirectory(BACKUP_TEMP_DIRECTORY);
//        File tempDirectoryPath = path.toFile();
//        String tempDirectory = path.toFile().getAbsolutePath();
//
//        long result = scheduledTaskService.purgeOldestFile(tempDirectory, 2);
//
//        assertThat(result).isEqualTo(0);
//
//        Files.walk(tempDirectoryPath.toPath())
//                .sorted(Comparator.reverseOrder())
//                .map(Path::toFile)
//                .forEach(File::delete);
//    }
//
//    @Tag("Deleting the oldest file should be successful")
//    @Test
//    public void whenBackupDatabaseShouldReturnValidateMessage() throws IOException, InterruptedException {
//        Path path = Path.of("backup", "test_directory");
//        BufferedReader mockBufferedReader = new BufferedReader(new StringReader("CREATE TABLE public.connection_user"));
//
//        when(mockCommandService.executeBackupCmd(anyString(), anyString(), anyString())).thenReturn(0);
//        when(mockFileReaderService.createFileBufferedReader(anyString())).thenReturn(mockBufferedReader);
//        when(mockFile.length()).thenReturn(2L);
//
//        int result = scheduledTaskService.backupDataBase(path.toString(), "mock_path");
//
//        assertThat(result).isEqualTo(0);
//    }
}
