package org.example.handlers.reports;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.example.Reports;
import org.example.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;

import static org.example.DatabaseConnectionApp.connection;
public class GenerateStudentReport implements HttpHandler{
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Ensure the method is a GET request
        if (!exchange.getRequestMethod().equalToString("GET")) {
            exchange.setStatusCode(405); // Method Not Allowed
            exchange.getResponseSender().send("Method Not Allowed");
            return;
        }

        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        exchange.startBlocking();

        // Directly specify the file name and directory
        String directoryPath = "report"; // The directory where the report is stored
        String fileName = "output.csv"; // The specific file we want to fetch
        String csvFilePath = directoryPath + File.separator + fileName;
        File file = new File(csvFilePath);

        if (file.exists()) {
            // Set headers for file download
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/csv");
            exchange.getResponseHeaders().put(Headers.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");

            // Efficient file sending without loading the whole file into memory
            byte[] fileContent = Files.readAllBytes(file.toPath()); // Read the file content
            exchange.getResponseSender().send(new String(fileContent)); // Send the content as a String
        } else {
            // File not found scenario
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.setStatusCode(404); // Not Found
            exchange.getResponseSender().send("{\"error\":\"Report file not found\"}");
        }
    }
}
