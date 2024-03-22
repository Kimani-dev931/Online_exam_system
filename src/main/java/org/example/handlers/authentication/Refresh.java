package org.example.handlers.authentication;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.example.Response;
import org.example.controller.Dynamic_Controller;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



public class Refresh implements HttpHandler {

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {

        String token = extractToken(exchange);

        // Validate the old token
        if (token == null || !validateToken(token)) {
            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
            return;
        }
        int userId = fetchUserIdFromToken(token);


        String newToken = generateToken(userId);

        updateUserAuthToken(userId, newToken);

        JSONObject responseJson = new JSONObject();
        responseJson.put("Refreshed Token Is", newToken);
        sendResponse(exchange, 200, responseJson.toString());
    }

    private int fetchUserIdFromToken(String token) {
        List<String> columns = List.of("user_id");
        String whereClause = "token = '" + token + "'";
        Response selectResponse = Dynamic_Controller.select("userauth", columns, whereClause, null, null, null, null, null, null, "MySQL", null);
        try {
            JSONObject selectData = new JSONObject(selectResponse.getData().toString());
            if (selectData.length() > 0) {
                return selectData.getInt("user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching user ID from token: " + e.getMessage());
        }
        return -1;
    }
    private void updateUserAuthToken(int userId, String newToken) {

        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put("token", newToken);
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(5);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        fieldValues.put("expiry_date", dtf.format(expiryDate));
        Response updateResponse = Dynamic_Controller.update("userauth", "user_id", userId, fieldValues);
        if (updateResponse.getStatusCode() != 200) {
            System.err.println("Failed to update auth details: " + updateResponse.getData().toString());
        }
    }
//    public static boolean validateToken(String token) {
//        try {
//            System.out.println(token);
//            List<String> columns = Arrays.asList("user_id","expiry_date");
//            String whereClause = "token = '" + token + "'";
//            Response selectResponse = dynamic_controller.select("userauth", columns, whereClause, null, null, null, null, null, null, "MySQL", null);
//            System.out.println(selectResponse.getData().toString());
//            JSONObject responseData = new JSONObject(selectResponse.getData().toString());
//            if (responseData.length() > 0) {
//
//                LocalDateTime expiryDate = LocalDateTime.parse(responseData.getString("expiry_date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//
//                if (expiryDate.isAfter(LocalDateTime.now())) {
//
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//        return false;
//    }

    public static boolean validateToken(String token) {
        try {
            System.out.println(token);
            List<String> columns = Arrays.asList("user_id", "expiry_date");
            String whereClause = "token = '" + token + "'";
            Response selectResponse = Dynamic_Controller.select("userauth", columns, whereClause, null, null, null, null, null, null, "MySQL", null);
            System.out.println(selectResponse.getData().toString());

            // Ensure the response is not null and not empty
            if (selectResponse.getData() == null || selectResponse.getData().toString().trim().isEmpty()) {
                System.out.println("Response data is null or empty.");
                return false;
            }

            JSONObject responseData = new JSONObject(selectResponse.getData().toString());
            if (responseData.length() > 0) {
                LocalDateTime expiryDate = LocalDateTime.parse(responseData.getString("expiry_date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                if (expiryDate.isAfter(LocalDateTime.now())) {
                    return true;
                }
            }
        } catch (JSONException e) {
            System.out.println("JSON parsing error: " + e.getMessage());
            // Handle JSON specific parsing errors here
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other exceptions
        }
        return false;
    }

    private String generateToken(int userId) {
        long timestamp = Instant.now().getEpochSecond();

        long expiryTime = timestamp + (5 * 60);

        String tokenRaw = userId + ":" + expiryTime + ":" + Math.random();
        return Base64.getEncoder().encodeToString(tokenRaw.getBytes());
    }

    private void sendResponse(HttpServerExchange exchange, int statusCode, String jsonData) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(jsonData);
    }

    private String extractToken(HttpServerExchange exchange) {
        String authorizationHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
