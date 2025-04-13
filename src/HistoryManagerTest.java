import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private final HistoryManager history = Managers.getDefaultHistory();
    private final TaskManager manager = Managers.getDefault();

    @Test
    void shouldPreserveTaskDataInHistory() {
        // Создаем задачу и добавляем в историю
        Task original = manager.createTask(new Task("Task", "Desc", Status.NEW));
        history.add(original);

        // Изменяем задачу и снова добавляем
        original.setStatus(Status.DONE);
        history.add(original);

        // Получаем историю
        List<Task> historyList = history.getHistory();

        // Проверяем
        assertEquals(2, historyList.size(), "В истории должно быть 2 записи");
        assertEquals(Status.NEW, historyList.get(0).getStatus(),
                "Первая запись должна сохранять исходный статус");
        assertEquals(Status.DONE, historyList.get(1).getStatus(),
                "Вторая запись должна содержать обновленный статус");
    }
}