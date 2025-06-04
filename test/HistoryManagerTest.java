import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.List;

class HistoryManagerTest {
    private final HistoryManager history = Managers.getDefaultHistory();
    private final TaskManager manager = Managers.getDefault();

    @Test
    void shouldKeepLastVersionInHistory() {
        Task original = manager.createTask(new Task("Task", "Desc", Status.NEW));
        history.add(original);

        original.setStatus(Status.DONE);
        history.add(original);

        List<Task> historyList = history.getHistory();

        assertEquals(1, historyList.size()); // Теперь ожидаем 1 запись
        assertEquals(Status.DONE, historyList.get(0).getStatus());

    }
}