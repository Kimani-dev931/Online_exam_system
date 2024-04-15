package org.example.rest.base;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

public class CORSHandler implements HttpHandler{

    private final HttpHandler httpHandler;

    public CORSHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().add(new HttpString("Access-Control-Max-Age"), "3600");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), "*");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Headers"), "*");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
        exchange.setStatusCode(200);


        if (exchange.getRequestMethod().equalToString("OPTIONS")) {
            exchange.endExchange();
            return;
        }

        httpHandler.handleRequest(exchange);
    }
}
