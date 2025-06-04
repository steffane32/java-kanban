import java.util.List;

public interface HistoryManager {

    void add(Task task);
    // Добавляет задачу в историю

    List<Task> getHistory();
    // Возвращает историю просмотров

    void remove(int id);
    // Удаляет задачу из истории (пригодится позже)
}