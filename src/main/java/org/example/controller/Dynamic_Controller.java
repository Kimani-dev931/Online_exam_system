package org.example.controller;

import org.example.QueryManager;
import org.example.Response;
import org.example.Database_Connection;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dynamic_Controller {
    public static Response add(String tableName, Map<String, String> fieldValues) {
        Connection connection = null;
        try {
            connection = Database_Connection.getConnection();
            String insertSQL = QueryManager.constructInsertStatement(tableName, fieldValues);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            int paramIndex = 1;
            for (String fieldName : fieldValues.keySet()) {
                preparedStatement.setObject(paramIndex++, fieldValues.get(fieldName));
            }

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating" +tableName+ "failed, no rows affected.");
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
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Response update(String tableName, String idColumn, int idValue, Map<String, String> fieldValues) {
        Connection connection = null;
        try {
            connection = Database_Connection.getConnection();
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
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Response object_update(String tableName, String idColumn, int idValue, Map<String, Object> fieldValues) {
        Connection connection = null;
        try {
            connection = Database_Connection.getConnection();
            boolean useCurrentTimestamp = "CURRENT_TIMESTAMP".equals(fieldValues.get("date_modified"));
            String updateSQL = QueryManager.construct_hashmap_object_UpdateStatement(tableName, idColumn, idValue, fieldValues, useCurrentTimestamp);
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
                return new Response(200, jsonData);
            } else {
                return new Response(204, new HashMap<>());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Internal server error
            return new Response(500);
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public static Response select(String tableName, List<String> columns, String whereClause, String groupBy, String orderBy, String havingClause, Integer limit, Integer offset, List<String> joinClauses, String databaseType, Map<String, String> likeConditions) {
        Connection connection = null;
        try {
            connection = Database_Connection.getConnection();
            Object data = QueryManager.dynamicSelect(connection, tableName, columns, whereClause, groupBy, orderBy, havingClause, limit, joinClauses, databaseType, offset, likeConditions);
            return new Response(200, data);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(500); // Internal server error
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
