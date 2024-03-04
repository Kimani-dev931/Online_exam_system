package org.example.handlers.exam;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.controllers.Exam;
import org.example.Response;

public class findExamsById implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String idValue = exchange.getQueryParameters().get("examId").getFirst();
        String whereClause = "exam_id = " + idValue;
        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
            try {
                Response response = Exam.selectExam( "Exam", null, whereClause, null, null, null, null, null, null, null,null);

                // Assuming response.getData() returns a JSON string representation of an array
                String responseData = response.getData().toString();
                // Check if the response data is an empty array
                if ("[]".equals(responseData.trim())) {
                    sendResponse(exchange1, StatusCodes.NOT_FOUND, "{\"error\":\"examId " +idValue+ "  not found\"}");
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
