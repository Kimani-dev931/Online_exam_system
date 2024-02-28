package org.example.rest.base;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
public class InvalidMethod implements HttpHandler{
    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.setStatusCode(StatusCodes.METHOD_NOT_ALLOWED);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

        exchange.getResponseSender().send("Method " + exchange.getRequestMethod() + " not allowed");
    }
}
