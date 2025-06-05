import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(String name, String description, Status status) {
        this(0, name, description, status);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }
}
