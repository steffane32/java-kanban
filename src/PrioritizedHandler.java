import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

            // Создаем JSON массив вручную
            StringBuilder jsonBuilder = new StringBuilder("[");
            for (int i = 0; i < prioritizedTasks.size(); i++) {
                Task task = prioritizedTasks.get(i);
                if (task instanceof SubTask) {
                    SubTask subtask = (SubTask) task;
                    jsonBuilder.append(String.format(
                            "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\",\"epicId\":%d}",
                            subtask.getId(),
                            subtask.getName().replace("\"", "\\\""),
                            subtask.getDescription().replace("\"", "\\\""),
                            subtask.getStatus(),
                            subtask.getEpicId()
                    ));
                } else {
                    jsonBuilder.append(String.format(
                            "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\"}",
                            task.getId(),
                            task.getName().replace("\"", "\\\""),
                            task.getDescription().replace("\"", "\\\""),
                            task.getStatus()
                    ));
                }
                if (i < prioritizedTasks.size() - 1) {
                    jsonBuilder.append(",");
                }
            }
            jsonBuilder.append("]");

            sendText(exchange, jsonBuilder.toString());
        } else {
            sendNotFound(exchange, "Метод не поддерживается: " + exchange.getRequestMethod());
        }
    }
}