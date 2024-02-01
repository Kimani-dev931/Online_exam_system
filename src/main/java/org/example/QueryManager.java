package org.example;
import com.mysql.cj.xdevapi.JsonArray;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;


public class QueryManager {
    public static String constructInsertStatement(String tableName, Map<String, String> fieldValues) {
        StringBuilder fieldNames = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (String field : fieldValues.keySet()) {
            if (fieldNames.length() > 0) {
                fieldNames.append(", ");
                placeholders.append(", ");
            }
            fieldNames.append(field);
            placeholders.append("?");
        }

        return "INSERT INTO " + tableName + " (" + fieldNames.toString() + ") VALUES (" + placeholders.toString() + ")";
    }

//    public static String constructUpdateStatement(String tableName, String idColumn, int idValue, Map<String, String> fieldValues) {
//        StringBuilder setClause = new StringBuilder();
//        for (String field : fieldValues.keySet()) {
//            if (setClause.length() > 0) {
//                setClause.append(", ");
//            }
//            setClause.append(field).append(" = ?");
//        }
//
//        return "UPDATE " + tableName + " SET " + setClause.toString() + " WHERE " + idColumn + " = " + idValue;
//    }
public static String constructUpdateStatement(String tableName, String idColumn, int idValue, Map<String, String> fieldValues, boolean useCurrentTimestamp) {
    StringBuilder setClause = new StringBuilder();
    for (String field : fieldValues.keySet()) {
        if (setClause.length() > 0) {
            setClause.append(", ");
        }
        if (useCurrentTimestamp && "date_modified".equals(field)) {
            setClause.append(field).append(" = CURRENT_TIMESTAMP");
        } else {
            setClause.append(field).append(" = ?");
        }
    }

    return "UPDATE " + tableName + " SET " + setClause.toString() + " WHERE " + idColumn + " = " + idValue;
}


    // Core method to execute any SQL query
    public static JSONArray dynamicSelect(Connection connection, String tableName, List<String> columns,
                                          String whereClause, String groupBy, String orderBy, Integer limit,
                                          List<String> joinClauses) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT ");

        // Columns
        if (columns == null || columns.isEmpty()) {
            query.append("*");
        } else {
            query.append(String.join(", ", columns));
        }

        // Table
        query.append(" FROM ").append(tableName);

        // Joins
        if (joinClauses != null && !joinClauses.isEmpty()) {
            for (String join : joinClauses) {
                query.append(" ").append(join);
            }
        }

        // Where clause
        if (whereClause != null && !whereClause.isEmpty()) {
            query.append(" WHERE ").append(whereClause);
        }

        // Group by
        if (groupBy != null && !groupBy.isEmpty()) {
            query.append(" GROUP BY ").append(groupBy);
        }

        // Order by
        if (orderBy != null && !orderBy.isEmpty()) {
            query.append(" ORDER BY ").append(orderBy);
        }

        // Limit
        if (limit != null) {
            query.append(" LIMIT ").append(limit);
        }

        PreparedStatement stmt = connection.prepareStatement(query.toString());
        ResultSet rs = stmt.executeQuery();

        return resultSetToJsonArray(rs);
    }

    public static JSONArray resultSetToJsonArray(ResultSet rs) throws SQLException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();

        while (rs.next()) {
            JSONObject obj = new JSONObject();
            for (int i = 1; i <= numColumns; i++) {
                String column_name = rsmd.getColumnName(i);
                obj.put(column_name, rs.getObject(column_name));
            }
            json.put(obj);
        }
        return json;
    }


}
