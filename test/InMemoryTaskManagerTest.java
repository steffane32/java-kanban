// InMemoryTaskManagerTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    void shouldClearAllTasks() {
        manager.createTask(new Task("Task", "Desc", Status.NEW));
        manager.createEpic(new Epic("Epic", "Desc", Status.NEW));

        manager.clearAll();

        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
    }
}