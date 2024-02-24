package org.example;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;

public class API {

    public static void startServer(Connection connection,DatabaseConfig config) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(exchange -> {
                    String path = exchange.getRequestPath();
                    String[] segments = path.split("/");

                    // Handle PUT requests for updates
                    if (segments.length >= 4 && "api".equals(segments[1].toLowerCase()) && exchange.getRequestMethod().equalToString("PUT")) {
                        handlePutRequest(exchange, segments, connection);
                    }
                    // Handle POST requests for creating new records
                    else if (segments.length >= 3 && "api".equals(segments[1].toLowerCase()) && exchange.getRequestMethod().equalToString("POST")) {
                        handlePostRequest(exchange, segments, connection);
                    }
                    // Handle GET requests for fetching data
                    else if (segments.length <= 6 && "api".equals(segments[1].toLowerCase()) && exchange.getRequestMethod().equalToString("GET")) {
                        handleGetRequest(exchange, segments, connection,config);
                    } else {
                        exchange.setStatusCode(404); // Not Found
                        exchange.getResponseSender().send("{\"error\":\"Endpoint not found\"}");
                    }
                }).build();

        server.start();
    }

    private static void handlePutRequest(HttpServerExchange exchange, String[] segments, Connection connection) {
        int idValue;
        try {
            idValue = Integer.parseInt(segments[3]); // Assuming ID is always the fourth segment
            exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
                try {
                    JSONObject json = new JSONObject(message);
                    Map<String, String> fieldValues = jsonToMap(json);
                    Response response;
                    switch (segments[1].toLowerCase()) {
                        case "student/updatestudent":
                            response = Student.updateStudent("Student", "student_id", idValue, fieldValues, connection);
                            break;
                        case "teacher/updateteacher":
                            response = Teacher.updateTeacher("Teacher", "teacher_id", idValue, fieldValues, connection);
                            break;
                        case "subject/updatesubject":
                            response = Subject.updateSubjects("Subjects", "subject_id", idValue, fieldValues, connection);
                            break;
                        case "question/updatequestion":
                            response = Questions.updateQuestions("Questions", "question_id", idValue, fieldValues, connection);
                            break;
                        case "class/updateclass":
                            response = Class.updateClass("Class", "class_id", idValue, fieldValues, connection);
                            break;
                        case "exam/updateexam":
                            response = Exam.updateExam("Exam", "exam_id", idValue, fieldValues, connection);
                            break;
                        case "options/updateoptions":
                            response = Options.updateOptions("Options", "option_id", idValue, fieldValues, connection);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown entity");
                    }

                    exchange1.setStatusCode(response.getStatusCode());
                    exchange1.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange1.getResponseSender().send(new JSONObject(response.getData()).toString());
                } catch (Exception e) {
                    exchange1.setStatusCode(500);
                    exchange1.getResponseSender().send("{\"error\":\"Error processing request: " + e.getMessage() + "\"}");
                }
            });
        } catch (NumberFormatException e) {
            exchange.setStatusCode(400); // Bad Request
            exchange.getResponseSender().send("{\"error\":\"Invalid ID format\"}");
        }
    }

    private static void handlePostRequest(HttpServerExchange exchange, String[] segments, Connection connection) {
        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
            try {
                JSONObject json = new JSONObject(message);
                Map<String, String> fieldValues = jsonToMap(json);
                Response response;
                String actionPath = segments[2].toLowerCase() + "/" + segments[3].toLowerCase();
                switch (actionPath) {
                    case "student/addstudent":
                        response = Student.insertstudent("Student", fieldValues, connection);
                        break;
                    case "teacher/addteacher":
                        response = Teacher.insertteacher("Teacher",fieldValues, connection);
                        break;
                    case "subject/addsubject":
                        response = Subject.insertsubjects("Subjects", fieldValues, connection);
                        break;
                    case "question/addquestion":
                        response = Questions.insertquestions("Questions", fieldValues, connection);
                        break;
                    case "class/addclass":
                        response = Class.insertclass("Class",fieldValues, connection);
                        break;
                    case "exam/addexam":
                        response = Exam.insertexam("Exam", fieldValues, connection);
                        break;
                    case "options/addoptions":
                        response = Options.insertoptions("Options",fieldValues, connection);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown entity");
                }


                exchange1.setStatusCode(response.getStatusCode());
                exchange1.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange1.getResponseSender().send(new JSONObject(response.getData()).toString());
            } catch (Exception e) {
                exchange1.setStatusCode(500);
                exchange1.getResponseSender().send("{\"error\":\"Error processing request: " + e.getMessage() + "\"}");
            }
        });
    }

    private static void handleGetRequest(HttpServerExchange exchange, String[] segments, Connection connection,DatabaseConfig config) {
        try {
            // General structure: /api/[entity]/[action]/[optional id(s)]
            String entity = segments.length > 2 ? segments[2].toLowerCase() : "";
            String action = segments.length > 3 ? segments[3].toLowerCase() : "";

            Response response = new Response(404, "Not Found"); // Default response if no matching route is found


            switch (entity) {
                case "exams":
                    if ("exams-set-by-teacher".equals(action) && segments.length > 4) {
                        // Assuming the route is /api/exams/exams-set-by-teacher/teacher_id
                        int teacherId = Integer.parseInt(segments[4]);
                        response = Reports.exams_set_by_a_Teacher(connection, teacherId);
                    }
                    break;
                case "student":
                    if ("student-answers-report".equals(action) && segments.length > 5) {
                        int studentId = Integer.parseInt(segments[4]);
                        int examId = Integer.parseInt(segments[5]);
                        response = Reports.fetchExamResultsForStudent(connection, studentId, examId);
                    }
                    break;
                case "exam-scores":
                    if ("student-exam-scores".equals(action) && segments.length > 4) {
                        int examId = Integer.parseInt(segments[4]);
                        response = Reports.fetch_Student_Scores_For_Exam(connection, examId);
                    }
                    break;
                case "exam":
                    if ("exam-report".equals(action)) {
                        response = Reports.generateStudentScoresReport(connection,config);
                    }
                    break;


            }

            // Send the response
            exchange.setStatusCode(response.getStatusCode());
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(response.toJson().toString());

        } catch (NumberFormatException e) {
            // Bad Request
            exchange.setStatusCode(400);
            exchange.getResponseSender().send("{\"error\":\"Invalid ID format\"}");
        } catch (Exception e) {
            // Internal Server Error
            exchange.setStatusCode(500);
            exchange.getResponseSender().send("{\"error\":\"Server error: " + e.getMessage() + "\"}");
        }
    }

    private static Map<String, String> jsonToMap(JSONObject json) {
        Map<String, String> map = new HashMap<>();
        json.keys().forEachRemaining(key -> {
            map.put(key, json.getString(key));
        });
        return map;
    }


}
