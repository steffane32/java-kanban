import java.io.File;

public class Managers {
    private Managers() {
        // Приватный конструктор
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file, getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}