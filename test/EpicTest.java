import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EpicTest {
    private TaskManager manager;
    private Epic epic;
    private SubTask subtask;
    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        // Явно указываем статус NEW при создании эпика
        epic = manager.createEpic(new Epic("Отдохнуть", "Подготовиться к отпуску", Status.NEW));
        subtask = manager.createSubtask(
                new SubTask("Забронировать тур", "", Status.NEW, epic.getId())
        );
    }
    @Test
    void epicStatusShouldUpdateToDoneWhenSubtaskDone() {
        // Проверяем начальный статус
        Assertions.assertEquals(Status.NEW, epic.getStatus(),
                "Созданный эпик с подзадачей NEW должен иметь статус NEW");

        // Изменяем статус подзадачи
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);

        // Проверяем обновлённый статус эпика
        Assertions.assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен измениться на DONE, когда все подзадачи выполнены");
    }
}


