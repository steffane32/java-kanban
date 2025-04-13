import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ValidationTest {
    private TaskManager manager = Managers.getDefault();

    @Test
    void testSubtaskEpicReference() {
        Epic epic = manager.createEpic(new Epic("Epic", "Desc", Status.NEW));
        SubTask sub = manager.createSubtask(new SubTask("Sub", "Desc", Status.NEW, epic.getId()));
        assertTrue(epic.getId() == sub.getEpicId());
    }
}