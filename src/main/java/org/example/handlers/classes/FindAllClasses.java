package org.example.handlers.classes;

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

public class FindAllClasses implements HttpHandler {

    @Override
//    public void handleRequest(HttpServerExchange exchange) {
//        String token = extractToken(exchange);
//
//        // Validate the token
//        if (token == null || !loginteacher.validateToken(token)) {
//            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
//            return;
//        }
//
//        try {
//            // Attempt to parse 'page' and 'pageSize' from query parameters
//            int page = Integer.parseInt(exchange.getQueryParameters().getOrDefault("page", new ArrayDeque<>(Arrays.asList("1"))).getFirst());
//            int pageSize = Integer.parseInt(exchange.getQueryParameters().getOrDefault("pageSize", new ArrayDeque<>(Arrays.asList("5"))).getFirst());
//
//            // Ensure that the parsed values are positive
//            if (page < 1 || pageSize < 1) {
//                throw new NumberFormatException("Page and pageSize must be positive integers.");
//            }
//
//            int offset = (page - 1) * pageSize; // Calculate the 'offset' for SQL query pagination
//
//            Map<String, String> paramToColumnMap = Map.of(
//                    "classNameStartsWith", "class_name",
//                    "dateCreatedStartsWith", "date_created"
//
//            );
//
//            Map<String, String> likeConditions = new HashMap<>();
//            StringBuilder userInputs = new StringBuilder();
//
//
//            exchange.getQueryParameters().forEach((paramName, value) -> {
//                String dbColumnName = paramToColumnMap.get(paramName);
//                if (dbColumnName != null) {
//                    likeConditions.put(dbColumnName, value.getFirst() + "%");
//                    userInputs.append(paramName).append(": ").append(value.getFirst()).append(", ");
//                }
//            });
//
//
//            if (userInputs.length() > 0) {
//                userInputs.setLength(userInputs.length() - 2); // Adjust for last ", "
//            }
//
//            List<String> columns = Arrays.asList("class_id", "class_name", "date_created");
//            String orderBy = "class_id";
//
//            // Proceed with fetching data and sending response
//            exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
//                try {
//                    Response response = dynamic_controller.select( "Class", columns, null, null, orderBy, null, pageSize, offset, null, null, likeConditions);
//                    if ("[]".equals(response.getData().toString().trim())) {
//                        sendResponse(exchange1, StatusCodes.NOT_FOUND, "{\"error\":\"Query-parameter values not found or no matching data: \"}");
//                    } else {
//                        sendResponse(exchange1, response.getStatusCode(), response.getData().toString());
//                    }
//                } catch (Exception e) {
//                    sendResponse(exchange1, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
//                }
//            });
//
//        } catch (NumberFormatException e) {
//            // Handle the case where 'page' or 'pageSize' cannot be parsed as integers
//            sendResponse(exchange, StatusCodes.BAD_REQUEST, "{\"error\":\"Invalid page or pageSize parameter. Both should be positive integers.\"}");
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

            List<String> columns = Arrays.asList("class_id", "class_name", "date_created");
            String orderBy = "class_id";



            exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
                try {

                    Response response = Dynamic_Controller.select("Class", columns, whereClause, null, orderBy, null, finalPageSize, offset, null, null, null);
                    List<String> column=Arrays.asList("count(*)");

                    Response countResponse = Dynamic_Controller.select("Class",column , whereClause, null, null, null,null, null, null, null, null);
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

                        sendResponse(exchange1, response.getStatusCode(), apiCupJson);
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
