package org.example.handlers.reports;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.controllers.Reports;
import org.example.Response;

import static org.example.DatabaseConnectionApp.connection;

public class fetch_Exams_Results_For_Student implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String studentId = exchange.getQueryParameters().get("studentId").getFirst();
        String examId = exchange.getQueryParameters().get("examId").getFirst();
        try {
            Response response = Reports.fetchExamResultsForStudent(connection, Integer.parseInt(studentId),Integer.parseInt(examId));
            sendResponse(exchange, response.getStatusCode(), response.getData().toString()); // Assuming response.getData() returns a String or can be converted to String
        }
        catch (NumberFormatException e) {
            sendResponse(exchange, StatusCodes.BAD_REQUEST, "{\"error\":\"Invalid student/exam ID format\"}");
        }
        catch (Exception e) {
            sendResponse(exchange, StatusCodes.INTERNAL_SERVER_ERROR, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
        }
    }

    private void sendResponse(HttpServerExchange exchange, int statusCode, String jsonData) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(jsonData);
    }
}
