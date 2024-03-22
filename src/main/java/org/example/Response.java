package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private int statusCode;
    private Object data;
    private String message;

    private int page;
    private int pages;
   private int pageSize;
   private int totalRecords;

    // Constructor for responses with Map data
    public Response(int statusCode, Map<String, String> data) {
        this.statusCode = statusCode;
        this.data = (data != null) ? data : new HashMap<>(); // Ensure data is never null
        this.message = "";
    }

    // Constructor for responses with JSONArray data
    public Response(int statusCode, Object data) {
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
        this(statusCode, (String) null);
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


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    // Method to convert this Response object to JSON
    public JSONObject toJson() {
        JSONObject response = new JSONObject();
        response.put("statusCode", this.statusCode);
        response.put("data", this.data);
        response.put("message", this.message);
        response.put("page", this.page);
        response.put("pages", this.pages);
        response.put("pageSize", this.pageSize);
        response.put("totalRecords", this.totalRecords);
        return response;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", page=" + page +
                ", pages=" + pages +
                ", pageSize=" + pageSize +
                ", totalRecords=" + totalRecords +
                '}';
    }
}
//public class Response {
//    public int statusCode;
//    public Object data;
//    private String message;
//    private int page;
//    private int pages;
//    private int pageSize;
//    private int totalRecords;
//
//    // Constructors
//
//    // Constructor for responses with Map data
//    public Response(int statusCode, Map<String, String> data) {
//        this.statusCode = statusCode;
//        this.data = (data != null) ? data : new HashMap<>(); // Ensure data is never null
//        this.message = "";
//    }
//
//    // Constructor for responses with JSONArray data
//    public Response(int statusCode, Object data) {
//        this.statusCode = statusCode;
//        this.data = (data != null) ? data : new JSONArray(); // Ensure data is never null
//        this.message = ""; // Default message to empty if not provided
//    }
//
//    // Constructor for error responses without specific data, using a message
//    public Response(int statusCode, String message) {
//        this.statusCode = statusCode;
//        this.message = message;
//        this.data = new JSONObject(); // Initialize data to an empty JSONObject for consistency
//    }
//
//    // Additional constructor to handle null data explicitly
//    public Response(int statusCode) {
//        this(statusCode, (String) null);
//    }
//
//    // Getters and setters for new fields
//    public int getPage() {
//        return page;
//    }
//
//    public void setPage(int page) {
//        this.page = page;
//    }
//
//    public int getPages() {
//        return pages;
//    }
//
//    public void setPages(int pages) {
//        this.pages = pages;
//    }
//
//    public int getPageSize() {
//        return pageSize;
//    }
//
//    public void setPageSize(int pageSize) {
//        this.pageSize = pageSize;
//    }
//
//    public int getTotalRecords() {
//        return totalRecords;
//    }
//
//    public void setTotalRecords(int totalRecords) {
//        this.totalRecords = totalRecords;
//    }
//
//    // Getters for existing fields remain the same
//
//    // Method to convert this Response object to JSON
//    public JSONObject toJson() {
//        JSONObject response = new JSONObject();
//        response.put("statusCode", this.statusCode);
//        response.put("data", this.data);
//        response.put("message", this.message);
//        response.put("page", this.page);
//        response.put("pages", this.pages);
//        response.put("pageSize", this.pageSize);
//        response.put("totalRecords", this.totalRecords);
//        return response;
//    }
//
//    // toString method for debugging
//    @Override
//    public String toString() {
//        return "Response{" +
//                "statusCode=" + statusCode +
//                ", data=" + data +
//                ", message='" + message + '\'' +
//                ", page=" + page +
//                ", pages=" + pages +
//                ", pageSize=" + pageSize +
//                ", totalRecords=" + totalRecords +
//                '}';
//    }
//}