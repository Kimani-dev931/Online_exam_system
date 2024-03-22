package org.example.handlers.responses;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.Response;
import org.example.controller.Dynamic_Controller;
import org.example.handlers.authentication.LoginStudent;
import org.example.handlers.authentication.LoginTeacher;

public class FindResponsesById implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {


        String token = extractToken(exchange);

        // Validate the token
        if (token == null || !LoginTeacher.validateToken(token)) {
            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
            return;
        }

        if (token == null || !LoginStudent.validateToken(token)) {
            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
            return;
        }

        String idValue = exchange.getQueryParameters().get("responseId").getFirst();
        String whereClause = "response_id = " + idValue;
        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
            try {
                Response response = Dynamic_Controller.select( "Responses", null, whereClause, null, null, null, null, null, null, null,null);

                // Assuming response.getData() returns a JSON string representation of an array
                String responseData = response.getData().toString();
                // Check if the response data is an empty array
                if ("[]".equals(responseData.trim())) {
                    sendResponse(exchange1, StatusCodes.NOT_FOUND, "{\"error\":\"responseId " +idValue+ "  not found\"}");
                } else {
                    sendResponse(exchange1, response.getStatusCode(), responseData);
                }
            } catch (Exception e) {
                sendResponse(exchange1, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
            }
        });
    }

    private void sendResponse(HttpServerExchange exchange, int statusCode, String jsonData) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(jsonData);
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
