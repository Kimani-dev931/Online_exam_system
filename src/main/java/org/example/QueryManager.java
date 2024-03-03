package org.example;

import com.mysql.cj.xdevapi.JsonArray;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
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
    public static Object dynamicSelect(Connection connection, String tableName, List<String> columns,
                                          String whereClause, String groupBy, String orderBy, String havingClause, Integer limit,
                                          List<String> joinClauses, String databaseType, Integer offset,Map<String, String> likeConditions) throws SQLException {
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
//        if (whereClause != null && !whereClause.isEmpty()) {
//            query.append(" WHERE ").append(whereClause);
//        }

        List<String> conditions = new ArrayList<>();
        if (whereClause != null && !whereClause.isEmpty()) {
            conditions.add(whereClause);
        }

        // LIKE conditions
        if (likeConditions != null && !likeConditions.isEmpty()) {
            for (Map.Entry<String, String> entry : likeConditions.entrySet()) {
                conditions.add(entry.getKey() + " LIKE '%" + entry.getValue() + "%'");
            }
        }

        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        // Group by
        if (groupBy != null && !groupBy.isEmpty()) {
            query.append(" GROUP BY ").append(groupBy);
        }

        // Having clause
        if (havingClause != null && !havingClause.isEmpty()) {
            query.append(" HAVING ").append(havingClause);
        }

        // Order by
        if (orderBy != null && !orderBy.isEmpty()) {
            query.append(" ORDER BY ").append(orderBy);
        }
        if (limit != null) {
            query.append(" LIMIT ").append(limit);
            // Only append OFFSET if it's not null and a LIMIT has been specified
            if (offset != null) {
                query.append(" OFFSET ").append(offset);
            }
        }
        // Adjusting LIMIT and OFFSET based on the database type
//        if (limit != null) {
//            if ("MySQL".equalsIgnoreCase(databaseType) || "PostgreSQL".equalsIgnoreCase(databaseType)) {
//                query.append(" LIMIT ").append(limit);
//                if (offset != null) {
//                    query.append(" OFFSET ").append(offset);
//                }
//            } else if ("MicrosoftSQL".equalsIgnoreCase(databaseType)) {
//                // For SQL Server, OFFSET must be used with ORDER BY, so ensure orderBy is not empty
//                if (orderBy == null || orderBy.isEmpty()) {
//                    throw new SQLException("ORDER BY clause is required for OFFSET and FETCH with SQL Server");
//                }
//                if (offset != null) {
//                    query.append(" OFFSET ").append(offset).append(" ROWS");
//                    if (limit != null) {
//                        query.append(" FETCH NEXT ").append(limit).append(" ROWS ONLY");
//                    }
//                }
//            }
//        }

        PreparedStatement stmt = connection.prepareStatement(query.toString());
        ResultSet rs = stmt.executeQuery();

        return resultSetToJson(rs);
    }

//    public static JSONArray resultSetToJsonArray(ResultSet rs) throws SQLException {
//        JSONArray json = new JSONArray();
//        ResultSetMetaData rsmd = rs.getMetaData();
//        int numColumns = rsmd.getColumnCount();
//
//        while (rs.next()) {
//            JSONObject obj = new JSONObject();
//            for (int i = 1; i <= numColumns; i++) {
//                String column_name = rsmd.getColumnName(i);
//                obj.put(column_name, rs.getObject(column_name));
//            }
//            json.put(obj);
//        }
//        return json;
//    }

    public static Object resultSetToJson(ResultSet rs) throws SQLException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();

        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            JSONObject obj = new JSONObject();
            for (int i = 1; i <= numColumns; i++) {
                String column_name = rsmd.getColumnName(i);
                obj.put(column_name, rs.getObject(column_name));
            }
            json.put(obj);
        }

        // If there's only one record, return it as a JSONObject.
        if (rowCount == 1) {
            return json.getJSONObject(0);
        } else {
            // For multiple records, return the JSONArray.
            return json;
        }
    }







}
