package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class filter {
    private static final Map<String, String> sqlConditions = new HashMap<>();

    static {
        sqlConditions.put("in", "IN");
        sqlConditions.put("inn", "IS NOT NULL");
        sqlConditions.put("eq", "=");
        sqlConditions.put("nq", "!=");
        sqlConditions.put("gt", ">");
        sqlConditions.put("gte", ">=");
        sqlConditions.put("lt", "<");
        sqlConditions.put("lte", "<=");
        sqlConditions.put("AND", "AND");
        sqlConditions.put("OR", "OR");
        sqlConditions.put("NOT", "NOT");
        sqlConditions.put("bt", "BETWEEN");
        sqlConditions.put("sw", "LIKE");
        sqlConditions.put("ew", "LIKE");
        sqlConditions.put("lm", "LIKE");
    }

    public static String generateWhereClause(Map<String, String[]> queryParams) {
        StringJoiner whereClause = new StringJoiner(" AND ", " WHERE ", "");
        queryParams.forEach((key, values) -> {
            String[] parts = key.split(":", 2);
            String operator = parts[0];
            String column = parts[1];
            String sqlOperator = sqlConditions.get(operator);

            if (sqlOperator == null) {
                throw new IllegalArgumentException("Invalid operator: " + operator);
            }

            String valueStr = String.join(", ", values); // Common case of handling values as a comma-separated list

            switch (operator) {
                case "in":
                    whereClause.add(column + " IN (" + valueStr + ")");
                    break;
                case "inn":
                    whereClause.add(column + " IS NOT NULL");
                    break;
                case "eq":
                    whereClause.add(column + " = '" + values[0] + "'");
                    break;
                case "nq":
                    whereClause.add(column + " != '" + values[0] + "'");
                    break;
                case "gt":
                    whereClause.add(column + " > '" + values[0] + "'");
                    break;
                case "gte":
                    whereClause.add(column + " >= '" + values[0] + "'");
                    break;
                case "lt":
                    whereClause.add(column + " < '" + values[0] + "'");
                    break;
                case "lte":
                    whereClause.add(column + " <= '" + values[0] + "'");
                    break;
                case "bt":
                    if (values.length != 2) throw new IllegalArgumentException("BETWEEN requires exactly two values");
                    whereClause.add(column + " BETWEEN '" + values[0] + "' AND '" + values[1] + "'");
                    break;
                case "sw":
                    whereClause.add(column + " LIKE '" + values[0] + "%'");
                    break;
                case "ew":
                    whereClause.add(column + " LIKE '%" + values[0] + "'");
                    break;
                case "lm":
                    whereClause.add(column + " LIKE '%" + values[0] + "%'");
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        });

        return whereClause.toString();
    }

}
