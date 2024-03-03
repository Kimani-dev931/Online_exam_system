package org.example.handlers.teacher;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.Response;
import org.example.controllers.Teacher;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static org.example.DatabaseConnectionApp.connection;

public class updateTeacher implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String idValue = exchange.getQueryParameters().get("teacherId").getFirst();
        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
            try {
                JSONObject json = new JSONObject(message);
                Map<String, String> fieldValues = jsonToMap(json);
                Response response = Teacher. updateTeacher("Teacher", "teacher_id", Integer.parseInt(idValue), fieldValues, connection);
                sendResponse(exchange, response.getStatusCode(), response.getData().toString()); // Assuming response.getData() returns a String or can be converted to String
            } catch (NumberFormatException e) {
                sendResponse(exchange, StatusCodes.BAD_REQUEST, "{\"error\":\"Invalid Teacher ID format\"}");
            } catch (Exception e) {
                sendResponse(exchange, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
            }
        });
    }
    private void sendResponse(HttpServerExchange exchange, int statusCode, String jsonData) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(jsonData);
    }
    private static Map<String, String> jsonToMap(JSONObject json) {
        Map<String, String> map = new HashMap<>();
        json.keys().forEachRemaining(key -> {
            map.put(key, json.getString(key));
        });
        return map;
    }

}
