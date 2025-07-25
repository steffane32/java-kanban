import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class HistoryManagerTest {
    private final HistoryManager history = Managers.getDefaultHistory();

    @Test
    void shouldPreserveTaskDataInHistory() {
        // Создаем задачу
        Task original = new Task(1, "Task", "Description", Status.NEW);

        // Добавляем в историю первый раз (NEW)
        history.add(original);

        // Изменяем задачу и добавляем снова (DONE)
        original.setStatus(Status.DONE);
        history.add(original);

        List<Task> historyList = history.getHistory();

        // Проверяем, что сохранились оба состояния
        assertEquals(2, historyList.size(), "В истории должно быть 2 записи");
        assertEquals(Status.NEW, historyList.get(0).getStatus(),
                "Первая запись должна сохранять исходный статус NEW");
        assertEquals(Status.DONE, historyList.get(1).getStatus(),
                "Вторая запись должна содержать статус DONE");
    }

    @Test
    void shouldKeepHistoryOrder() {
        Task task1 = new Task(1, "Task1", "Desc1", Status.NEW);
        Task task2 = new Task(2, "Task2", "Desc2", Status.IN_PROGRESS);

        history.add(task1);
        history.add(task2);

        List<Task> historyList = history.getHistory();

        assertEquals(2, historyList.size());
        assertEquals(task1.getId(), historyList.get(0).getId());
        assertEquals(task2.getId(), historyList.get(1).getId());
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task = new Task(1, "Task", "Desc", Status.NEW);
        history.add(task);

        history.remove(task.getId());

        assertTrue(history.getHistory().isEmpty(),
                "История должна быть пустой после удаления");
    }

    @Test
    void shouldLimitHistorySize() {
        // Заполняем историю сверх лимита
        for (int i = 1; i <= 15; i++) {
            history.add(new Task(i, "Task" + i, "Desc", Status.NEW));
        }

        assertEquals(10, history.getHistory().size(),
                "История должна ограничиваться 10 записями");
    }

    @Test
    void shouldNotAffectOriginalTask() {
        Task original = new Task(1, "Original", "Desc", Status.NEW);
        history.add(original);

        // Меняем оригинальную задачу
        original.setStatus(Status.DONE);

        Task fromHistory = history.getHistory().get(0);
        assertEquals(Status.NEW, fromHistory.getStatus(),
                "История должна хранить неизменяемые копии");
    }
}