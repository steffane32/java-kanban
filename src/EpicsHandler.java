import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.matches("/epics/\\d+")) {
                int id = Integer.parseInt(path.substring(7));

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
        String jsonResponse = gson.toJson(epics);
        sendText(exchange, jsonResponse);
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Epic newEpic = gson.fromJson(body, Epic.class);
            Epic createdEpic = taskManager.createEpic(newEpic);
            String responseJson = gson.toJson(createdEpic);

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

        String jsonResponse = gson.toJson(epic);
        sendText(exchange, jsonResponse);
    }

    private void handleDeleteEpic(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic == null) {
            sendNotFound(exchange, "Эпик с id=" + id + " не найдена");
            return;
        }

        taskManager.deleteEpicById(id);
        String responseJson = gson.toJson(new MessageResponse("Эпик с id=" + id + " удалена"));
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