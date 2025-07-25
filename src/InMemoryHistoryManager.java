import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) return;

        // Удаляем старую версию задачи (если есть)
        remove(task.getId());

        // Создаем копию задачи для сохранения в истории
        Task taskCopy = copyTask(task);
        history.add(taskCopy);

        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }

    @Override
    public void remove(int id) {
        history.removeIf(task -> task.getId() == id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    private Task copyTask(Task original) {
        if (original instanceof Epic) {
            Epic epic = (Epic) original;
            return new Epic(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus());
        } else if (original instanceof SubTask) {
            SubTask subTask = (SubTask) original;
            return new SubTask(subTask.getId(), subTask.getName(),
                    subTask.getDescription(), subTask.getStatus(),
                    subTask.getEpicId());
        } else {
            return new Task(original.getId(), original.getName(),
                    original.getDescription(), original.getStatus());
        }
    }
}