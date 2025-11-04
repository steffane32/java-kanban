import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.matches("/tasks/\\d+")) {
                int id = Integer.parseInt(path.substring(7));

                switch (method) {
                    case "GET":
                        handleGetTaskById(exchange, id);
                        break;
                    case "DELETE":
                        handleDeleteTask(exchange, id);
                        break;
                    default:
                        sendNotFound(exchange, "Метод не поддерживается: " + method);
                }
            } else if (path.equals("/tasks")) {
                switch (method) {
                    case "GET":
                        handleGetAllTasks(exchange);
                        break;
                    case "POST":
                        handleCreateTask(exchange);
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

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        String jsonResponse = gson.toJson(tasks);
        sendText(exchange, jsonResponse);
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task newTask = gson.fromJson(body, Task.class);
            Task createdTask = taskManager.createTask(newTask);
            String responseJson = gson.toJson(createdTask);

            byte[] resp = responseJson.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            sendInternalError(exchange, "Ошибка создания задачи: " + e.getMessage());
        }
        exchange.close();
    }

    private void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        Task task = taskManager.getTaskById(id);
        if (task == null) {
            sendNotFound(exchange, "Задача с id=" + id + " не найдена");
            return;
        }

        String jsonResponse = gson.toJson(task);
        sendText(exchange, jsonResponse);
    }

    private void handleDeleteTask(HttpExchange exchange, int id) throws IOException {
        Task task = taskManager.getTaskById(id);
        if (task == null) {
            sendNotFound(exchange, "Задача с id=" + id + " не найдена");
            return;
        }

        taskManager.deleteTaskById(id);
        String responseJson = gson.toJson(new MessageResponse("Задача с id=" + id + " удалена"));
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