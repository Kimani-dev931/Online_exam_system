package org.example.handlers.student;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.Response;
import org.example.Student;

import static org.example.DatabaseConnectionApp.connection;

public class findAllSudents implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
            try {
                // Call the method to insert the student into the database
                Response response = Student.selectStudent(connection,"Student",null,null,null,null,null,null,null,null,null);
                // Send the response back to the client
                sendResponse(exchange1, response.getStatusCode(), response.getData().toString());
            } catch (Exception e) {
                // Catch any parsing or database exceptions and return an appropriate error message
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
