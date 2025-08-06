// FileBackedTaskManagerTest.java
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
            tempFile.deleteOnExit();
            return FileBackedTaskManager.loadFromFile(tempFile, Managers.getDefaultHistory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldSaveAndLoadFromFile() {
        Task task = manager.createTask(new Task("Task", "Desc", Status.NEW));
        Epic epic = manager.createEpic(new Epic("Epic", "Desc", Status.NEW));
        manager.createSubtask(new SubTask("Sub", "Desc", Status.NEW, epic.getId()));

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile, Managers.getDefaultHistory());

        assertEquals(1, loaded.getAllTasks().size());
        assertEquals(1, loaded.getAllEpics().size());
        assertEquals(1, loaded.getAllSubtasks().size());
    }

    @Test
    void shouldThrowWhenFileInvalid() {
        File invalid = new File("nonexistent/path");
        assertThrows(ManagerSaveException.class,
                () -> FileBackedTaskManager.loadFromFile(invalid, Managers.getDefaultHistory()));
    }
}