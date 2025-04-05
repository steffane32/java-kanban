import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Tasks> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, List<Integer>> epicSubtasks;
    private int nextId = 1;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        epicSubtasks = new HashMap<>();
    }

    public List<Tasks> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
        epicSubtasks.clear();
    }

    public Tasks getTaskById(int id) {
        return tasks.get(id);
    }

    public void createTask(Tasks task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        if (SubTask.getEpicId() > 0) {
            int epicId = SubTask.getEpicId();
            if (!epicSubtasks.containsKey(epicId)) {
                epicSubtasks.put(epicId, new ArrayList<>());
            }
            epicSubtasks.get(epicId).add(task.getId());
        }
    }

    public void updateTask(Tasks updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
        for (List<Integer> subTaskIds : epicSubtasks.values()) {
            subTaskIds.remove(Integer.valueOf(id));
        }
    }

    public void updateEpicStatus(int epicId) {
        List<Integer> subtasks = epicSubtasks.get(epicId);
        if (subtasks == null || subtasks.isEmpty()) {
            if (epics.containsKey(epicId)) {
                epics.get(epicId).setStatus(Status.NEW);
            }
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer subtaskId : subtasks) {
            SubTask subtask = (SubTask) tasks.get(subtaskId);
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

        if (epics.containsKey(epicId)) {
            epics.get(epicId).setStatus(newStatus);
        }
    }

    public List<Tasks> getSubtasksForEpic(int epicId) {
        List<Tasks> subtasksList = new ArrayList<>();
        List<Integer> subtaskIds = epicSubtasks.getOrDefault(epicId, new ArrayList<>());
        for (Integer id : subtaskIds) {
            subtasksList.add(tasks.get(id));
        }
        return subtasksList;
    }
}