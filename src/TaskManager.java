import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private List<Tasks> tasks;
    private Map<Integer, List<SubTask>> epicSubtasks;
    private int nextId = 1;

    public TaskManager() {
        tasks = new ArrayList<>();
        epicSubtasks = new HashMap<>();
    }

    public List<Tasks> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public void deleteAllTasks() {
        tasks.clear();
        epicSubtasks.clear();
    }

    public Tasks getTaskById(int id) {
        for (Tasks task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public void createTask(Tasks task) {
        tasks.add(task);
        if (task.getClass() == SubTask.class) {
            SubTask subtask = (SubTask) task;
            int epicId = subtask.getEpicId();
            if (!epicSubtasks.containsKey(epicId)) {
                epicSubtasks.put(epicId, new ArrayList<>());
            }

            epicSubtasks.get(epicId).add(subtask);
        }
    }

    public void updateTask(Tasks updatedTask) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == updatedTask.getId()) {
                tasks.set(i, updatedTask);
                return;
            }
        }
    }

    public void deleteTaskById(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == id) {
                tasks.remove(i);
                return;
            }
        }
    }
    public void updateEpicStatus(int epicId) {
        List<SubTask> subtasks = epicSubtasks.get(epicId);
        if (subtasks == null || subtasks.isEmpty()) {
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (SubTask subtask : subtasks) {
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        Status newStatus;
        if (allNew) {
            newStatus = Status.NEW;
        } else if (allDone) {
            newStatus = Status.DONE;
        } else {
            newStatus = Status.IN_PROGRESS;
        }


        for (Tasks task : tasks) {
            if (task.getId() == epicId) {
                task.setStatus(newStatus);
                break;
            }
        }
    }
    public List<Tasks> getSubtasksForEpic(int epicId) {
        return new ArrayList<>(epicSubtasks.getOrDefault(epicId, new ArrayList<>()));


    }
}