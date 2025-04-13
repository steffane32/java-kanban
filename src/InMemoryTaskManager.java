import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subtasks = new HashMap<>();
    private int nextId = 1;
    private final LinkedList<Task> history = new LinkedList<>(); // Для хранения истории
    private static final int MAX_HISTORY_SIZE = 10; // Лимит ист
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }
        // Методы для Task
        @Override
        public List<Task> getAllTasks () {
            return new ArrayList<>(tasks.values());
        }

        @Override
        public void deleteAllTasks () {
            tasks.clear();
        }

        @Override
        public Task createTask (Task task){
            task.setId(nextId++);
            tasks.put(task.getId(), task);
            return task;
        }

        @Override
        public void updateTask (Task task){
            if (tasks.containsKey(task.getId())) {
                tasks.put(task.getId(), task);
            }
        }

        @Override
        public void deleteTaskById ( int id){
            tasks.remove(id);
        }

        // Методы для Epic
        public List<Epic> getAllEpics () {
            return new ArrayList<>(epics.values());
        }

        public void deleteAllEpics () {
            epics.clear();
            subtasks.clear(); // Удаляем все подзадачи, так как они принадлежат эпикам
        }

        public Epic createEpic (Epic epic){
            epic.setId(nextId++);
            epics.put(epic.getId(), epic);
            return epic;
        }

        public void updateEpic (Epic epic){
            if (epics.containsKey(epic.getId())) {
                Epic savedEpic = epics.get(epic.getId());
                savedEpic.setName(epic.getName());
                savedEpic.setDescription(epic.getDescription());
            }
        }

        public void deleteEpicById ( int id){
            Epic epic = epics.remove(id);
            if (epic != null) {
                for (Integer subtaskId : epic.getSubtaskIds()) {
                    subtasks.remove(subtaskId);
                }
            }
        }

        // Методы для SubTask
        public List<SubTask> getAllSubtasks () {
            return new ArrayList<>(subtasks.values());
        }

        public void deleteAllSubtasks () {
            subtasks.clear();
            for (Epic epic : epics.values()) {
                epic.getSubtaskIds().clear();
                updateEpicStatus(epic.getId());
            }
        }

        public SubTask createSubtask (SubTask subtask){
            int epicId = subtask.getEpicId();
            if (!epics.containsKey(epicId)) {
                return null;
            }
            subtask.setId(nextId++);
            subtasks.put(subtask.getId(), subtask);
            epics.get(epicId).addSubtaskId(subtask.getId());
            updateEpicStatus(epicId);
            return subtask;
        }

        public void updateSubtask (SubTask subtask){
            int subtaskId = subtask.getId();
            if (subtasks.containsKey(subtaskId)) {
                subtasks.put(subtaskId, subtask);
                updateEpicStatus(subtask.getEpicId());
            }
        }

        public void deleteSubtaskById ( int id){
            SubTask subtask = subtasks.remove(id);
            if (subtask != null) {
                int epicId = subtask.getEpicId();
                epics.get(epicId).removeSubtaskId(id);
                updateEpicStatus(epicId);
            }
        }


        @Override
        public List<SubTask> getSubtasksForEpic ( int epicId){
            if (!epics.containsKey(epicId)) {
                return Collections.emptyList();
            }
            List<SubTask> result = new ArrayList<>();
            for (Integer subtaskId : epics.get(epicId).getSubtaskIds()) {
                result.add(subtasks.get(subtaskId));
            }
            return result;
        }

        public void clearAll () {
            tasks.clear();
            epics.clear();
            subtasks.clear();
            nextId = 1;
        }

        @Override
        public void updateEpicStatus ( int epicId){
            Epic epic = epics.get(epicId);
            if (epic == null) return;

            List<Integer> subtaskIds = epic.getSubtaskIds();
            if (subtaskIds.isEmpty()) {
                epic.setStatus(Status.NEW);
                return;
            }

            boolean allNew = true;
            boolean allDone = true;

            for (Integer subtaskId : subtaskIds) {
                SubTask subtask = subtasks.get(subtaskId);
                if (subtask == null) continue;

                if (subtask.getStatus() != Status.NEW) allNew = false;
                if (subtask.getStatus() != Status.DONE) allDone = false;
            }

            if (allNew) epic.setStatus(Status.NEW);
            else if (allDone) epic.setStatus(Status.DONE);
            else epic.setStatus(Status.IN_PROGRESS);
        }
        @Override
        public List<Task> getHistory () {
            return new ArrayList<>(history); // Возвращаем копию
        }

        private void addToHistory (Task task){
            history.addFirst(task); // Добавляем в начало
            if (history.size() > MAX_HISTORY_SIZE) {
                history.removeLast(); // Удаляем самый старый элемент
            }
        }

        // Модифицируем методы получения задач
        // @Override
        public Task getTaskById ( int id){
            Task task = tasks.get(id);
            if (task != null) historyManager.add(task);
            return task;
        }

        @Override
        public Epic getEpicById ( int id){
            Epic epic = epics.get(id);
            if (epic != null) historyManager.add(epic);
            return epic;
        }

        @Override
        public SubTask getSubtaskById ( int id){
            SubTask subtask = subtasks.get(id);
            if (subtask != null) historyManager.add(subtask);
            return subtask;
        }
    }
