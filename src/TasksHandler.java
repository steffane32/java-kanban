import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            // Проверяем, есть ли ID в пути (формат: /tasks/123)
            if (path.matches("/tasks/\\d+")) {
                // Извлекаем ID из пути
                int id = Integer.parseInt(path.substring(7)); // "/tasks/".length() = 7

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
                // Обработка пути /tasks (без ID)
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
        // Получаем все задачи из менеджера
        List<Task> tasks = taskManager.getAllTasks();

        // Создаем упрощённый JSON массив вручную
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            jsonBuilder.append(String.format(
                    "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\"}",
                    task.getId(),
                    task.getName().replace("\"", "\\\""),
                    task.getDescription().replace("\"", "\\\""),
                    task.getStatus()
            ));
            if (i < tasks.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");

        String jsonResponse = jsonBuilder.toString();
        sendText(exchange, jsonResponse);
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        // Читаем тело запроса
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        // Используем JsonObject чтобы вытащить только нужные поля
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        Status status = Status.valueOf(jsonObject.get("status").getAsString());

        // Создаем задачу через конструктор (без времени)
        Task newTask = new Task(name, description, status);

        // Создаем задачу через менеджер
        Task createdTask = taskManager.createTask(newTask);

        // Создаем упрощённый JSON ответ вручную (без полей времени)
        String responseJson = String.format(
                "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\"}",
                createdTask.getId(),
                createdTask.getName().replace("\"", "\\\""),
                createdTask.getDescription().replace("\"", "\\\""),
                createdTask.getStatus()
        );

        // Отправляем ответ с кодом 201
        byte[] resp = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(201, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    private void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        Task task = taskManager.getTaskById(id);
        if (task == null) {
            sendNotFound(exchange, "Задача с id=" + id + " не найдена");
            return;
        }

        String jsonResponse = String.format(
                "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\"}",
                task.getId(),
                task.getName().replace("\"", "\\\""),
                task.getDescription().replace("\"", "\\\""),
                task.getStatus()
        );
        sendText(exchange, jsonResponse);
    }

    private void handleDeleteTask(HttpExchange exchange, int id) throws IOException {
        Task task = taskManager.getTaskById(id);
        if (task == null) {
            sendNotFound(exchange, "Задача с id=" + id + " не найдена");
            return;
        }

        taskManager.deleteTaskById(id);
        sendText(exchange, "{\"message\":\"Задача с id=" + id + " удалена\"}");
    }
}