package org.example;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teacher {

    public static Response insertteacher(String tableName, Map<String, String> fieldValues, Connection connection) {
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


    public static Response updateTeacher(String tableName, String idColumn, int idValue, Map<String, String> fieldValues, Connection connection) {
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


    public static Response selectTeacher(Connection connection, String tableName, List<String> columns,
                                         String whereClause, String groupBy, String orderBy, String havingClause, Integer limit, Integer offset,
                                         List<String> joinClauses, String databaseType) throws SQLException {
        JSONArray data = QueryManager.dynamicSelect(connection, tableName, columns, whereClause, groupBy, orderBy, havingClause, limit, joinClauses, databaseType, offset);
        // Assuming the operation is always successful and returns a 200 status code
        return new Response(200, data);
    }

}


