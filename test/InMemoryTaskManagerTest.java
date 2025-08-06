// InMemoryTaskManagerTest.java
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager(Managers.getDefaultHistory());
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
    @Test
    void shouldClearAllTasks() {
        manager.createTask(new Task(0, "Task", "Desc", Status.NEW));
        Epic epic = manager.createEpic(new Epic(0,"Epic", "Desc", Status.NEW));
        manager.createSubtask(new SubTask(0,"Sub", "Desc", Status.NEW, epic.getId()));

        manager.clearAll();

        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }
}