package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private int statusCode;
    private Object data; // Use Object to support both Map and JSONArray
    private String message;

    // Constructor for responses with Map data
    public Response(int statusCode, Map<String, String> data) {
        this.statusCode = statusCode;
        this.data = (data != null) ? data : new HashMap<>(); // Ensure data is never null
        this.message = ""; // Default message to empty if not provided
    }

    // Constructor for responses with JSONArray data
    public Response(int statusCode, JSONArray data) {
        this.statusCode = statusCode;
        this.data = (data != null) ? data : new JSONArray(); // Ensure data is never null
        this.message = ""; // Default message to empty if not provided
    }

    // Constructor for error responses without specific data, using a message
    public Response(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = new JSONObject(); // Initialize data to an empty JSONObject for consistency
    }

    // Additional constructor to handle null data explicitly
    public Response(int statusCode) {
        this(statusCode, (String) null); // Delegate to the constructor that accepts a message
    }

    // Getters and potentially setters
    public int getStatusCode() {
        return statusCode;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    // Method to convert this Response object to JSON
    public JSONObject toJson() {
        JSONObject response = new JSONObject();
        response.put("statusCode", this.statusCode);
        response.put("data", this.data);
        response.put("message", this.message);
        return response;
    }

    // Optionally, a toString method for debugging
    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
