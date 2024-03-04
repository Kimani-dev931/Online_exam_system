package org.example.handlers.subjects;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.Response;
import org.example.controllers.Subject;

public class findSubjectsById implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String idValue = exchange.getQueryParameters().get("subjectId").getFirst();
        String whereClause = "subject_id = " + idValue;
        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
            try {
                Response response = Subject.selectSubjects( "Subjects", null, whereClause, null, null, null, null, null, null, null,null);


                String responseData = response.getData().toString();
                // Check if the response data is an empty array
                if ("[]".equals(responseData.trim())) {
                    sendResponse(exchange1, StatusCodes.NOT_FOUND, "{\"error\":\"subjectId " +idValue+ "  not found\"}");
                } else {
                    sendResponse(exchange1, response.getStatusCode(), responseData);
                }
            } catch (Exception e) {
                sendResponse(exchange1, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
            }
        });
    }

    private void sendResponse(HttpServerExchange exchange, int statusCode, String jsonData) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(jsonData);
    }
}
