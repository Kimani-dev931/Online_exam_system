package org.example;

import org.json.JSONArray;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Class {
//    public static void InsertClass(String tableName, Map<String, String> fieldValues, Connection connection) {
//        try {
//            String insertSQL = QueryManager.constructInsertStatement(tableName, fieldValues);
//            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
//            int paramIndex = 1;
//            for (String fieldName : fieldValues.keySet()) {
//                preparedStatement.setObject(paramIndex++, fieldValues.get(fieldName));
//            }
//
//            preparedStatement.executeUpdate();
//            System.out.println("Data saved successfully in table " + tableName);
//            preparedStatement.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public static Response insertclass(String tableName, Map<String, String> fieldValues, Connection connection) {
        try {
            String insertSQL = QueryManager.constructInsertStatement(tableName, fieldValues);
            // Use PreparedStatement with RETURN_GENERATED_KEYS to get the auto-generated keys (if any)
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            int paramIndex = 1;
            for (String fieldName : fieldValues.keySet()) {
                preparedStatement.setObject(paramIndex++, fieldValues.get(fieldName));
            }

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {

                return new Response(500, "Creating user failed, no rows affected.");
            }


            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {

                long generatedId = generatedKeys.getLong(1); // Change 1 to the column index of your auto-increment key
                fieldValues.put("id", Long.toString(generatedId)); // Assuming "id" is the key you want to use
            }

            // Close the PreparedStatement
            preparedStatement.close();

            // Return a Response object with status code 201 and the inserted data
            return new Response(201, fieldValues);
        } catch (SQLException e) {
            e.printStackTrace();
            // Return a Response object with status code 500 and error message in case of an exception
            return new Response(500, "Error inserting data: " + e.getMessage());
        }
    }

    //    public static void updateClass(String tableName, String idColumn, int idValue, Map<String, String> fieldValues, Connection connection) {
//        try {
//            String updateSQL = QueryManager.constructUpdateStatement(tableName, idColumn, idValue, fieldValues);
//            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
//
//            int paramIndex = 1;
//            for (String value : fieldValues.values()) {
//                preparedStatement.setObject(paramIndex++, value);
//            }
//
//            int affectedRows = preparedStatement.executeUpdate();
//            if (affectedRows > 0) {
//                System.out.println("Record updated successfully in table " + tableName);
//            } else {
//                System.out.println("No record updated.");
//            }
//            preparedStatement.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void updateClass(String tableName, String idColumn, int idValue, Map<String, String> fieldValues, Connection connection) {
//        try {
//            boolean useCurrentTimestamp = "CURRENT_TIMESTAMP".equals(fieldValues.get("date_modified"));
//
//            // Construct the update statement
//            String updateSQL = QueryManager.constructUpdateStatement(tableName, idColumn, idValue, fieldValues, useCurrentTimestamp);
//
//            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
//
//            int paramIndex = 1;
//            for (String key : fieldValues.keySet()) {
//
//                if (!(useCurrentTimestamp && "date_modified".equals(key))) {
//                    preparedStatement.setObject(paramIndex++, fieldValues.get(key));
//                }
//            }
//
//            int affectedRows = preparedStatement.executeUpdate();
//            if (affectedRows > 0) {
//                System.out.println("Record updated successfully in table " + tableName);
//            } else {
//                System.out.println("No record updated.");
//            }
//            preparedStatement.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

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
                return new Response(200, fieldValues); // Assuming successful update
            } else {
                return new Response(204, new HashMap<>()); // No content updated
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(500); // Internal server error
        }
    }

//    public static JSONArray selectClass(Connection connection, String tableName, List<String> columns,
//                                        String whereClause, String groupBy, String orderBy, String havingClause, Integer limit, Integer offset,
//                                        List<String> joinClauses, String databaseType) throws SQLException {
//        return QueryManager.dynamicSelect(connection, tableName, columns, whereClause, groupBy, orderBy, havingClause, limit, joinClauses, databaseType, offset);
//    }

    public static Response selectclass(Connection connection, String tableName, List<String> columns,
                                         String whereClause, String groupBy, String orderBy, String havingClause, Integer limit, Integer offset,
                                         List<String> joinClauses, String databaseType) throws SQLException {
        JSONArray data = QueryManager.dynamicSelect(connection, tableName, columns, whereClause, groupBy, orderBy, havingClause, limit, joinClauses, databaseType, offset);
        // Assuming the operation is always successful and returns a 200 status code
        return new Response(200, data);
    }
}
