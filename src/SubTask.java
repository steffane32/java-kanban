import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, String description, Status status, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, int epicId) {
        this(0, name, description, status, epicId);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() + "m" : "null") +
                ", startTime=" + (getStartTime() != null ? getStartTime() : "null") +
                ", endTime=" + getEndTime() +
                '}';
    }
}