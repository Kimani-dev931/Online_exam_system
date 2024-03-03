package org.example.handlers.teacher;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.Response;
import org.example.controllers.Student;
import org.example.controllers.Teacher;

import java.util.*;

import static org.example.DatabaseConnectionApp.connection;

public class findAllTeachers implements HttpHandler {

    //    @Override
//    public void handleRequest(HttpServerExchange exchange) {
//        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
//            try {
//                // Call the method to insert the student into the database
//                Response response = Teacher.selectTeacher(connection,"Teacher",null,null,null,null,null,null,null,null,null,null);
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
                    "firstNameStartsWith", "first_name",
                    "lastNameStartsWith", "last_name",
                    "tscNumberStartsWith", "tsc_number",
                    "idNumberStartsWith", "id_number",
                    "userNameStartsWith", "username",
                    "phoneNumberStartsWith", "phone_number",
                    "educationLevelStartsWith", "education_level",
                    "emailStartsWith", "email",
                    "dateCreatedStartsWith", "date_created" // Corrected key format for consistency
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

            List<String> columns = Arrays.asList("teacher_id", "first_name", "last_name", "tsc_number", "id_number", "username", "phone_number", "education_level", "email", "date_created");

            String orderBy = "teacher_id";
            // Existing logic to construct likeConditions and fetch data...

            exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
                try {
                    // Fetch data and handle empty result set
                    Response response = Student.selectStudent(connection, "Teacher", columns, null, null, orderBy, null, pageSize, offset, null, null, likeConditions);
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
}