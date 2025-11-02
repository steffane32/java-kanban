// FileBackedTaskManagerTest.java
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
            tempFile.deleteOnExit();
            return new FileBackedTaskManager(tempFile, Managers.getDefaultHistory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldSaveAndLoadFromFile() {
        Task task = manager.createTask(new Task(0, "Task", "Desc", Status.NEW));
        Epic epic = manager.createEpic(new Epic(0, "Epic", "Desc", Status.NEW));
        SubTask sub = manager.createSubtask(new SubTask(0, "Sub", "Desc", Status.NEW, epic.getId()));

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile, Managers.getDefaultHistory());

        assertEquals(1, loaded.getAllTasks().size());
        assertEquals(1, loaded.getAllEpics().size());
        assertEquals(1, loaded.getAllSubtasks().size());
        assertEquals(task.getName(), loaded.getTaskById(task.getId()).getName());
        assertEquals(epic.getName(), loaded.getEpicById(epic.getId()).getName());
        assertEquals(sub.getName(), loaded.getSubtaskById(sub.getId()).getName());
    }

    @Test
    void shouldThrowWhenFileInvalid() {
        File invalid = new File("nonexistent/path/tasks.csv");
        assertThrows(IllegalArgumentException.class,
                () -> FileBackedTaskManager.loadFromFile(invalid, Managers.getDefaultHistory()),
                "Ожидалось IllegalArgumentException при попытке загрузить из несуществующего файла");
    }

    @Test
    void shouldDetectTimeOverlaps() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task(0, "Task1", "Desc", Status.NEW,
                Duration.ofHours(1), now);
        Task task2 = new Task(0, "Task2", "Desc", Status.NEW,
                Duration.ofHours(2), now.plusMinutes(30));

        manager.createTask(task1);
        assertThrows(IllegalStateException.class, () -> manager.createTask(task2));
    }
}