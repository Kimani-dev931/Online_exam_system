package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class Reports {
    // 2 :Display all exams set by a teacher:
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

            List<String> joinClauses = Arrays.asList("JOIN Teacher t ON e.teacher_id = t.teacher_id", "JOIN Subjects sub ON e.subject_id = sub.subject_id");
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


    //3:Generate a report on the answers provided by a pupil for an exam and their percentage score
    public static Response fetchExamResultsForStudent(Connection connection, int studentId, int examId) {
        try {
            List<String> columns = Arrays.asList(
                    "s.student_id", "s.first_name", "s.last_name",
                    "q.exam_id", "q.question_text", "o.option_value", "o.correct_answer", "q.question_marks"
            );

            List<String> joinClauses = Arrays.asList(
                    "JOIN Questions q ON r.questions_id = q.questions_id",
                    "JOIN Options o ON r.option_id = o.option_id",
                    "JOIN Student s ON r.student_id = s.student_id"
            );

            String whereClause = "s.student_id = " + studentId + " AND q.exam_id = " + examId;

            Response response =  Exam.selectExam(connection, "Responses r", columns, whereClause, null, null, null, null, null, joinClauses, null);

            if (response.getStatusCode() == 200) {
                if (response.getData() instanceof JSONArray) {
                    JSONArray results = (JSONArray) response.getData();
                    JSONArray processedResults = new JSONArray();
                    double totalPossibleMarks = 0;
                    double totalPossiblescore = 0;
                    double percentageScore;


                    for (int i = 0; i < results.length(); i++) {
                        JSONObject row = results.getJSONObject(i);
                        boolean correctAnswer = row.getBoolean("correct_answer");
                        int questionMarks = row.getInt("question_marks");
                        double questionscore = correctAnswer ? questionMarks : 0;
                         totalPossibleMarks += questionMarks;
                         totalPossiblescore += questionscore;
                         percentageScore = totalPossiblescore / totalPossibleMarks *100;
                        row.put("percentage_score", percentageScore);
                        processedResults.put(row);

                        // Assuming you want to calculate the percentage of correct answers
                        // Here, we're just marking each question as fully correct or not

                    }


                    // Assuming Response constructor can handle JSONArray directly
                    return new Response(200, processedResults);
                } else {
                    return new Response(500, "Unexpected data type received");
                }
            } else {
                return response;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(500, "Error processing exam results: " + e.getMessage());
        }
    }



    //4: Generate a Report on the Top 5 Pupils with the Highest Scores in a Certain Exam
    public static Response fetch_Student_Scores_For_Exam(Connection connection, int examId) {
        try {
            List<String> columns = Arrays.asList(
                    "s.student_id", "s.first_name", "s.last_name",
                    "SUM(CASE WHEN o.correct_answer = 1 THEN q.question_marks ELSE 0 END) AS total_score"
            );

            List<String> joinClauses = Arrays.asList(
                    "JOIN Questions q ON r.questions_id = q.questions_id",
                    "JOIN Options o ON r.option_id = o.option_id",
                    "JOIN Student s ON r.student_id = s.student_id"
            );

            String whereClause = "q.exam_id = " + examId;
            String groupBy = "s.student_id";

            // Fetching total scores for each student
            Response studentScoresResponse = Exam.selectExam(connection, "Responses r", columns,
                    whereClause, groupBy, "total_score DESC", null, 5, null, joinClauses, null);

            if (studentScoresResponse.getStatusCode() != 200) {
                // If the query was not successful, return the response directly
                return studentScoresResponse;
            }

            // Assuming getData() returns a JSONArray
            JSONArray studentScores = (JSONArray) studentScoresResponse.getData();

            // Fetching total possible score for the exam
            PreparedStatement totalScoreStmt = connection.prepareStatement(
                    "SELECT SUM(question_marks) AS total_possible_score FROM Questions WHERE exam_id = " + examId
            );
            ResultSet totalScoreRs = totalScoreStmt.executeQuery();
            double totalPossibleScore = 0;
            if (totalScoreRs.next()) {
                totalPossibleScore = totalScoreRs.getDouble("total_possible_score");
            }
            totalScoreStmt.close();

            // Calculate percentage scores
            JSONArray processedResults = new JSONArray();
            for (int i = 0; i < studentScores.length(); i++) {
                JSONObject row = studentScores.getJSONObject(i);
                double totalScore = row.getDouble("total_score");
                double percentageScore = (totalScore / totalPossibleScore) * 100;
                row.put("percentage_score", percentageScore);
                processedResults.put(row);
            }

            // Assuming the operation was successful, return the processed results
            return new Response(200, processedResults);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(500, "Error processing exam scores: " + e.getMessage());
        }


    }
    public static Response generateStudentScoresReport(Connection connection) {
        try {
            List<String> columns = Arrays.asList(
                    "S.student_id",
                    "S.first_name",
                    "S.last_name",
                    "SUM(IF(sub.subject_text = 'English', Q.question_marks, 0)) AS English_Score",
                    "SUM(IF(sub.subject_text = 'Mathematics', Q.question_marks, 0)) AS Mathematics_Score",
                    "SUM(IF(sub.subject_text = 'Science', Q.question_marks, 0)) AS Science_Score",
                    "SUM(IF(sub.subject_text = 'Kiswahili', Q.question_marks, 0)) AS Kiswahili_Score",
                    "SUM(IF(sub.subject_text = 'Social Studies and Religious Education', Q.question_marks, 0)) AS SSRE_Score",
                    "(SUM(IF(sub.subject_text = 'English', Q.question_marks, 0)) + " +
                            "SUM(IF(sub.subject_text = 'Mathematics', Q.question_marks, 0)) + " +
                            "SUM(IF(sub.subject_text = 'Science', Q.question_marks, 0)) + " +
                            "SUM(IF(sub.subject_text = 'Kiswahili', Q.question_marks, 0)) + " +
                            "SUM(IF(sub.subject_text = 'Social Studies and Religious Education', Q.question_marks, 0))) AS Total_Score",
                    "((SUM(IF(sub.subject_text = 'English', Q.question_marks, 0)) + " +
                            "SUM(IF(sub.subject_text = 'Mathematics', Q.question_marks, 0)) + " +
                            "SUM(IF(sub.subject_text = 'Science', Q.question_marks, 0)) + " +
                            "SUM(IF(sub.subject_text = 'Kiswahili', Q.question_marks, 0)) + " +
                            "SUM(IF(sub.subject_text = 'Social Studies and Religious Education', Q.question_marks, 0)))/5) AS Average_Score"
            );
            List<String> joinClauses = Arrays.asList(
                    "JOIN Responses R ON S.student_id = R.student_id",
                    "JOIN Options O ON R.option_id = O.option_id AND O.correct_answer = true",
                    "JOIN Questions Q ON R.questions_id = Q.questions_id",
                    "JOIN Exam E ON Q.exam_id = E.exam_id",
                    "JOIN Subjects sub ON E.subject_id = sub.subject_id"
            );

            String groupBy = "S.student_id, S.first_name, S.last_name";
            String orderBy = "Total_Score DESC, Average_Score DESC";

            Response examResponse = Exam.selectExam(connection, "Student S", columns, null, groupBy, orderBy, null, null, null, joinClauses, null);

            if (examResponse.getStatusCode() != 200) {
                return examResponse; // Early return in case of error
            }

            // Process the result and generate CSV content
            JSONArray results = (JSONArray) examResponse.getData(); // Assuming getData() returns JSONArray
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("Student ID,First Name,Last Name,English Score,Mathematics Score,Science Score,Kiswahili Score,SSRE Score,Total Score,Average Score\n");

            for (int i = 0; i < results.length(); i++) {
                JSONObject row = results.getJSONObject(i);
                csvContent.append(row.getInt("student_id")).append(",");
                csvContent.append(row.getString("first_name")).append(",");
                csvContent.append(row.getString("last_name")).append(",");
                csvContent.append(row.getDouble("English_Score")).append(",");
                csvContent.append(row.getDouble("Mathematics_Score")).append(",");
                csvContent.append(row.getDouble("Science_Score")).append(",");
                csvContent.append(row.getDouble("Kiswahili_Score")).append(",");
                csvContent.append(row.getDouble("SSRE_Score")).append(",");
                csvContent.append(row.getDouble("Total_Score")).append(",");
                csvContent.append(row.getDouble("Average_Score")).append("\n");
            }

            // Define the directory where the CSV file will be saved
            String directoryPath = "report"; // Folder name
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdir(); // Create the directory if it doesn't exist
            }

            // Define the path to your output file within the directory
            String csvFilePath = directoryPath + File.separator + "output.csv";
            try (PrintWriter out = new PrintWriter(csvFilePath)) {
                out.println(csvContent.toString());
            }

            // Return a response with the CSV file path
            return new Response(200, csvFilePath);

        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(500, "SQL Error: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(500, "IO Error: " + e.getMessage());
        }
    }
//    private static void writeResultsToCSV(JSONArray results, String csvFilePath) throws IOException {
//        FileWriter csvWriter = new FileWriter(csvFilePath);
//        csvWriter.append("Student ID,First Name,Last Name,English Score,Mathematics Score,Science Score,Kiswahili Score,SSRE Score,Total Score,Average Score\n");
//
//        for (int i = 0; i < results.length(); i++) {
//            JSONObject row = results.getJSONObject(i);
//            csvWriter.append(row.getInt("student_id") + ",");
//            csvWriter.append(row.getString("first_name") + ",");
//            csvWriter.append(row.getString("last_name") + ",");
//            csvWriter.append(row.getDouble("English_Score") + ",");
//            csvWriter.append(row.getDouble("Mathematics_Score") + ",");
//            csvWriter.append(row.getDouble("Science_Score") + ",");
//            csvWriter.append(row.getDouble("Kiswahili_Score") + ",");
//            csvWriter.append(row.getDouble("SSRE_Score") + ",");
//            csvWriter.append(row.getDouble("Total_Score") + ",");
//            csvWriter.append(row.getDouble("Average_Score") + "\n");
//        }
//
//        csvWriter.flush();
//        csvWriter.close();
//    }






}