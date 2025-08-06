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
        manager.createTask(new Task(0, "Task", "Desc", Status.NEW));
        Epic epic = manager.createEpic(new Epic(0,"Epic", "Desc", Status.NEW));
        manager.createSubtask(new SubTask(0,"Sub", "Desc", Status.NEW, epic.getId()));

        manager.clearAll();

        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }
}