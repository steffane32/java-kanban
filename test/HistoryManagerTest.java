import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.List;

class HistoryManagerTest {
    private final HistoryManager history = Managers.getDefaultHistory();

    @Test
    void shouldPreserveTaskDataInHistory() {
        // Создаем задачу
        Task original = new Task(1, "Task", "Desc", Status.NEW);

        // Добавляем в историю первый раз
        history.add(original);

        // Изменяем задачу и добавляем снова
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