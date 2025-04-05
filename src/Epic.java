import java.util.ArrayList;
import java.util.List;

public class Epic extends Tasks {
    private List<Integer> subTaskIds;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subTaskIds = new ArrayList<>();
    }

    public void addSubTask(int subTaskId) {
        subTaskIds.add(subTaskId);
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }
}