package org.example;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.sql.*;

class DatabaseConnectionApp {
    private static Connection connection = null;
    private static final String SECRET_KEY = "beadc627d00ec777340bf6f06ece360fe1762e8b4408504516afd194dc303c77";

    public static void main(String[] args) {


        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("config.xml"));

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();





            List<DatabaseConfig> configs = new ArrayList<>();


            DatabaseConfig config = new DatabaseConfig();

            String databaseType = (String) xpath.compile("/database-config/database-type").evaluate(doc, XPathConstants.STRING);
            config.setDatabaseType(databaseType);

            String databaseName = (String) xpath.compile("/database-config/database-name").evaluate(doc, XPathConstants.STRING);
            config.setDatabaseName(databaseName);

            String databaseHost = (String) xpath.compile("/database-config/database-host").evaluate(doc, XPathConstants.STRING);
            config.setDatabaseHost(databaseHost);

            String username = (String) xpath.compile("/database-config/username/text()").evaluate(doc, XPathConstants.STRING);
            config.setUsername(username);

            String password = (String) xpath.compile("/database-config/password/text()").evaluate(doc, XPathConstants.STRING);
            config.setPassword(password);

            String usernameEncrypted = (String) xpath.compile("/database-config/username/@ENCRYPTED").evaluate(doc, XPathConstants.STRING);
            config.setUsernameEncrypted("YES".equals(usernameEncrypted));

            String passwordEncrypted = (String) xpath.compile("/database-config/password/@ENCRYPTED").evaluate(doc, XPathConstants.STRING);
            config.setPasswordEncrypted("YES".equals(passwordEncrypted));


            // Add the DatabaseConfig object to the list
            configs.add(config);


            String usernameEncryptedAttribute = (String) xpath.compile("/database-config/username/@ENCRYPTED").evaluate(doc, XPathConstants.STRING);
            boolean shouldEncryptUsername = !"YES".equals(usernameEncryptedAttribute);

            // Check if the "ENCRYPTED" attribute is set to "NO" for password
            String passwordEncryptedAttribute = (String) xpath.compile("/database-config/password/@ENCRYPTED").evaluate(doc, XPathConstants.STRING);
            boolean shouldEncryptPassword = !"YES".equals(passwordEncryptedAttribute);
            // Encrypt the username and password if needed
            if (shouldEncryptUsername) {
                config.setUsername(encrypt(config.getUsername(), SECRET_KEY));
                config.setUsernameEncrypted(true);
            }

            if (shouldEncryptPassword) {
                config.setPassword(encrypt(config.getPassword(), SECRET_KEY));
                config.setPasswordEncrypted(true);
            }
            // Update the XML with the modified data
            config.updateXmlElement(doc);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("config.xml"));
            transformer.transform(source, result);




            String connectionString = "";
            if ("MySQL".equalsIgnoreCase(config.getDatabaseType())) {
                connectionString = "jdbc:mysql://" + config.getDatabaseHost() + "/" + config.getDatabaseName();

            } else if ("PostgreSQL".equalsIgnoreCase(config.getDatabaseType())) {
                connectionString = "jdbc:postgresql://" + config.getDatabaseHost() + "/" + config.getDatabaseName();

            } else if ("MicrosoftSQL".equalsIgnoreCase(config.getDatabaseType())) {
                connectionString = "jdbc:sqlserver://" + config.getDatabaseHost() + ";databaseName=" + config.getDatabaseName();

            }

            String decryptedUsername = config.isUsernameEncrypted() ? decrypt(config.getUsername(), SECRET_KEY) : config.getUsername();
            String decryptedPassword = config.isPasswordEncrypted() ? decrypt(config.getPassword(), SECRET_KEY) : config.getPassword();

            connection = DriverManager.getConnection(connectionString, decryptedUsername, decryptedPassword);
//            String tableName = "Student"; // Example table name
//            Map<String, String> fieldValues = new HashMap<>();
//            fieldValues.put("class_id", "2");
//            fieldValues.put("first_name", "Harrison");
//            fieldValues.put("last_name", "Kimani");
//            fieldValues.put("gender", "Male");
//            fieldValues.put("Date_of_birth", "2003-05-29");
//            fieldValues.put("Parent_phone_number", "0720808535");
//            fieldValues.put(" user_name", "Pkimani@$");
//            fieldValues.put(" password", "P845$");
//
//            Student.prepareAndExecuteData(tableName, fieldValues, connection);


//            String tableName = "Teacher"; // Example table name
//            Map<String, String> fieldValues = new HashMap<>();
//            fieldValues.put("first_name", "Stacy");
//            fieldValues.put("last_name", "Wanjiru");
//            fieldValues.put("tsc_number", "A450500");
//            fieldValues.put("id_number", "33076943");
//            fieldValues.put("username", "Edwin");
//            fieldValues.put("phone_number", "0750680543");
//            fieldValues.put("education_level", "Diploma");
//            fieldValues.put("email", "Wanjiru@gmail.com");
//            fieldValues.put("password", "Wanjiru$267*");
//
//            Teacher.executeteacher(tableName, fieldValues, connection);

//              String tableName = "Subjects"; // Example table name
//              Map<String, String> fieldValues = new HashMap<>();
//              fieldValues.put("subject_text", "Social studies and Religious Education");
//              Subject.insertsubject(tableName, fieldValues, connection);

//              String tableName = "Exam"; // Example table name
//              Map<String, String> fieldValues = new HashMap<>();
//              fieldValues.put("class_id", "2");
//              fieldValues.put("subject_id", "1");
//              fieldValues.put("teacher_id", "1");
//              fieldValues.put("starting_time", "10:30:00");
//              fieldValues.put("time_taken", "90");
//              fieldValues.put("exam_name", "Jesma");
//              fieldValues.put("exam_date ", "2024-01-28");
//              Exam.InsertExam(tableName, fieldValues, connection);

//
//            String tableName = "Questions";
//            Map<String, String> fieldValues = new HashMap<>();
//            fieldValues.put("exam_id", "5");
//            fieldValues.put("question_text ", "1: In the Bible, who came as the savior of the world?");
//            fieldValues.put(" question_marks", "10");
//            Questions.Insertquestions(tableName, fieldValues, connection);

//            String tableName = "Options";
//            Map<String, String> fieldValues = new HashMap<>();
//            fieldValues.put("question_id", "17");
//            fieldValues.put("option_label", "D");
//            fieldValues.put("option_value", "David");
//            fieldValues.put("correct_answer", "0");
//            Options.Insertoptions(tableName, fieldValues, connection);

//            String tableName = "Responses";
//            Map<String, String> fieldValues = new HashMap<>();
//            fieldValues.put("student_id", "11");
//            fieldValues.put("questions_id", "17");
//            fieldValues.put("option_id", "73");
//            Responses.Insertresponses(tableName, fieldValues, connection);

            // This will execute 'SELECT * FROM Student
//            try {
//
//                JSONArray results = Student.selectStudent(connection, "Student", null, null, null, null, null, null);
//                System.out.println(results.toString());
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
            // 2 :Display all exams set by a teacher:
//            try {
//                // Columns to be selected
//                List<String> columns = Arrays.asList(
//                        "e.exam_id",
//                        "e.class_id",
//                        "e.subject_id",
//                        "e.exam_date",
//                        "e.starting_time",
//                        "e.time_taken",
//                        "t.first_name",
//                        "t.last_name"
//                );
//
//                // Join clause
//                List<String> joinClauses = Arrays.asList(
//                        "JOIN Teacher t ON e.teacher_id = t.teacher_id"
//                );
//
//                // Where clause
//                String whereClause = "t.teacher_id = 1";
//
//                // This will execute the specified SELECT query
//                JSONArray results = Student.selectStudent(
//                        connection,
//                        "Exam e", // Table name with alias
//                        columns,
//                        whereClause,
//                        null, // groupBy
//                        null, // orderBy
//                        null, // limit
//                        joinClauses
//                );
//
//                System.out.println(results.toString());
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }

            //3:Generate a report on the answers provided by a pupil for an exam and their percentage score
//            try {
//                List<String> columns = Arrays.asList(
//                        "s.student_id", "s.first_name", "s.last_name",
//                        "q.exam_id", "q.question_text", "o.option_value", "o.correct_answer", "q.question_marks"
//                );
//
//                List<String> joinClauses = Arrays.asList(
//                        "JOIN Question q ON r.questions_id = q.questions_id",
//                        "JOIN Option o ON r.option_id = o.option_id",
//                        "JOIN Students s ON r.student_id = s.student_id"
//               );
//
//               String whereClause = "s.student_id = 3 AND q.exam_id = 1";
//
//               JSONArray results = Exam.selectExam(connection, "Responses r", columns,
//                        whereClause, null, null, null, joinClauses);
//
//                // Process the results to calculate percentage score
//                JSONArray processedResults = new JSONArray();
//                for (int i = 0; i < results.length(); i++) {
//                    JSONObject row = results.getJSONObject(i);
//                    // Extract necessary data for calculation
//                    boolean correctAnswer = row.getBoolean("correct_answer");
//                    int questionMarks = row.getInt("question_marks");
//
//
//
//                    double percentageScore = correctAnswer ? questionMarks : 0;
//
//                    // Add the calculated score to the result
//                    row.put("percentage_score", percentageScore);
//                    processedResults.put(row);
//                }
//
//                System.out.println(processedResults.toString());
//           } catch (SQLException e) {
//               e.printStackTrace();
//            }
            //4: Generate a Report on the Top 5 Pupils with the Highest Scores in a Certain Exam
//            try {
//                List<String> columns = Arrays.asList(
//                        "s.student_id", "s.first_name", "s.last_name",
//                        "SUM(CASE WHEN o.correct_answer = TRUE THEN q.question_marks ELSE 0 END) AS total_score"
//                );
//
//                List<String> joinClauses = Arrays.asList(
//                        "JOIN Question q ON r.questions_id = q.questions_id",
//                        "JOIN Option o ON r.option_id = o.option_id",
//                        "JOIN Students s ON r.student_id = s.student_id"
//                );
//
//                String whereClause = "q.exam_id = 1";
//                String groupBy = "s.student_id";
//
//                // Fetching total scores for each student
//                JSONArray studentScores = Exam.selectExam(connection, "Responses r", columns,
//                        whereClause, groupBy, "total_score DESC", 5, joinClauses);
//
//                // Fetching total possible score for the exam
//                PreparedStatement totalScoreStmt = connection.prepareStatement(
//                        "SELECT SUM(question_marks) AS total_possible_score FROM Question WHERE exam_id = 1"
//                );
//                ResultSet totalScoreRs = totalScoreStmt.executeQuery();
//                double totalPossibleScore = 0;
//                if (totalScoreRs.next()) {
//                    totalPossibleScore = totalScoreRs.getDouble("total_possible_score");
//                }
//
//                // Calculate percentage scores
//                JSONArray processedResults = new JSONArray();
//                for (int i = 0; i < studentScores.length(); i++) {
//                    JSONObject row = studentScores.getJSONObject(i);
//                    double totalScore = row.getDouble("total_score");
//                    double percentageScore = (totalScore / totalPossibleScore) * 100;
//                    row.put("percentage_score", percentageScore);
//                    processedResults.put(row);
//                }
//
//                System.out.println(processedResults.toString());
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }

            //5:Generate a report sheet of the scores for all pupils in each of the exams done and rank them from the highest average score to lowest.
            try {
                List<String> columns = new ArrayList<>();
                // Define the columns to select, including subject scores and overall average
                if ("MySQL".equalsIgnoreCase(config.getDatabaseType())){
                   columns = Arrays.asList(
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
                } else if ("MicrosoftSQL".equalsIgnoreCase(config.getDatabaseType())) {
                    columns = Arrays.asList(
                        "S.student_id",
                        "S.first_name",
                        "S.last_name",
                        "SUM(CASE WHEN sub.subject_text = 'English' THEN Q.question_marks ELSE 0 END) AS English_Score",
                        "SUM(CASE WHEN sub.subject_text = 'Mathematics' THEN Q.question_marks ELSE 0 END) AS Mathematics_Score",
                        "SUM(CASE WHEN sub.subject_text = 'Science' THEN Q.question_marks ELSE 0 END) AS Science_Score",
                        "SUM(CASE WHEN sub.subject_text = 'Kiswahili' THEN Q.question_marks ELSE 0 END) AS Kiswahili_Score",
                        "SUM(CASE WHEN sub.subject_text = 'Social Studies and Religious Education' THEN Q.question_marks ELSE 0 END) AS SSRE_Score",
                        "(SUM(CASE WHEN sub.subject_text = 'English' THEN Q.question_marks ELSE 0 END) + " +
                                "SUM(CASE WHEN sub.subject_text = 'Mathematics' THEN Q.question_marks ELSE 0 END) + " +
                                "SUM(CASE WHEN sub.subject_text = 'Science' THEN Q.question_marks ELSE 0 END) + " +
                                "SUM(CASE WHEN sub.subject_text = 'Kiswahili' THEN Q.question_marks ELSE 0 END) + " +
                                "SUM(CASE WHEN sub.subject_text = 'Social Studies and Religious Education' THEN Q.question_marks ELSE 0 END)) AS Total_Score",
                        "((SUM(CASE WHEN sub.subject_text = 'English' THEN Q.question_marks ELSE 0 END) + " +
                                "SUM(CASE WHEN sub.subject_text = 'Mathematics' THEN Q.question_marks ELSE 0 END) + " +
                                "SUM(CASE WHEN sub.subject_text = 'Science' THEN Q.question_marks ELSE 0 END) + " +
                                "SUM(CASE WHEN sub.subject_text = 'Kiswahili' THEN Q.question_marks ELSE 0 END) + " +
                                "SUM(CASE WHEN sub.subject_text = 'Social Studies and Religious Education' THEN Q.question_marks ELSE 0 END))/5) AS Average_Score"
                    );

                }else if ("PostgreSQL".equalsIgnoreCase(config.getDatabaseType())) {
                    columns = Arrays.asList(
                            "S.student_id",
                            "S.first_name",
                            "S.last_name",
                            "SUM(CASE WHEN sub.subject_text = 'English' THEN Q.question_marks ELSE 0 END) AS English_Score",
                            "SUM(CASE WHEN sub.subject_text = 'Mathematics' THEN Q.question_marks ELSE 0 END) AS Mathematics_Score",
                            "SUM(CASE WHEN sub.subject_text = 'Science' THEN Q.question_marks ELSE 0 END) AS Science_Score",
                            "SUM(CASE WHEN sub.subject_text = 'Kiswahili' THEN Q.question_marks ELSE 0 END) AS Kiswahili_Score",
                            "SUM(CASE WHEN sub.subject_text = 'Social Studies and Religious Education' THEN Q.question_marks ELSE 0 END) AS SSRE_Score",
                            "(SUM(CASE WHEN sub.subject_text = 'English' THEN Q.question_marks ELSE 0 END) + " +
                                    "SUM(CASE WHEN sub.subject_text = 'Mathematics' THEN Q.question_marks ELSE 0 END) + " +
                                    "SUM(CASE WHEN sub.subject_text = 'Science' THEN Q.question_marks ELSE 0 END) + " +
                                    "SUM(CASE WHEN sub.subject_text = 'Kiswahili' THEN Q.question_marks ELSE 0 END) + " +
                                    "SUM(CASE WHEN sub.subject_text = 'Social Studies and Religious Education' THEN Q.question_marks ELSE 0 END)) AS Total_Score",
                            "((SUM(CASE WHEN sub.subject_text = 'English' THEN Q.question_marks ELSE 0 END) + " +
                                    "SUM(CASE WHEN sub.subject_text = 'Mathematics' THEN Q.question_marks ELSE 0 END) + " +
                                    "SUM(CASE WHEN sub.subject_text = 'Science' THEN Q.question_marks ELSE 0 END) + " +
                                    "SUM(CASE WHEN sub.subject_text = 'Kiswahili' THEN Q.question_marks ELSE 0 END) + " +
                                    "SUM(CASE WHEN sub.subject_text = 'Social Studies and Religious Education' THEN Q.question_marks ELSE 0 END))/5) AS Average_Score"
                    );


                }
                // Define the JOIN clauses to include in the query
                List<String> joinClauses = Arrays.asList(
                        "JOIN Responses R ON S.student_id = R.student_id",
                        "JOIN Options O ON R.option_id = O.option_id AND O.correct_answer = 1",
                        "JOIN Questions Q ON R.questions_id = Q.questions_id",
                        "JOIN Exam E ON Q.exam_id = E.exam_id",
                        "JOIN Subjects sub ON E.subject_id = sub.subject_id"
                );

                // Define the GROUP BY clause
                String groupBy = "S.student_id, S.first_name, S.last_name";

                // Execute the query and get the results
                JSONArray results = Exam.selectExam(connection, "Student S", columns, null, groupBy, null, null, joinClauses);

                // Write the results to a CSV file
                FileWriter csvWriter = new FileWriter("output.csv");
                csvWriter.append("Student ID,First Name,Last Name,English Score,Mathematics Score,Science Score,Kiswahili Score,SSRE Score,Total Score,Average Score\n");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject row = results.getJSONObject(i);
                    csvWriter.append(row.getInt("student_id") + ",");
                    csvWriter.append(row.getString("first_name") + ",");
                    csvWriter.append(row.getString("last_name") + ",");
                    csvWriter.append(row.getDouble("English_Score") + ",");
                    csvWriter.append(row.getDouble("Mathematics_Score") + ",");
                    csvWriter.append(row.getDouble("Science_Score") + ",");
                    csvWriter.append(row.getDouble("Kiswahili_Score") + ",");
                    csvWriter.append(row.getDouble("SSRE_Score") + ",");
                    csvWriter.append(row.getDouble("Total_Score") + ",");
                    csvWriter.append(row.getDouble("Average_Score") + "\n");
                }

                csvWriter.flush();
                csvWriter.close();

                System.out.println("CSV Report sheet file created successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



//            //update student
//            String tableName = "Student";
//            String idColumn = "student_id";
//            int idValue = 3;
//            Map<String, String> fieldValues = new HashMap<>();
//            fieldValues.put("last_name", "Wambui");
//            fieldValues.put("date_modified", "CURRENT_TIMESTAMP");
//            Student.updateStudent(tableName, idColumn, idValue, fieldValues, connection);

//            try {
//                JSONArray results = Student.selectStudent(connection, "Student", Arrays.asList("student_id", "first_name"),
//                        "gender = 'Male'", null, "student_id DESC", 10, Arrays.asList("JOIN Class ON Student.class_id = Class.class_id"));
//                System.out.println(results.toString());
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }




            //String insertSQL = QueryManager.constructInsertStatement(tableName, fieldValues);
            //PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);



            //int paramIndex = 1;
            //for (String fieldName : fieldValues.keySet()) {
                //preparedStatement.setObject(paramIndex++, fieldValues.get(fieldName));
            //}

            //preparedStatement.executeUpdate();
            //preparedStatement.close();





            // Close the database connection when done
            connection.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static String encrypt(String input, String key) throws Exception {
        byte[] keyBytes = hexStringToByteArray(key);

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Key must be 32 bytes long");
        }

        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static String decrypt(String encryptedText, String key) throws Exception {
        byte[] keyBytes = hexStringToByteArray(key);

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Key must be 32 bytes long");
        }

        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }




}



