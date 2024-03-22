package org.example.handlers.exam;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.Response;
import org.example.controller.Dynamic_Controller;
import org.example.Filter;
import org.example.handlers.authentication.LoginTeacher;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FindAllExams implements HttpHandler {

//    @Override
//    public void handleRequest(HttpServerExchange exchange) {
//        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
//            try {
//                // Call the method to insert the student into the database
//                Response response = Exam.selectExam(connection,"Exam",null,null,null,null,null,null,null,null,null,null);
//                // Send the response back to the client
//                sendResponse(exchange1, response.getStatusCode(), response.getData().toString());
//            } catch (Exception e) {
//                // Catch any parsing or database exceptions and return an appropriate error message
//                sendResponse(exchange1, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
//            }
//        });
//    }

    @Override
//    public void handleRequest(HttpServerExchange exchange) {
//        String token = extractToken(exchange);
//
//        // Validate the token
//        if (token == null || !loginteacher.validateToken(token)) {
//            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
//            return;
//        }
//        if (token == null || !loginstudent.validateToken(token)) {
//            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
//            return;
//        }
//        try {
//
//            // Attempt to parse 'page' and 'pageSize' from query parameters
//            int page = Integer.parseInt(exchange.getQueryParameters().getOrDefault("page", new ArrayDeque<>(Arrays.asList("1"))).getFirst());
//            int pageSize = Integer.parseInt(exchange.getQueryParameters().getOrDefault("pageSize", new ArrayDeque<>(Arrays.asList("5"))).getFirst());
//
//            // Ensure that the parsed values are positive
//            if (page < 1 || pageSize < 1) {
//                throw new IllegalArgumentException("Page and pageSize must be positive integers.");
//            }
//            int offset = (page - 1) * pageSize;
//            // Mapping from query parameter names to database column names
//            Map<String, String> paramToColumnMap = Map.of(
//                    "startingTimeStartsWith", "starting_time",
//                    "timeTakenStartsWith", "time_taken",
//                    "examDateStartsWith", "exam_date",
//                    "examNameStartsWith", "exam_name",
//                    "userNameStartsWith", "user_name",
//                    "dateCreatedStartsWith", "date_created" // Corrected key format for consistency
//            );
//
//            Map<String, String> likeConditions = new HashMap<>();
//            StringBuilder userInputs = new StringBuilder(); // To accumulate user inputs for error messaging
//
//            // Automatically construct likeConditions based on the mapping
//            exchange.getQueryParameters().forEach((paramName, value) -> {
//                String dbColumnName = paramToColumnMap.get(paramName);
//                if (dbColumnName != null) { // Only add if the parameter maps to a known column
//                    likeConditions.put(dbColumnName, value.getFirst() + "%");
//                    userInputs.append(paramName).append(": ").append(value.getFirst()).append(", ");
//                }
//            });
//
//            // Remove the last comma and space
//            if (userInputs.length() > 0) {
//                userInputs.setLength(userInputs.length() - 2); // Adjust for last ", "
//            }
//
//            List<String> columns = Arrays.asList("exam_id","class_id","class_id", "subject_id", "teacher_id", "starting_time", "time_taken", "exam_date","date_created","exam_name");
//
//
//            // Existing logic to construct likeConditions and fetch data...
//
//            exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
//                try {
//                    // Fetch data and handle empty result set
//                    Response response = dynamic_controller.select("Exam", columns, null, null, null, null, pageSize, offset, null, null, likeConditions);
//                    if ("[]".equals(response.getData().toString().trim())) {
//                        sendResponse(exchange1, StatusCodes.NOT_FOUND, "{\"error\":\"Query-parameter values not found or no matching data: " + userInputs.toString() + "\"}");
//                    } else {
//                        sendResponse(exchange1, response.getStatusCode(), response.getData().toString());
//                    }
//                } catch (Exception e) {
//                    // Handle other exceptions
//                    sendResponse(exchange1, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
//                }
//            });
//        } catch (NumberFormatException e) {
//            // Catch and handle the case where page or pageSize are not integers
//            sendResponse(exchange, StatusCodes.BAD_REQUEST, "{\"error\":\"Page and pageSize must be integers.\"}");
//        }
//    }
    public void handleRequest(HttpServerExchange exchange) {
        String token = extractToken(exchange);

        // Validate the token
        if (token == null || !LoginTeacher.validateToken(token)) {
            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
            return;
        }

        try {
            int page = Integer.parseInt(exchange.getQueryParameters().getOrDefault("page", new ArrayDeque<>(Arrays.asList("1"))).getFirst());
            int pageSize = Integer.parseInt(exchange.getQueryParameters().getOrDefault("pageSize", new ArrayDeque<>(Arrays.asList("5"))).getFirst());
            pageSize = Math.min(pageSize, 50);
            int finalPageSize = pageSize;
            if (page < 1 || pageSize < 1) {
                throw new IllegalArgumentException("Page and pageSize must be positive integers.");

            }
            if (pageSize > 50) {
                // Display JSON error message for exceeding the maximum page size
                sendResponse(exchange, StatusCodes.BAD_REQUEST, "{\"error\":\"Page size cannot exceed 50.\"}");
                return;
            }
            int offset = (page - 1) * pageSize;

            String filterString = exchange.getQueryParameters().getOrDefault("filter", new ArrayDeque<>()).peekFirst();
            if (filterString == null || filterString.isEmpty()) {
                filterString = "";
            } else {
                filterString = URLDecoder.decode(filterString, StandardCharsets.UTF_8);
            }

            // Generate the WHERE clause from the filter string
            System.out.println(filterString);
            String whereClause = Filter.generateWhereClause(filterString);
            System.out.println(whereClause );

            List<String> columns = Arrays.asList("exam_id","class_id","class_id", "subject_id", "teacher_id", "starting_time", "time_taken", "exam_date","date_created","exam_name");
            String orderBy = "exam_id";



            exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
                try {

                    Response response = Dynamic_Controller.select("Exam", columns, whereClause, null, orderBy, null, finalPageSize, offset, null, null, null);
                    List<String> column=Arrays.asList("count(*)");

                    Response countResponse = Dynamic_Controller.select("Exam",column , whereClause, null, null, null,null, null, null, null, null);
                    // Extract the count value from the response
                    JsonNode countNode = new ObjectMapper().readTree(countResponse.getData().toString());
                    int totalRecords = countNode.get("count(*)").asInt();
                    if ("[]".equals(response.getData().toString().trim())) {
                        sendResponse(exchange1, StatusCodes.NOT_FOUND, "{\"error\":\"No matching data.\"}");
                    } else {


                        Map<String, Object> apiCup = new HashMap<>();
                        apiCup.put("responseCode", response.getStatusCode());
                        apiCup.put("page", page);
                        apiCup.put("pages", (totalRecords + finalPageSize - 1) / finalPageSize);
                        apiCup.put("pageSize", finalPageSize);
                        apiCup.put("totalRecords", totalRecords);
                        apiCup.put("data",new ObjectMapper().readTree(response.getData().toString()));

                        ObjectMapper objectMapper = new ObjectMapper();
                        ObjectNode jsonNode = objectMapper.createObjectNode();
                        jsonNode.put("totalRecords", apiCup.get("totalRecords").toString());
                        jsonNode.put("pages", apiCup.get("pages").toString());
                        jsonNode.put("pageSize", apiCup.get("pageSize").toString());
                        jsonNode.put("page", apiCup.get("page").toString());
                        jsonNode.put("responseCode", apiCup.get("responseCode").toString());
                        jsonNode.set("data", (JsonNode) apiCup.get("data"));

                        String apiCupJson = jsonNode.toString();

                        sendResponse(exchange1, response.getStatusCode(),apiCupJson );
                    }
                } catch (Exception e) {
                    sendResponse(exchange1, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
                }
            });
        } catch (NumberFormatException e) {
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
