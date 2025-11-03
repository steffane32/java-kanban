import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            // Проверяем, есть ли ID в пути (формат: /subtasks/123)
            if (path.matches("/subtasks/\\d+")) {
                // Извлекаем ID из пути
                int id = Integer.parseInt(path.substring(10)); // "/subtasks/".length() = 10

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
                // Обработка пути /subtasks (без ID)
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
        // Получаем все подзадачи из менеджера
        List<SubTask> subtasks = taskManager.getAllSubtasks();

        // Создаем упрощённый JSON массив вручную
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < subtasks.size(); i++) {
            SubTask subtask = subtasks.get(i);
            jsonBuilder.append(String.format(
                    "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\",\"epicId\":%d}",
                    subtask.getId(),
                    subtask.getName().replace("\"", "\\\""),
                    subtask.getDescription().replace("\"", "\\\""),
                    subtask.getStatus(),
                    subtask.getEpicId()
            ));
            if (i < subtasks.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");

        String jsonResponse = jsonBuilder.toString();
        sendText(exchange, jsonResponse);
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {
        try {
            // Читаем тело запроса
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            // Используем JsonObject чтобы вытащить только нужные поля
            JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            Status status = Status.valueOf(jsonObject.get("status").getAsString());
            int epicId = jsonObject.get("epicId").getAsInt();

            // Создаем подзадачу через конструктор (без времени)
            SubTask newSubtask = new SubTask(name, description, status, epicId);

            // Создаем подзадачу через менеджер
            SubTask createdSubtask = taskManager.createSubtask(newSubtask);

            // Создаем упрощённый JSON ответ вручную
            String responseJson = String.format(
                    "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\",\"epicId\":%d}",
                    createdSubtask.getId(),
                    createdSubtask.getName().replace("\"", "\\\""),
                    createdSubtask.getDescription().replace("\"", "\\\""),
                    createdSubtask.getStatus(),
                    createdSubtask.getEpicId()
            );

            // Отправляем ответ с кодом 201
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

        String jsonResponse = String.format(
                "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\",\"epicId\":%d}",
                subtask.getId(),
                subtask.getName().replace("\"", "\\\""),
                subtask.getDescription().replace("\"", "\\\""),
                subtask.getStatus(),
                subtask.getEpicId()
        );
        sendText(exchange, jsonResponse);
    }

    private void handleDeleteSubtask(HttpExchange exchange, int id) throws IOException {
        SubTask subtask = taskManager.getSubtaskById(id);
        if (subtask == null) {
            sendNotFound(exchange, "Подзадача с id=" + id + " не найдена");
            return;
        }

        taskManager.deleteSubtaskById(id);
        sendText(exchange, "{\"message\":\"Подзадача с id=" + id + " удалена\"}");
    }
}