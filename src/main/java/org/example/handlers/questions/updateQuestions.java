package org.example.handlers.questions;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.Response;
import org.example.controller.dynamic_controller;
import org.example.handlers.authentication.loginteacher;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class updateQuestions implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String token = extractToken(exchange);

        // Validate the token
        if (token == null || !loginteacher.validateToken(token)) {
            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
            return;
        }

        String idValue = exchange.getQueryParameters().get("questionsId").getFirst();
        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
            try {
                JSONObject json = new JSONObject(message);
                Map<String, String> fieldValues = jsonToMap(json);
                Response response = dynamic_controller.update("Questions", "questions_id", Integer.parseInt(idValue), fieldValues);
                sendResponse(exchange, response.getStatusCode(), response.getData().toString()); // Assuming response.getData() returns a String or can be converted to String
            } catch (NumberFormatException e) {
                sendResponse(exchange, StatusCodes.BAD_REQUEST, "{\"error\":\"Invalid Question ID format\"}");
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
    private String extractToken(HttpServerExchange exchange) {
        // token is sent as a Bearer token in the Authorization in postman
        String authorizationHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract token part
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
