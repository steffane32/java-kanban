import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.matches("/subtasks/\\d+")) {
                int id = Integer.parseInt(path.substring(10));

                switch (method) {
                    case "GET":
                        handleGetSubtaskById(exchange, id);
                        break;
                    case "DELETE":
                        handleDeleteSubtask(exchange, id);
                        break;
                    default:
                        sendNotFound(exchange, "Метод не поддерживается: " + method);
                }
            } else if (path.equals("/subtasks")) {
                switch (method) {
                    case "GET":
                        handleGetAllSubtasks(exchange);
                        break;
                    case "POST":
                        handleCreateSubtask(exchange);
                        break;
                    default:
                        sendNotFound(exchange, "Метод не поддерживается: " + method);
                }
            } else {
                sendNotFound(exchange, "Путь не найден: " + path);
            }
        } catch (Exception e) {
            sendInternalError(exchange, "Ошибка обработки запроса: " + e.getMessage());
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        List<SubTask> subtasks = taskManager.getAllSubtasks();
        String jsonResponse = gson.toJson(subtasks);
        sendText(exchange, jsonResponse);
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            SubTask newSubtask = gson.fromJson(body, SubTask.class);
            SubTask createdSubtask = taskManager.createSubtask(newSubtask);
            String responseJson = gson.toJson(createdSubtask);

            byte[] resp = responseJson.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            sendInternalError(exchange, "Ошибка создания подзадачи: " + e.getMessage());
        }
        exchange.close();
    }

    private void handleGetSubtaskById(HttpExchange exchange, int id) throws IOException {
        SubTask subtask = taskManager.getSubtaskById(id);
        if (subtask == null) {
            sendNotFound(exchange, "Подзадача с id=" + id + " не найдена");
            return;
        }

        String jsonResponse = gson.toJson(subtask);
        sendText(exchange, jsonResponse);
    }

    private void handleDeleteSubtask(HttpExchange exchange, int id) throws IOException {
        SubTask subtask = taskManager.getSubtaskById(id);
        if (subtask == null) {
            sendNotFound(exchange, "Подзадача с id=" + id + " не найдена");
            return;
        }

        taskManager.deleteSubtaskById(id);
        String responseJson = gson.toJson(new MessageResponse("Подзадача с id=" + id + " удалена"));
        sendText(exchange, responseJson);
    }

    private static class MessageResponse {
        private final String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}