package org.example.handlers.authentication;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.example.Response;
import org.example.controller.Dynamic_Controller;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Logout implements HttpHandler {

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.startBlocking();
        String token = exchange.getQueryParameters().get("token").getFirst();

        int userId = validateToken(token);

        if (userId == -1) {
            sendResponse(exchange, 401, "{\"error\":\"Invalid or missing token\"}");
            return;
        }

        if (invalidateToken(userId)) {
            sendResponse(exchange, 200, "{\"message\":\"Logout successful\"}");
        } else {
            sendResponse(exchange, 500, "{\"error\":\"Failed to logout\"}");
        }
    }

    private int validateToken(String token) {
        try {
            List<String> columns = List.of("user_id", "expiry_date");
            String whereClause = "token = '" + token + "'";
            Response selectResponse = Dynamic_Controller.select("userauth", columns, whereClause, null, null, null, null, null, null, "MySQL", null);

            if (selectResponse.getData() == null || selectResponse.getData().toString().trim().isEmpty()) {
                return -1;
            }

            JSONObject responseData = new JSONObject(selectResponse.getData().toString());
            if (responseData.length() > 0) {
                LocalDateTime expiryDate = LocalDateTime.parse(responseData.getString("expiry_date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                if (expiryDate.isAfter(LocalDateTime.now())) {
                    return responseData.getInt("user_id");  // Return user ID if token is valid
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean invalidateToken(int userId) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put("expiry_date", dtf.format(now));

        Response updateResponse = Dynamic_Controller.update("userauth", "user_id", userId, fieldValues);
        return updateResponse.getStatusCode() == 200;
    }

    private void sendResponse(HttpServerExchange exchange, int statusCode, String jsonData) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(jsonData);
    }
}
