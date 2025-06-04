import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class EpicTest {
    private TaskManager manager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        epic = manager.createEpic(new Epic("Отдохнуть", "Подготовиться к отпуску", Status.NEW));
    }

    @Test
    @DisplayName("Эпик без подзадач должен иметь статус NEW")
    void emptyEpicShouldHaveNewStatus() {
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    @DisplayName("Статус эпика должен обновляться при изменении статуса подзадачи")
    void shouldUpdateEpicStatusWhenSubtaskChanges() {
        SubTask sub1 = manager.createSubtask(new SubTask("Подзадача 1", "", Status.NEW, epic.getId()));
        SubTask sub2 = manager.createSubtask(new SubTask("Подзадача 2", "", Status.NEW, epic.getId()));

        // Все NEW -> эпик NEW
        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());

        // Одна DONE -> эпик IN_PROGRESS
        sub1.setStatus(Status.DONE);
        manager.updateSubtask(sub1);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
        // Все DONE -> эпи
    }
}


