package org.example.handlers.classes;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.controllers.Class;
import org.example.Response;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class updateClass implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String idValue = exchange.getQueryParameters().get("classId").getFirst();
        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
            try {
                JSONObject json = new JSONObject(message);
                Map<String, String> fieldValues = jsonToMap(json);
                Response response = Class.updateClass("Class", "class_id", Integer.parseInt(idValue), fieldValues);
                sendResponse(exchange, response.getStatusCode(), response.getData().toString()); // Assuming response.getData() returns a String or can be converted to String
            } catch (NumberFormatException e) {
                sendResponse(exchange, StatusCodes.BAD_REQUEST, "{\"error\":\"Invalid Class ID format\"}");
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
