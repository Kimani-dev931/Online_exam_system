package org.example;

import com.mysql.cj.xdevapi.JsonArray;

import java.sql.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;


public class Student {


    public static Response insertstudent(String tableName, Map<String, String> fieldValues, Connection connection) {
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

            // Assuming the data inserted is the data you want to return
            return new Response(201, fieldValues); // 201 Created
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Or handle error appropriately
        }
    }

    public static Response updateStudent(String tableName, String idColumn, int idValue, Map<String, String> fieldValues, Connection connection) {
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
                return new Response(204, new HashMap<>());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(500); // Internal server error
        }
    }


    //    public static JSONArray selectStudent(Connection connection, String tableName, List<String> columns,
//                                          String whereClause, String groupBy, String orderBy, String havingClause, Integer limit, Integer offset,
//                                          List<String> joinClauses, String databaseType) throws SQLException {
//        return QueryManager.dynamicSelect(connection, tableName, columns, whereClause, groupBy, orderBy, havingClause, limit, joinClauses, databaseType, offset);
//    }
    public static Response selectStudent(Connection connection, String tableName, List<String> columns,
                                         String whereClause, String groupBy, String orderBy, String havingClause, Integer limit, Integer offset,
                                         List<String> joinClauses, String databaseType) throws SQLException {
        JSONArray data = QueryManager.dynamicSelect(connection, tableName, columns, whereClause, groupBy, orderBy, havingClause, limit, joinClauses, databaseType, offset);
        // Assuming the operation is always successful and returns a 200 status code
        return new Response(200, data);
    }


}
