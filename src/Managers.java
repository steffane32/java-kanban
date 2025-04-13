public class Managers {
    private Managers() {
        // Приватный конструктор, чтобы нельзя было создать экземпляр класса
    }

    /**
     * Возвращает реализацию TaskManager по умолчанию (в памяти)
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    /**
     * Возвращает реализацию HistoryManager по умолчанию (пригодится позже)
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}