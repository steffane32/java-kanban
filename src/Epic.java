import java.util.ArrayList;
import java.util.List;

public class Epic extends Tasks {
    private List<SubTask> subTasks;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subTasks = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
        subTask.setEpicId(this.getId());
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }
}