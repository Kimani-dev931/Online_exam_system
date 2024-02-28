package org.example.handlers.teacher;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.Response;
import org.example.Teacher;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static org.example.DatabaseConnectionApp.connection;

public class addTeachers implements HttpHandler {
    @Override
    public void handleRequest(final HttpServerExchange exchange) {
        // Determine the content type
        String contentType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);

        if (contentType != null && contentType.contains("application/json")) {
            // Handle JSON
            exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
                try {
                    JSONObject json = new JSONObject(message);
                    Map<String, String> fieldValues = jsonToMap(json);
                    performDatabaseOperation(exchange1, fieldValues);
                } catch (Exception e) {
                    sendResponse(exchange1, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
                }
            });
        } else if (contentType != null && contentType.contains("multipart/form-data")) {
            // Handle FormData
            exchange.startBlocking();
            final FormDataParser parser = FormParserFactory.builder().build().createParser(exchange);
            if (parser != null) {
                exchange.dispatch(() -> {
                    try {
                        FormData formData = parser.parseBlocking();
                        Map<String, String> fieldValues = new HashMap<>();
                        for (String fieldName : formData) {
                            FormData.FormValue formValue = formData.getFirst(fieldName);
                            if (formValue.isFile()) {
                                // Handle file uploads here
                            } else {
                                // Regular form field
                                fieldValues.put(fieldName, formValue.getValue());
                            }
                        }
                        performDatabaseOperation(exchange, fieldValues);
                    } catch (Exception e) {
                        sendResponse(exchange, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
                    }
                });
            }
        } else {
            // Unsupported Content Type
            sendResponse(exchange, StatusCodes.UNSUPPORTED_MEDIA_TYPE, "{\"error\":\"Unsupported Content Type\"}");
        }
    }

    private void performDatabaseOperation(HttpServerExchange exchange, Map<String, String> fieldValues) {
        try {
            Response response = Teacher.insertteacher("Teacher", fieldValues, connection);

            // Check if the response is null
            if (response == null) {
                // Handle the null response scenario, possibly as an internal server error
                sendResponse(exchange, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Database operation failed\"}");
                return; // Stop further processing
            }

            sendResponse(exchange, response.getStatusCode(), response.getData().toString());
        } catch (Exception e) {
            sendResponse(exchange, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
        }
    }


    private void sendResponse(HttpServerExchange exchange, int statusCode, String jsonData) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(jsonData);
    }

    private static Map<String, String> jsonToMap(JSONObject json) {
        Map<String, String> map = new HashMap<>();
        json.keys().forEachRemaining(key -> map.put(key, json.optString(key)));
        return map;
    }
}
