import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager;
    protected final List<Task> prioritizedTasks = new ArrayList<>();

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(this::removeFromPrioritizedTasks);
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public Task createTask(Task task) {
        validateTaskTime(task);
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            removeFromPrioritizedTasks(oldTask);
            validateTaskTime(task);
            tasks.put(task.getId(), task);
            addToPrioritizedTasks(task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            removeFromPrioritizedTasks(task);
            historyManager.remove(id);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().forEach(subtaskId -> {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            historyManager.remove(epic.getId());
        });
        subtasks.values().forEach(this::removeFromPrioritizedTasks);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic savedEpic = epics.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
                removeFromPrioritizedTasks(subtasks.get(subtaskId));
            }
            historyManager.remove(id);
        }
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().forEach(this::removeFromPrioritizedTasks);
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
            updateEpicTimings(epic.getId());
        });
    }

    @Override
    public SubTask createSubtask(SubTask subtask) {
        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик с id=" + epicId + " не существует");
        }
        validateTaskTime(subtask);
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epics.get(epicId).addSubtaskId(subtask.getId());
        addToPrioritizedTasks(subtask);
        updateEpicStatus(epicId);
        updateEpicTimings(epicId);
        return subtask;
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        int subtaskId = subtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            SubTask oldSubtask = subtasks.get(subtaskId);
            removeFromPrioritizedTasks(oldSubtask);
            validateTaskTime(subtask);
            subtasks.put(subtaskId, subtask);
            addToPrioritizedTasks(subtask);
            updateEpicStatus(subtask.getEpicId());
            updateEpicTimings(subtask.getEpicId());
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        SubTask subtask = subtasks.remove(id);
        if (subtask != null) {
            removeFromPrioritizedTasks(subtask);
            int epicId = subtask.getEpicId();
            epics.get(epicId).removeSubtaskId(id);
            historyManager.remove(id);
            updateEpicStatus(epicId);
            updateEpicTimings(epicId);
        }
    }

    @Override
    public SubTask getSubtaskById(int id) {
        SubTask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public List<SubTask> getSubtasksForEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            return Collections.emptyList();
        }
        return epics.get(epicId).getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void clearAll() {
        deleteAllTasks();
        deleteAllEpics();
        deleteAllSubtasks();
        nextId = 1;
    }

    protected void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<SubTask> epicSubtasks = getSubtasksForEpic(epicId);
        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (SubTask subtask : epicSubtasks) {
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected void updateEpicTimings(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<SubTask> subtasksList = getSubtasksForEpic(epicId);
        epic.updateTimings(subtasksList);
    }

    protected void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
            prioritizedTasks.sort(Comparator.comparing(Task::getStartTime));
        }
    }

    protected void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    protected List<Task> getPrioritizedTasksList() {
        return Collections.unmodifiableList(prioritizedTasks);
    }

    private void validateTaskTime(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return;
        }

        for (Task existingTask : getPrioritizedTasksList()) {
            if (existingTask.getId() != newTask.getId() &&
                    isTimeOverlapping(newTask, existingTask)) {
                throw new IllegalStateException(
                        "Задача пересекается по времени с существующей задачей: " +
                                newTask + " пересекается с " + existingTask
                );
            }
        }
    }

    private boolean isTimeOverlapping(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}