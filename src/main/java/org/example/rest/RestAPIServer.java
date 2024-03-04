package org.example.rest;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.PathHandler;
import org.example.rest.base.CORSHandler;
import org.example.rest.base.FallBack;

import java.nio.charset.StandardCharsets;

import static org.example.MainApp.config;

public class RestAPIServer {
    public static void start() {
        try {

            String BASE_REST_API_URL = "/api/rest";
//

            PathHandler pathHandler = Handlers.path()
                    .addPrefixPath(BASE_REST_API_URL+"/reports", Routes.reports())
                    .addPrefixPath(BASE_REST_API_URL+"/exam", Routes.exam())
                    .addPrefixPath(BASE_REST_API_URL+"/classes", Routes.classes())
                    .addPrefixPath(BASE_REST_API_URL+"/options", Routes.options())
                    .addPrefixPath(BASE_REST_API_URL+"/questions", Routes.questions())
                    .addPrefixPath(BASE_REST_API_URL+"/responses", Routes.responses())
                    .addPrefixPath(BASE_REST_API_URL+"/students", Routes.student())
                    .addPrefixPath(BASE_REST_API_URL+"/subjects", Routes.subjects())
                    .addPrefixPath(BASE_REST_API_URL+"/teachers", Routes.teacher())

                    .addPrefixPath("/", new FallBack())
                    ;

            Undertow server = Undertow.builder()
                    .setServerOption(UndertowOptions.DECODE_URL, true)
                    .setServerOption(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name())
                    .setIoThreads(Integer.parseInt(config.getiothreads()))
                    .setWorkerThreads(Integer.parseInt(config.getiothreads()))
                    .addHttpListener(Integer.parseInt(config.getundertowserverport()),config.getundertowserverhost())
                    .setHandler(new CORSHandler(pathHandler))
                    .build();

            server.start();

            System.out.println(" Rest API Server started at: " +config.getundertowserverhost()+ ":" + config.getundertowserverport());
            System.out.println();
        }

        catch (Exception e) {
            System.err.println("Error starting RestAPIServer: (" + e.getMessage() + ")");
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
