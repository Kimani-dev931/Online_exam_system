package org.example.handlers.options;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.controllers.Options;
import org.example.Response;
import org.example.handlers.authentication.loginstudent;
import org.example.handlers.authentication.loginteacher;

import java.util.*;

public class findAllOptions implements HttpHandler {
    //    @Override
//    public void handleRequest(HttpServerExchange exchange) {
//        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
//            try {
//                // Call the method to insert the student into the database
//                Response response = Options.selectOptions(connection,"Options",null,null,null,null,null,null,null,null,null,null);
//                // Send the response back to the client
//                sendResponse(exchange1, response.getStatusCode(), response.getData().toString());
//            } catch (Exception e) {
//                // Catch any parsing or database exceptions and return an appropriate error message
//                sendResponse(exchange1, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
//            }
//        });
//    }
    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String token = extractToken(exchange);

        // Validate the token
        if (token == null || !loginteacher.validateToken(token)) {
            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
            return;
        }

        if (token == null || !loginstudent.validateToken(token)) {
            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
            return;
        }

        try {
            // Attempt to parse 'page' and 'pageSize' from query parameters
            int page = Integer.parseInt(exchange.getQueryParameters().getOrDefault("page", new ArrayDeque<>(Arrays.asList("1"))).getFirst());
            int pageSize = Integer.parseInt(exchange.getQueryParameters().getOrDefault("pageSize", new ArrayDeque<>(Arrays.asList("5"))).getFirst());

            // Ensure that the parsed values are positive
            if (page < 1 || pageSize < 1) {
                throw new IllegalArgumentException("Page and pageSize must be positive integers.");
            }
            int offset = (page - 1) * pageSize;
            // Mapping from query parameter names to database column names
            Map<String, String> paramToColumnMap = Map.of(
                    "optionLabelStartsWith", "option_label",
                    "correctAnswerStartsWith", "correct_answer",
                    "dateCreatedStartsWith", "date_created"
                    // Corrected key format for consistency
            );

            Map<String, String> likeConditions = new HashMap<>();
            StringBuilder userInputs = new StringBuilder(); // To accumulate user inputs for error messaging

            // Automatically construct likeConditions based on the mapping
            exchange.getQueryParameters().forEach((paramName, value) -> {
                String dbColumnName = paramToColumnMap.get(paramName);
                if (dbColumnName != null) { // Only add if the parameter maps to a known column
                    likeConditions.put(dbColumnName, value.getFirst() + "%");
                    userInputs.append(paramName).append(": ").append(value.getFirst()).append(", ");
                }
            });

            // Remove the last comma and space
            if (userInputs.length() > 0) {
                userInputs.setLength(userInputs.length() - 2); // Adjust for last ", "
            }

            List<String> columns = Arrays.asList("option_id", "question_id", "correct_answer", "date_created");

            String orderBy = "option_id";
            // Existing logic to construct likeConditions and fetch data...

            exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
                try {
                    // Fetch data and handle empty result set
                    Response response = Options.selectOptions( "Options", columns, null, null, orderBy, null, pageSize, offset, null, null, likeConditions);
                    if ("[]".equals(response.getData().toString().trim())) {
                        sendResponse(exchange1, StatusCodes.NOT_FOUND, "{\"error\":\"Query-parameter values not found or no matching data: " + userInputs.toString() + "\"}");
                    } else {
                        sendResponse(exchange1, response.getStatusCode(), response.getData().toString());
                    }
                } catch (Exception e) {
                    // Handle other exceptions
                    sendResponse(exchange1, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
                }
            });
        } catch (NumberFormatException e) {
            // Catch and handle the case where page or pageSize are not integers
            sendResponse(exchange, StatusCodes.BAD_REQUEST, "{\"error\":\"Page and pageSize must be integers.\"}");
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
