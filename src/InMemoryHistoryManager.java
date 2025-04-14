import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) return;

        //history.remove(task); // Удаляем старое вхождение (если есть)
        history.addFirst(task);

        //if (history.size() > MAX_HISTORY_SIZE) {
         //   history.removeLast();
        //}
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history); // Возвращаем копию
    }

//    @Override
//    public void remove(int id) {
//        history.removeIf(task -> task.getId() == id);
//    }
}