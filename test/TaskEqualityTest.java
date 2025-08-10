// TaskEqualityTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskEqualityTest {
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW);
        Task task2 = new Task(1, "Task 2", "Different", Status.DONE);
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void subtasksWithSameIdShouldBeEqual() {
        SubTask sub1 = new SubTask(1, "Sub 1", "Desc", Status.NEW, 10);
        SubTask sub2 = new SubTask(1, "Sub 2", "Diff", Status.DONE, 20);
        assertEquals(sub1, sub2, "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    void epicShouldNotEqualTaskWithSameId() {
        Task task = new Task(1, "Task", "Desc", Status.NEW);
        Epic epic = new Epic(1, "Epic", "Desc", Status.NEW);
        assertNotEquals(task, epic, "Эпик и задача не должны быть равны даже при одинаковом id");
    }
}