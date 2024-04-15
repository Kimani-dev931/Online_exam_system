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

import java.util.List;
import java.util.Map;

public class ChangePassword  implements HttpHandler{
    @Override

    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.startBlocking();
        final FormDataParser parser = FormParserFactory.builder().build().createParser(exchange);
        if (parser != null) {
            try {
                FormData formData = parser.parseBlocking();
                String currentPassword = formData.getFirst("current_password").getValue();
                String newPassword = formData.getFirst("new_password").getValue();

                Response response = changePassword(currentPassword, newPassword);
                sendResponse(exchange, response.getStatusCode(), response.getData().toString());
            } catch (Exception e) {
                sendResponse(exchange, 500, "{\"error\":\"Server error\"}");
            }
        } else {
            sendResponse(exchange, 400, "{\"error\":\"Bad request\"}");
        }
    }

    private Response changePassword( String currentPassword, String newPassword) {
        try {
            int teacherId = LoginTeacher.getAuthenticatedTeacherId();

            Response authenticateResponse = authenticateTeacher(teacherId, currentPassword);
            if (authenticateResponse.getStatusCode() != 200) {

                return new Response(403, new JSONObject().put("error", "Current password is incorrect"));
            }


            Map<String, String> fieldValues = Map.of("password", newPassword);
            Response updateResponse = Dynamic_Controller.update("Teacher", "teacher_id", teacherId, fieldValues);

            if (updateResponse.getStatusCode() == 200) {
                return new Response(200, new JSONObject().put("message", "Password updated successfully"));
            } else {
                return new Response(500, new JSONObject().put("error", "Failed to update password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(500, new JSONObject().put("error", "Server error"));
        }
    }

    private Response authenticateTeacher(int teacherId, String password) {
        try {
            String whereClause = "teacher_id = " + teacherId;
            Response selectResponse = Dynamic_Controller.select("Teacher", List.of("password"), whereClause, null, null, null, null, null, null, null, null);

            JSONObject responseData = new JSONObject(selectResponse.getData().toString());
            if (responseData.length() == 0) {
                return new Response(404, new JSONObject().put("error", "Teacher not found"));
            }

            String storedPassword = responseData.getString("password");

            if (password.equals(storedPassword)) {
                return new Response(200, new JSONObject());
            } else {
                return new Response(403, new JSONObject().put("error", "Authentication failed, invalid password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(500, new JSONObject().put("error", "Server error"));
        }
    }

    private void sendResponse(HttpServerExchange exchange, int statusCode, String jsonData) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(jsonData);
    }
}
