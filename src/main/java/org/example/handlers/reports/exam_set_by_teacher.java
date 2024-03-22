package org.example.handlers.reports;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.controller.Reports;
import org.example.Response;
import org.example.handlers.authentication.loginteacher;

import static org.example.MainApp.connection;


public class exam_set_by_teacher implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String token = extractToken(exchange);

        // Validate the token
        if (token == null || !loginteacher.validateToken(token)) {
            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
            return;
        }

        String teacherId = exchange.getQueryParameters().get("teacherId").getFirst();
        try {
            Response response = Reports.exams_set_by_a_Teacher(connection, Integer.parseInt(teacherId));
            sendResponse(exchange, response.getStatusCode(), response.getData().toString()); // Assuming response.getData() returns a String or can be converted to String
        }
        catch (NumberFormatException e) {
            sendResponse(exchange, StatusCodes.BAD_REQUEST, "{\"error\":\"Invalid teacher ID format\"}");
        }
        catch (Exception e) {
            sendResponse(exchange, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
        }
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
