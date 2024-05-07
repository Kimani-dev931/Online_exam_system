package org.example.handlers.authentication;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.Headers;
import org.example.Response;
import org.example.controller.Dynamic_Controller;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class LoginStudent implements HttpHandler {
    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.startBlocking();
        final FormDataParser parser = FormParserFactory.builder().build().createParser(exchange);
        if (parser != null) {
            try {
                FormData formData = parser.parseBlocking();
                String username = formData.getFirst("user_name").getValue();
                String password = formData.getFirst("password").getValue();

                Response response = authenticateStudent(username, password);
                sendResponse(exchange, response.getStatusCode(), response.getData().toString());
            } catch (Exception e) {
                sendResponse(exchange, 500, "{\"error\":\"Server error\"}");
            }
        } else {
            sendResponse(exchange, 400, "{\"error\":\"Bad request\"}");
        }
    }

    private Response authenticateStudent(String username, String password) {
        try {
            String whereClause = "user_name = '" + username + "'";
            Response selectResponse = Dynamic_Controller.select("Student", java.util.Arrays.asList("student_id", "password"), whereClause, null, null, null, null, null, null, null, null);

            JSONObject responseData = new JSONObject(selectResponse.getData().toString());
            if (responseData.length() == 0) {
                return new Response(404, new JSONObject().put("error", "User not found"));
            }

            int studentId = responseData.getInt("student_id");
            Response lockStatusResponse = checkLockStatus(studentId);
            if (lockStatusResponse != null) {
                return lockStatusResponse;
            }
            String storedPassword = responseData.getString("password");

            if (password.equals(storedPassword)) {
                String token = generateToken(studentId);

                storeAuthDetails(studentId, token);

                JSONObject jsonData = new JSONObject();
                jsonData.put("token", token);
                jsonData.put("message", "Hello " + username + ", you just logged in successfully.");
                return new Response(200, jsonData);
            } else {

                incrementLoginAttempts(studentId);
                return new Response(403, new JSONObject().put("error", "Authentication failed, invalid password or username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(500, new JSONObject().put("error", "Server error"));
        }
    }

    public void incrementLoginAttempts(int studentId) {

        List<String> columns = List.of("login_attempts", "locked");
        String whereClause = "user_id = " + studentId + " AND user_type = 'student'";
        Response selectResponse = Dynamic_Controller.select("userauth", columns, whereClause, null, null, null, null, null, null, "MySQL", null);

        try {
            JSONObject selectData = new JSONObject(selectResponse.getData().toString());
            if (selectData.length() > 0) {
                int currentAttempts = selectData.getInt("login_attempts");
                boolean isLocked = selectData.getBoolean("locked");


                int newAttempts = currentAttempts + 1;

                boolean newIsLocked = newAttempts >= 6 || isLocked;

                Map<String, Object> updateFields = new HashMap<>();
                updateFields.put("login_attempts", Integer.toString(newAttempts));

                updateFields.put("locked", newIsLocked ? 1 : 0);


                Response updateResponse = Dynamic_Controller.object_update("userauth", "user_id", studentId, updateFields);

                if (updateResponse.getStatusCode() != 200) {
                    System.err.println("Failed to update login attempts: " + updateResponse.getData().toString());
                }
            } else {

                System.err.println("No userauth record found for teacherId: " + studentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error incrementing login attempts: " + e.getMessage());
        }
    }


    private Response checkLockStatus(int studentId) {
        List<String> columns = List.of("locked");
        String whereClause = "user_id = " + studentId + " AND user_type = 'student'";
        Response selectResponse = Dynamic_Controller.select("userauth", columns, whereClause, null, null, null, null, null, null, "MySQL", null);

        String responseDataStr = selectResponse.getData().toString().trim();


        if ("[]".equals(responseDataStr) || (!responseDataStr.startsWith("{") && !responseDataStr.startsWith("["))) {

            return null;
        }

        try {
            JSONObject selectData = new JSONObject(responseDataStr);

            boolean isLocked = selectData.optBoolean("locked", false); // default to false if not found

            if (isLocked) {

                return new Response(403, new JSONObject().put("error", "Your account has been locked after three unsuccessful login attempts. Please consult the admin."));
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();

            return new Response(500, new JSONObject().put("error", "Server error: " + e.getMessage()));
        }
    }

    public static Response resetAccountLock(int studentId) {

        Map<String, String> fieldValues = new HashMap<>();
        // Reset the lock status
        fieldValues.put("locked", "0");
        // Optionally reset the login attempts as well
        fieldValues.put("login_attempts", "0");


        Response updateResponse = Dynamic_Controller.update("userauth", "user_id", studentId, fieldValues);

        if (updateResponse.getStatusCode() == 200) {

            return new Response(200, new JSONObject().put("message", "Account unlocked successfully."));
        } else {

            return new Response(500, new JSONObject().put("error", "Failed to unlock account."));
        }
    }



    private void sendResponse(HttpServerExchange exchange, int statusCode, String jsonData) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(jsonData);
    }


    private String generateToken(int teacherId) {
        long timestamp = Instant.now().getEpochSecond();
        // 5 minutes from now
        long expiryTime = timestamp + (5 * 60);

        String tokenRaw = teacherId + ":" + expiryTime + ":" + Math.random();
        String token = Base64.getEncoder().encodeToString(tokenRaw.getBytes());

        System.out.println("Generated token: " + token);
        return token;
    }


    private void storeAuthDetails(int studentId, String token) {
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(5);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put("user_id", String.valueOf(studentId));
        fieldValues.put("user_type", "student");
        fieldValues.put("token", token);
        fieldValues.put("expiry_date", dtf.format(expiryDate));

        Response response;

        String whereClause = "user_id = " + studentId + " AND user_type = 'student'";
        Response selectResponse = Dynamic_Controller.select("userauth", java.util.Arrays.asList("*"), whereClause, null, null, null, null, null, null, "MySQL", null);

        if (selectResponse.getData().toString().equals("[]")) {
            response = Dynamic_Controller.add("userauth", fieldValues);
        } else {
            response = Dynamic_Controller.update("userauth", "user_id", studentId, fieldValues);
        }

        if (response.getStatusCode() != 200 && response.getStatusCode() != 201) {
            System.err.println("Failed to store auth details: " + response.getData().toString());
        }
    }



    public static boolean validateToken(String token) {
        try {
            System.out.println(token);

            List<String> columns = Arrays.asList("user_id","expiry_date");

            String whereClause = "token = '" + token + "'";


            Response selectResponse = Dynamic_Controller.select("userauth", columns, whereClause, null, null, null, null, null, null, "MySQL", null);
            System.out.println(selectResponse.getData().toString());

            JSONObject responseData = new JSONObject(selectResponse.getData().toString());
            if (responseData.length() > 0) {

                LocalDateTime expiryDate = LocalDateTime.parse(responseData.getString("expiry_date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                if (expiryDate.isAfter(LocalDateTime.now())) {

                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return false;
    }


}
