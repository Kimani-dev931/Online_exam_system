package org.example;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Responses {

    public static void Insertresponses(String tableName, Map<String, String> fieldValues, Connection connection) {
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
    public static JSONArray selectResponses(Connection connection, String tableName, List<String> columns,
                                          String whereClause, String groupBy, String orderBy,String havingClause, Integer limit,Integer offset,
                                          List<String> joinClauses,String databaseType) throws SQLException {
        return QueryManager.dynamicSelect(connection, tableName, columns, whereClause, groupBy, orderBy,havingClause, limit, joinClauses,databaseType,offset);
    }
}
