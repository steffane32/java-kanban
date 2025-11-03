import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.matches("/epics/\\d+")) {
                int id = Integer.parseInt(path.substring(7)); // "/epics/".length() = 6

                switch (method) {
                    case "GET":
                        handleGetEpicById(exchange, id);
                        break;
                    case "DELETE":
                        handleDeleteEpic(exchange, id);
                        break;
                    default:
                        sendNotFound(exchange, "Метод не поддерживается: " + method);
                }
            } else if (path.equals("/epics")) {
                switch (method) {
                    case "GET":
                        handleGetAllEpics(exchange);
                        break;
                    case "POST":
                        handleCreateEpic(exchange);
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

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();

        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < epics.size(); i++) {
            Epic epic = epics.get(i);
            jsonBuilder.append(String.format(
                    "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\"}",
                    epic.getId(),
                    epic.getName().replace("\"", "\\\""),
                    epic.getDescription().replace("\"", "\\\""),
                    epic.getStatus()
            ));
            if (i < epics.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");

        sendText(exchange, jsonBuilder.toString());
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            Status status = Status.valueOf(jsonObject.get("status").getAsString());

            Epic newEpic = new Epic(0, name, description, status);
            Epic createdEpic = taskManager.createEpic(newEpic);

            String responseJson = String.format(
                    "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\"}",
                    createdEpic.getId(),
                    createdEpic.getName().replace("\"", "\\\""),
                    createdEpic.getDescription().replace("\"", "\\\""),
                    createdEpic.getStatus()
            );

            byte[] resp = responseJson.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            sendInternalError(exchange, "Ошибка создания эпика: " + e.getMessage());
        }
        exchange.close();
    }

    private void handleGetEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic == null) {
            sendNotFound(exchange, "Эпик с id=" + id + " не найдена");
            return;
        }

        String jsonResponse = String.format(
                "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"status\":\"%s\"}",
                epic.getId(),
                epic.getName().replace("\"", "\\\""),
                epic.getDescription().replace("\"", "\\\""),
                epic.getStatus()
        );
        sendText(exchange, jsonResponse);
    }

    private void handleDeleteEpic(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic == null) {
            sendNotFound(exchange, "Эпик с id=" + id + " не найдена");
            return;
        }

        taskManager.deleteEpicById(id);
        sendText(exchange, "{\"message\":\"Эпик с id=" + id + " удалена\"}");
    }
}