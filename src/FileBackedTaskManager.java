import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) {
        if (!file.exists()) {
            throw new RuntimeException("Файл не существует: " + file.getPath());
        }

        try {
            FileBackedTaskManager manager = new FileBackedTaskManager(file, historyManager);
            manager.load();
            return manager;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке из файла", e);
        }
    }

    private void load() throws IOException {
        String content = Files.readString(file.toPath());
        String[] lines = content.split("\n");

        if (lines.length < 2) {
            return;
        }

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }

            try {
                Task task = fromString(line);
                if (task != null) {
                    if (task instanceof Epic) {
                        epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof SubTask) {
                        SubTask subtask = (SubTask) task;
                        subtasks.put(subtask.getId(), subtask);
                        Epic epic = epics.get(subtask.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(subtask.getId());
                        }
                    } else {
                        tasks.put(task.getId(), task);
                    }

                    if (task.getId() >= nextId) {
                        nextId = task.getId() + 1;
                    }

                    if (task.getStartTime() != null) {
                        prioritizedTasks.add(task);
                    }
                }
            } catch (Exception e) {
                System.err.println("Ошибка при загрузке задачи: " + e.getMessage());
            }
        }

        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
            updateEpicTimings(epic.getId());
        }
    }

    protected void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic,duration,startTime\n");

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
            throw new RuntimeException("Ошибка при сохранении в файл", e);
        }
    }

    private static String toString(Task task) {
        if (task instanceof SubTask) {
            SubTask subtask = (SubTask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d,%d,%s",
                    subtask.getId(),
                    subtask.getName(),
                    subtask.getStatus(),
                    subtask.getDescription(),
                    subtask.getEpicId(),
                    subtask.getDuration() != null ? subtask.getDuration().toMinutes() : 0,
                    subtask.getStartTime() != null ? subtask.getStartTime().format(DATE_TIME_FORMATTER) : "");
        } else if (task instanceof Epic) {
            return String.format("%d,EPIC,%s,%s,%s,,%d,%s",
                    task.getId(),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    task.getDuration() != null ? task.getDuration().toMinutes() : 0,
                    task.getStartTime() != null ? task.getStartTime().format(DATE_TIME_FORMATTER) : "");
        } else {
            return String.format("%d,TASK,%s,%s,%s,,%d,%s",
                    task.getId(),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    task.getDuration() != null ? task.getDuration().toMinutes() : 0,
                    task.getStartTime() != null ? task.getStartTime().format(DATE_TIME_FORMATTER) : "");
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",", -1);
        if (parts.length < 5) {
            throw new RuntimeException("Некорректные данные у задачи");
        }

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Duration duration = parts.length > 6 && !parts[6].isEmpty()
                ? Duration.ofMinutes(Long.parseLong(parts[6]))
                : null;

        LocalDateTime startTime = parts.length > 7 && !parts[7].isEmpty()
                ? LocalDateTime.parse(parts[7], DATE_TIME_FORMATTER)
                : null;

        switch (type) {
            case "TASK":
                return new Task(id, name, description, status, duration, startTime);
            case "EPIC":
                Epic epic = new Epic(id, name, description, status);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                return epic;
            case "SUBTASK":
                if (parts.length < 6 || parts[5].isEmpty()) {
                    throw new RuntimeException("Не указан epicId для подзадачи");
                }
                int epicId = Integer.parseInt(parts[5]);
                return new SubTask(id, name, description, status, epicId, duration, startTime);
            default:
                throw new RuntimeException("Неверный тип задачи: " + type);
        }
    }

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

    @Override
    public void clearAll() {
        super.clearAll();
        save();
    }
}