// ValidationTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {
    private TaskManager manager = Managers.getDefault();

    @Test
    void testSubtaskEpicReference() {
        Epic epic = manager.createEpic(new Epic(0,"Epic", "Desc", Status.NEW));
        SubTask sub = manager.createSubtask(new SubTask(0,"Sub", "Desc", Status.NEW, epic.getId()));

        assertEquals(epic.getId(), sub.getEpicId());
        assertTrue(manager.getSubtasksForEpic(epic.getId()).contains(sub));
    }
}