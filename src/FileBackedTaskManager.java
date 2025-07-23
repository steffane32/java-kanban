import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file, historyManager);
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            if (lines.length < 2) return manager; // Пустой файл или только заголовок

            for (int i = 1; i < lines.length; i++) {
                Task task = fromString(lines[i]);
                if (task != null) {
                    if (task instanceof Epic) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof SubTask) {
                        manager.subtasks.put(task.getId(), (SubTask) task);
                        Epic epic = manager.epics.get(((SubTask) task).getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(task.getId());
                        }
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }
                    if (task.getId() >= manager.nextId) {
                        manager.nextId = task.getId() + 1;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }
        return manager;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (SubTask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    private static String toString(Task task) {
        if (task instanceof SubTask) {
            SubTask subtask = (SubTask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d",
                    subtask.getId(), subtask.getName(), subtask.getStatus(),
                    subtask.getDescription(), subtask.getEpicId());
        } else if (task instanceof Epic) {
            return String.format("%d,EPIC,%s,%s,%s,",
                    task.getId(), task.getName(), task.getStatus(), task.getDescription());
        } else {
            return String.format("%d,TASK,%s,%s,%s,",
                    task.getId(), task.getName(), task.getStatus(), task.getDescription());
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 5) return null;

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case "TASK":
                return new Task(id, name, description, status);
            case "EPIC":
                return new Epic(id, name, description, status);
            case "SUBTASK":
                int epicId = parts.length > 5 ? Integer.parseInt(parts[5]) : 0;
                return new SubTask(id, name, description, status, epicId);
            default:
                return null;
        }
    }

    // Переопределения методов с вызовом save()
    @Override
    public Task createTask(Task task) {
        Task created = super.createTask(task);
        save();
        return created;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic created = super.createEpic(epic);
        save();
        return created;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public SubTask createSubtask(SubTask subtask) {
        SubTask created = super.createSubtask(subtask);
        save();
        return created;
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}
