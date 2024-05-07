package org.example;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public static String generateWhereClause(String filterString) {

        return parseExpression(filterString).trim();
    }


//    public static String generateWhereClause(String filterString) {
//        if (!filterString.contains(":") && isDateFormat(filterString)) {
//            return filterString;  // Return the date string as it is if it matches the format
//        }
//        return parseExpression(filterString).trim();
//    }
//
//    private static boolean isDateFormat(String input) {
//        String datePattern = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$"; // Regex pattern to match date format
//        return input.matches(datePattern);
//    }
//


    private static String parseExpression(String expression) {
        StringBuilder whereClause = new StringBuilder();

        Pattern pattern = Pattern.compile("\\(([^()]+)\\)");
        Matcher matcher = pattern.matcher(expression);

        int lastEnd = 0;

        while (matcher.find()) {
            String before = expression.substring(lastEnd, matcher.start()).trim();
            if (!before.isEmpty()) {
                whereClause.append(parseLogicalOperators(before));
            }
            whereClause.append("(").append(parseLogicalOperators(matcher.group(1))).append(")");
            lastEnd = matcher.end();
        }
        String after = expression.substring(lastEnd).trim();
        if (!after.isEmpty()) {
            whereClause.append(parseLogicalOperators(after));
        }

        return whereClause.toString();
    }

    private static String parseLogicalOperators(String group) {
        StringBuilder whereClause = new StringBuilder();
        String[] parts = group.split("\\|");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.isEmpty()) continue;


            if (part.equals("AND") || part.equals("OR")) {
                whereClause.append(" ").append(part).append(" ");
            } else {
                whereClause.append(parseCondition(part));
            }
        }
        return whereClause.toString();
    }



    private static String parseCondition(String condition) {
        String[] parts = condition.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid condition format: " + condition);
        }

        String column = parts[0];
        String operator = parts[1];
        // Here am removing  the parentheses
        String value = parts[2].replaceAll("[()]", "");

        String sqlOperator = sqlConditions.get(operator);
        if (sqlOperator == null) {
            throw new IllegalArgumentException("Invalid operator: " + operator);
        }


        switch (operator) {
            case "in":
                return column + " IN (" + value + ")";
            case "inn":
                return column + " IS NOT NULL";
            case "eq":
                return column + " = '" + value + "'";
            case "nq":
                return column + " != '" + value + "'";
            case "gt":
                return column + " > '" + value + "'";
            case "gte":
                return column + " >= '" + value + "'";
            case "lt":
                return column + " < '" + value + "'";
            case "lte":
                return column + " <= '" + value + "'";
            case "bt":
                String[] betweenValues = value.split(",\\s*");
                if (betweenValues.length != 2)
                    throw new IllegalArgumentException("BETWEEN requires exactly two values");
                return column + " BETWEEN '" + betweenValues[0] + "' AND '" + betweenValues[1] + "'";
            case "sw":
                return column + " LIKE '" + value + "%'";
            case "ew":
                return column + " LIKE '%" + value + "'";
            case "lm":
                return column + " LIKE '%" + value + "%'";
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }





}



