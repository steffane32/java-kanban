import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    @Test
    void shouldCreateAndGetTask() {
        Task task = new Task(0, "Task", "Description", Status.NEW);
        Task created = manager.createTask(task);

        assertNotNull(created.getId(), "Задача должна получить id при создании");
        assertEquals(task.getName(), manager.getTaskById(created.getId()).getName());
    }

    @Test
    void shouldUpdateTask() {
        Task task = manager.createTask(new Task(0, "Task", "Desc", Status.NEW));
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);

        assertEquals(Status.IN_PROGRESS, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    void shouldDeleteTask() {
        Task task = manager.createTask(new Task(0, "Task", "Desc", Status.NEW));
        manager.deleteTaskById(task.getId());

        assertNull(manager.getTaskById(task.getId()));
    }

    @Test
    void shouldCreateEpicWithSubtasks() {
        Epic epic = manager.createEpic(new Epic(0, "Epic", "Desc", Status.NEW));
        SubTask sub = manager.createSubtask(new SubTask(0, "Sub", "Desc", Status.NEW, epic.getId()));

        assertEquals(epic.getId(), sub.getEpicId());
        assertTrue(manager.getSubtasksForEpic(epic.getId()).contains(sub));
    }

    @Test
    void shouldUpdateEpicStatus() {
        Epic epic = manager.createEpic(new Epic(0, "Epic", "Desc", Status.NEW));
        SubTask sub1 = manager.createSubtask(new SubTask(0, "Sub1", "Desc", Status.NEW, epic.getId()));
        SubTask sub2 = manager.createSubtask(new SubTask(0, "Sub2", "Desc", Status.NEW, epic.getId()));

        // NEW -> IN_PROGRESS
        sub1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(sub1);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());

        // IN_PROGRESS -> DONE
        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        Task task1 = new Task(0, "Task1", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        Task task2 = new Task(0, "Task2", "Desc", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());

        manager.createTask(task2);
        manager.createTask(task1);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(task2.getId(), prioritized.get(0).getId());
        assertEquals(task1.getId(), prioritized.get(1).getId());
    }

    @Test
    void shouldDetectTimeOverlaps() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task(0, "Task1", "Desc", Status.NEW,
                Duration.ofHours(1), now);
        Task task2 = new Task(0, "Task2", "Desc", Status.NEW,
                Duration.ofHours(2), now.plusMinutes(30));

        manager.createTask(task1);
        assertThrows(IllegalArgumentException.class, () -> manager.createTask(task2));
    }

    @Test
    void shouldHandleEpicTimings() {
        Epic epic = manager.createEpic(new Epic(0, "Epic", "Desc", Status.NEW));
        LocalDateTime start = LocalDateTime.now();

        SubTask sub1 = manager.createSubtask(new SubTask(0, "Sub1", "Desc", Status.NEW,
                epic.getId(), Duration.ofHours(1), start));
        SubTask sub2 = manager.createSubtask(new SubTask(0, "Sub2", "Desc", Status.NEW,
                epic.getId(), Duration.ofHours(2), start.plusHours(1)));

        assertEquals(start, epic.getStartTime());
        assertEquals(sub2.getEndTime(), epic.getEndTime());
        assertEquals(Duration.ofHours(3), epic.getDuration());
    }
}