package org.example.controllers;

import org.example.QueryManager;
import org.example.Response;
import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Class {


//    public static Response insertclass(String tableName, Map<String, String> fieldValues, Connection connection) {
//        try {
//            String insertSQL = QueryManager.constructInsertStatement(tableName, fieldValues);
//            // Use PreparedStatement with RETURN_GENERATED_KEYS to get the auto-generated keys (if any)
//            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
//            int paramIndex = 1;
//            for (String fieldName : fieldValues.keySet()) {
//                preparedStatement.setObject(paramIndex++, fieldValues.get(fieldName));
//            }
//
//            int affectedRows = preparedStatement.executeUpdate();
//            if (affectedRows == 0) {
//
//                return new Response(500, "Creating user failed, no rows affected.");
//            }
//
//
//            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
//            if (generatedKeys.next()) {
//
//                long generatedId = generatedKeys.getLong(1); // Change 1 to the column index of your auto-increment key
//                fieldValues.put("id", Long.toString(generatedId)); // Assuming "id" is the key you want to use
//            }
//
//            // Close the PreparedStatement
//            preparedStatement.close();
//            JSONObject jsonData = new JSONObject(fieldValues);
//            // Return a Response object with status code 201 and the inserted data
//            return new Response(201, jsonData);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Return a Response object with status code 500 and error message in case of an exception
//            return new Response(500, "Error inserting data: " + e.getMessage());
//        }
//    }

    public static Response insertclass(String tableName, Map<String, String> fieldValues, Connection connection) {
        try {
            String insertSQL = QueryManager.constructInsertStatement(tableName, fieldValues);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            int paramIndex = 1;
            for (String fieldName : fieldValues.keySet()) {
                preparedStatement.setObject(paramIndex++, fieldValues.get(fieldName));
            }

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            JSONObject jsonData = new JSONObject(fieldValues);
            return new Response(201, jsonData); // 201 Created

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getSQLState().startsWith("23")) { // SQL state code for integrity constraint violation, which includes duplicates
                return new Response(409, new JSONObject().put("error", "Duplicate entry")); // 409 Conflict
            } else {
                return new Response(500, new JSONObject().put("error", "Database error: " + e.getMessage())); // 500 Internal Server Error
            }
        }
    }


    public static Response updateClass(String tableName, String idColumn, int idValue, Map<String, String> fieldValues, Connection connection) {
        try {
            boolean useCurrentTimestamp = "CURRENT_TIMESTAMP".equals(fieldValues.get("date_modified"));
            String updateSQL = QueryManager.constructUpdateStatement(tableName, idColumn, idValue, fieldValues, useCurrentTimestamp);
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);

            int paramIndex = 1;
            for (String key : fieldValues.keySet()) {
                if (!(useCurrentTimestamp && "date_modified".equals(key))) {
                    preparedStatement.setObject(paramIndex++, fieldValues.get(key));
                }
            }

            int affectedRows = preparedStatement.executeUpdate();
            preparedStatement.close();

            if (affectedRows > 0) {
                JSONObject jsonData = new JSONObject(fieldValues);
                return new Response(200, jsonData); // Assuming successful update
            } else {
                return new Response(204, new HashMap<>()); // No content updated
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(500); // Internal server error
        }
    }


    public static Response selectclass(Connection connection, String tableName, List<String> columns,
                                         String whereClause, String groupBy, String orderBy, String havingClause, Integer limit, Integer offset,
                                         List<String> joinClauses, String databaseType,Map<String, String> likeConditions) throws SQLException {
        Object data = QueryManager.dynamicSelect(connection, tableName, columns, whereClause, groupBy, orderBy, havingClause, limit, joinClauses, databaseType, offset,likeConditions);
        // Assuming the operation is always successful and returns a 200 status code
        return new Response(200, data);
    }
}
