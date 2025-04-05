public class SubTask extends Tasks {
    private int epicId;

    public SubTask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}