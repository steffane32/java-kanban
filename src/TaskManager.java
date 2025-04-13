import java.util.List;

public interface TaskManager {
    // Методы для Task
    List<Task> getAllTasks();
    void deleteAllTasks();
    Task getTaskById(int id);
    Task createTask(Task task);
    void updateTask(Task task);
    void deleteTaskById(int id);

    // Методы для Epic
    List<Epic> getAllEpics();
    void deleteAllEpics();
    Epic getEpicById(int id);
    Epic createEpic(Epic epic);
    void updateEpic(Epic epic);
    void deleteEpicById(int id);

    // Методы для SubTask
    List<SubTask> getAllSubtasks();
    void deleteAllSubtasks();
    SubTask getSubtaskById(int id);
    SubTask createSubtask(SubTask subtask);
    void updateSubtask(SubTask subtask);
    void deleteSubtaskById(int id);

    // Общие методы
    List<SubTask> getSubtasksForEpic(int epicId);
    void updateEpicStatus(int epicId); // Реализация будет в классе

    // Метод просмотра истории
    List<Task> getHistory();


}