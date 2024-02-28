package org.example.handlers.classes;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.Class;
import org.example.Response;

import static org.example.DatabaseConnectionApp.connection;

public class findAllClasses implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
            try {
                // Call the method to insert the student into the database
                Response response = Class.selectclass(connection,"Class",null,null,null,null,null,null,null,null,null);
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
