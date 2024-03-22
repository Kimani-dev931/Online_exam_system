package org.example;

import java.util.*;

public class Filter {
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

//    public static String generateWhereClause(String filterString) {
//        try {
//            StringBuilder whereClause = new StringBuilder();
//            boolean isFirstCondition = true; // avoid prepending AND/OR for the first condition
//            boolean isNotOperator = false; // handle NOT operator
//
//            // Your existing logic to parse and generate the WHERE clause
//
//            return whereClause.toString().trim();
//        } catch (Exception e) {
//            // Handle the exception and generate a JSON error response
//            StringBuilder errorResponse = new StringBuilder();
//            errorResponse.append("{\"error\": \"");
//            errorResponse.append(e.getMessage());
//            errorResponse.append("\"}");
//            return errorResponse.toString();
//        }
//    }


    public static String generateWhereClause(String filterString) {
     try{
        StringBuilder whereClause = new StringBuilder();
        boolean isFirstCondition = true;
        boolean isNotOperator = false;

        // Split filterString by logical operators and remove "|" characters
        String[] parts = filterString.split("\\|AND\\||\\|OR\\||\\|NOT\\|");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("\\|", "").trim(); // Remove "|" characters and trim the part
        }

        // Iterate over parts
        for (String part : parts) {
            // Trim and skip empty parts
            part = part.trim();
            if (part.isEmpty()) continue;

            // Determine logical operator
            if (part.equals("AND") || part.equals("OR")) {
                whereClause.append(" ").append(part).append(" ");
                isFirstCondition = true; // Reset isFirstCondition for the next condition group
                continue;
            } else if (part.equals("NOT")) {
                isNotOperator = true;
                continue;
            }

            // Handling conditions
            String[] conditionParts = part.split(":");

            if (conditionParts.length != 3) {
                throw new IllegalArgumentException("Invalid condition: " + part);
            }
            String column = conditionParts[0];
            String operator = conditionParts[1];
            String value = conditionParts[2].replaceAll("[()]", ""); // Removing parentheses for IN conditions

            String sqlOperator = sqlConditions.get(operator);
            if (sqlOperator == null) {
                throw new IllegalArgumentException("Invalid operator: " + operator);
            }

            // Add logical operator if not the first condition and not using NOT operator
            if (!isFirstCondition && !isNotOperator) {
                whereClause.append(" AND ");
            } else if (isNotOperator) {
                whereClause.append(" NOT ");
                isNotOperator = false; // Reset isNotOperator flag after using it
            }

            // Construct the condition based on the operator
            switch (operator) {
                case "in":
                    whereClause.append(column).append(" IN (").append(value).append(")");
                    break;
                case "inn":
                    whereClause.append(column).append(" IS NOT NULL");
                    break;
                case "eq":
                    whereClause.append(column).append(" = '").append(value).append("'");
                    break;
                case "nq":
                    whereClause.append(column).append(" != '").append(value).append("'");
                    break;
                case "gt":
                    whereClause.append(column).append(" > '").append(value).append("'");
                    break;
                case "gte":
                    whereClause.append(column).append(" >= '").append(value).append("'");
                    break;
                case "lt":
                    whereClause.append(column).append(" < '").append(value).append("'");
                    break;
                case "lte":
                    whereClause.append(column).append(" <= '").append(value).append("'");
                    break;
                case "bt":
                    String[] betweenValues = value.split(",\\s*");
                    if (betweenValues.length != 2) throw new IllegalArgumentException("BETWEEN requires exactly two values");
                    whereClause.append(column).append(" BETWEEN '").append(betweenValues[0]).append("' AND '").append(betweenValues[1]).append("'");
                    break;
                case "sw":
                    whereClause.append(column).append(" LIKE '").append(value).append("%'");
                    break;
                case "ew":
                    whereClause.append(column).append(" LIKE '%").append(value).append("'");
                    break;
                case "lm":
                    whereClause.append(column).append(" LIKE '%").append(value).append("%'");
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }

            isFirstCondition = false;
        }

        return whereClause.toString().trim();
     } catch (Exception e) {
         StringBuilder errorResponse = new StringBuilder();
         errorResponse.append("{\"error\": Invalid operator search param\"");
         errorResponse.append(e.getMessage());
         return errorResponse.toString();
     }
    }

}
