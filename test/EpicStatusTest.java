// EpicStatusTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicStatusTest {
    private TaskManager manager = Managers.getDefault();

    @Test
    void shouldBeNewWhenNoSubtasks() {
        Epic epic = manager.createEpic(new Epic("Epic", "Desc", Status.NEW));
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldBeNewWhenAllSubtasksNew() {
        Epic epic = manager.createEpic(new Epic("Epic", "Desc", Status.NEW));
        manager.createSubtask(new SubTask("Sub1", "Desc", Status.NEW, epic.getId()));
        manager.createSubtask(new SubTask("Sub2", "Desc", Status.NEW, epic.getId()));

        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldBeDoneWhenAllSubtasksDone() {
        Epic epic = manager.createEpic(new Epic("Epic", "Desc", Status.NEW));
        SubTask sub1 = manager.createSubtask(new SubTask("Sub1", "Desc", Status.NEW, epic.getId()));
        SubTask sub2 = manager.createSubtask(new SubTask("Sub2", "Desc", Status.NEW, epic.getId()));

        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);

        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldBeInProgressWhenMixedStatuses() {
        Epic epic = manager.createEpic(new Epic("Epic", "Desc", Status.NEW));
        SubTask sub1 = manager.createSubtask(new SubTask("Sub1", "Desc", Status.NEW, epic.getId()));
        SubTask sub2 = manager.createSubtask(new SubTask("Sub2", "Desc", Status.DONE, epic.getId()));

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldBeInProgressWhenAnyInProgress() {
        Epic epic = manager.createEpic(new Epic("Epic", "Desc", Status.NEW));
        SubTask sub = manager.createSubtask(new SubTask("Sub", "Desc", Status.IN_PROGRESS, epic.getId()));

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }
}