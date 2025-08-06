// TimeOverlapTest.java
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TimeOverlapTest {
    private TaskManager manager = Managers.getDefault();

    @Test
    void shouldDetectExactOverlap() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("Task1", "Desc", Status.NEW, Duration.ofHours(1), now);
        Task task2 = new Task("Task2", "Desc", Status.NEW, Duration.ofHours(1), now);

        manager.createTask(task1);
        assertThrows(ManagerSaveException.class, () -> manager.createTask(task2));
    }

    @Test
    void shouldDetectPartialOverlap() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("Task1", "Desc", Status.NEW, Duration.ofHours(2), now);
        Task task2 = new Task("Task2", "Desc", Status.NEW, Duration.ofHours(2), now.plusHours(1));

        manager.createTask(task1);
        assertThrows(ManagerSaveException.class, () -> manager.createTask(task2));
    }

    @Test
    void shouldAllowNonOverlappingTasks() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("Task1", "Desc", Status.NEW, Duration.ofHours(1), now);
        Task task2 = new Task("Task2", "Desc", Status.NEW, Duration.ofHours(1), now.plusHours(2));

        manager.createTask(task1);
        assertDoesNotThrow(() -> manager.createTask(task2));
    }

    @Test
    void shouldIgnoreTasksWithoutTime() {
        Task task1 = new Task("Task1", "Desc", Status.NEW);
        Task task2 = new Task("Task2", "Desc", Status.NEW);

        assertDoesNotThrow(() -> {
            manager.createTask(task1);
            manager.createTask(task2);
        });
    }
}