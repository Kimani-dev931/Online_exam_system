package org.example.handlers.classes;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.controllers.Class;
import org.example.Response;
import org.example.handlers.authentication.loginteacher;

import java.util.*;

public class findAllClasses implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String token = extractToken(exchange);

        // Validate the token
        if (token == null || !loginteacher.validateToken(token)) {
            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
            return;
        }

        try {
            // Attempt to parse 'page' and 'pageSize' from query parameters
            int page = Integer.parseInt(exchange.getQueryParameters().getOrDefault("page", new ArrayDeque<>(Arrays.asList("1"))).getFirst());
            int pageSize = Integer.parseInt(exchange.getQueryParameters().getOrDefault("pageSize", new ArrayDeque<>(Arrays.asList("5"))).getFirst());

            // Ensure that the parsed values are positive
            if (page < 1 || pageSize < 1) {
                throw new NumberFormatException("Page and pageSize must be positive integers.");
            }

            int offset = (page - 1) * pageSize; // Calculate the 'offset' for SQL query pagination

            Map<String, String> paramToColumnMap = Map.of(
                    "classNameStartsWith", "class_name",
                    "dateCreatedStartsWith", "date_created"

            );

            Map<String, String> likeConditions = new HashMap<>();
            StringBuilder userInputs = new StringBuilder();


            exchange.getQueryParameters().forEach((paramName, value) -> {
                String dbColumnName = paramToColumnMap.get(paramName);
                if (dbColumnName != null) {
                    likeConditions.put(dbColumnName, value.getFirst() + "%");
                    userInputs.append(paramName).append(": ").append(value.getFirst()).append(", ");
                }
            });


            if (userInputs.length() > 0) {
                userInputs.setLength(userInputs.length() - 2); // Adjust for last ", "
            }

            List<String> columns = Arrays.asList("class_id", "class_name", "date_created");
            String orderBy = "class_id";

            // Proceed with fetching data and sending response
            exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
                try {
                    Response response = Class.selectclass( "Class", columns, null, null, orderBy, null, pageSize, offset, null, null, likeConditions);
                    if ("[]".equals(response.getData().toString().trim())) {
                        sendResponse(exchange1, StatusCodes.NOT_FOUND, "{\"error\":\"Query-parameter values not found or no matching data: \"}");
                    } else {
                        sendResponse(exchange1, response.getStatusCode(), response.getData().toString());
                    }
                } catch (Exception e) {
                    sendResponse(exchange1, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
                }
            });

        } catch (NumberFormatException e) {
            // Handle the case where 'page' or 'pageSize' cannot be parsed as integers
            sendResponse(exchange, StatusCodes.BAD_REQUEST, "{\"error\":\"Invalid page or pageSize parameter. Both should be positive integers.\"}");
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
