package org.example;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Exam {
    public static void InsertExam(String tableName, Map<String, String> fieldValues, Connection connection) {
        try {
            String insertSQL = QueryManager.constructInsertStatement(tableName, fieldValues);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
            int paramIndex = 1;
            for (String fieldName : fieldValues.keySet()) {
                preparedStatement.setObject(paramIndex++, fieldValues.get(fieldName));
            }

            preparedStatement.executeUpdate();
            System.out.println("Data saved successfully in table " + tableName);
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//    public static void updateExam(String tableName, String idColumn, int idValue, Map<String, String> fieldValues, Connection connection) {
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
   public static void updateExam(String tableName, String idColumn, int idValue, Map<String, String> fieldValues, Connection connection) {
      try {
        boolean useCurrentTimestamp = "CURRENT_TIMESTAMP".equals(fieldValues.get("date_modified"));

        // Construct the update statement
        String updateSQL = QueryManager.constructUpdateStatement(tableName, idColumn, idValue, fieldValues, useCurrentTimestamp);

        PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);

        int paramIndex = 1;
        for (String key : fieldValues.keySet()) {

            if (!(useCurrentTimestamp && "date_modified".equals(key))) {
                preparedStatement.setObject(paramIndex++, fieldValues.get(key));
            }
        }

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Record updated successfully in table " + tableName);
        } else {
            System.out.println("No record updated.");
        }
        preparedStatement.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
 }

    public static JSONArray selectExam(Connection connection, String tableName, List<String> columns,
                                          String whereClause, String groupBy, String orderBy, Integer limit,
                                          List<String> joinClauses) throws SQLException {
        return QueryManager.dynamicSelect(connection, tableName, columns, whereClause, groupBy, orderBy, limit, joinClauses);
    }
}
