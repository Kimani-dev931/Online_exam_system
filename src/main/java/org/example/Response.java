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


    public Response(int statusCode, Map<String, String> data) {
        this.statusCode = statusCode;
        this.data = (data != null) ? data : new HashMap<>();
        this.message = "";
    }


    public Response(int statusCode, Object data) {
        this.statusCode = statusCode;
        this.data = (data != null) ? data : new JSONArray();
        this.message = "";
    }


    public Response(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = new JSONObject();
    }


    public Response(int statusCode) {
        this(statusCode, (String) null);
    }


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
