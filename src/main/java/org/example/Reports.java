package org.example;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Reports {
    public static Response exams_set_by_a_Teacher(Connection connection, int teacherId) {
        try {
            List<String> columns = Arrays.asList(
                    "e.exam_id",
                    "e.class_id",
                    "e.subject_id",
                    "e.exam_date",
                    "e.starting_time",
                    "e.time_taken",
                    "e.exam_name",
                    "t.first_name",
                    "t.last_name"
            );

            List<String> joinClauses = Arrays.asList("JOIN Teacher t ON e.teacher_id = t.teacher_id");
            String whereClause = "t.teacher_id = " + teacherId;


            Response response = Student.selectStudent(
                    connection,
                    "Exam e",
                    columns,
                    whereClause,
                    null,
                    null,
                    null,
                    null,
                    null,
                    joinClauses,
                    null
            );


            return response;
        } catch (SQLException e) {
            e.printStackTrace();

            return new Response(500, "Error fetching exams: " + e.getMessage());
        }
    }




}
