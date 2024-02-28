package org.example.rest;

import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import org.example.handlers.classes.addClass;
import org.example.handlers.classes.findAllClasses;
import org.example.handlers.classes.findClassById;
import org.example.handlers.classes.updateClass;
import org.example.handlers.exam.addExam;
import org.example.handlers.exam.findAllExams;
import org.example.handlers.exam.findExamsById;
import org.example.handlers.options.addOptions;
import org.example.handlers.options.findAllOptions;
import org.example.handlers.options.findOptionsById;
import org.example.handlers.options.updateOptions;
import org.example.handlers.questions.addQuestions;
import org.example.handlers.questions.findAllQuestions;
import org.example.handlers.questions.findQuestionsById;
import org.example.handlers.questions.updateQuestions;
import org.example.handlers.reports.GenerateStudentReport;
import org.example.handlers.reports.exam_set_by_teacher;
import org.example.handlers.reports.fetch_Exams_Results_For_Student;
import org.example.handlers.reports.Top_5_Student_Scores;
import org.example.handlers.exam.updateExam;
import org.example.handlers.responses.addResponses;
import org.example.handlers.responses.findAllResponses;
import org.example.handlers.responses.findResponsesById;
import org.example.handlers.responses.updateResponses;
import org.example.handlers.student.addStudent;
import org.example.handlers.student.findAllSudents;
import org.example.handlers.student.findStudentById;
import org.example.handlers.student.updateStudent;
import org.example.handlers.subjects.addSubjects;
import org.example.handlers.subjects.findAllSubjects;
import org.example.handlers.subjects.findSubjectsById;
import org.example.handlers.subjects.updateSubjects;
import org.example.handlers.teacher.addTeachers;
import org.example.handlers.teacher.findAllTeachers;
import org.example.handlers.teacher.findTeachersById;
import org.example.handlers.teacher.updateTeacher;
import org.example.rest.base.Dispatcher;
import org.example.rest.base.FallBack;
import org.example.rest.base.InvalidMethod;

public class Routes {

    public static RoutingHandler exam() {
        return Handlers.routing()
                .post("", new Dispatcher(new BlockingHandler(new addExam())))
                .get("", new Dispatcher(new findAllExams()))
                .get("/{examId}", new Dispatcher(new findExamsById()))
                .put("/{examId}", new Dispatcher(new BlockingHandler(new updateExam())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler reports() {
        return Handlers.routing()
                .get("/exams-set-teacher/{teacherId}", new Dispatcher(new exam_set_by_teacher()))
                .get("/fetch-exam-results-for-student/{studentId}/{examId}", new Dispatcher(new fetch_Exams_Results_For_Student()))
                .get("/top-5-student-scores/{examId}", new Dispatcher(new Top_5_Student_Scores()))
                .get("/student-report", new Dispatcher(new GenerateStudentReport()))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));

    }
    public static RoutingHandler classes() {
        return Handlers.routing()
                .post("", new Dispatcher(new BlockingHandler(new addClass())))
                .get("", new Dispatcher(new findAllClasses()))
                .get("/{classId}", new Dispatcher(new findClassById()))
                .put("/{classId}", new Dispatcher(new BlockingHandler(new updateClass())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler options() {
        return Handlers.routing()
                .post("", new Dispatcher(new BlockingHandler(new addOptions())))
                .get("", new Dispatcher(new findAllOptions()))
                .get("/{optionId}", new Dispatcher(new findOptionsById()))
                .put("/{optionId}", new Dispatcher(new BlockingHandler(new updateOptions())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler questions() {
        return Handlers.routing()
                .post("", new Dispatcher(new BlockingHandler(new addQuestions())))
                .get("", new Dispatcher(new findAllQuestions()))
                .get("/{questionsId}", new Dispatcher(new findQuestionsById()))
                .put("/{questionsId}", new Dispatcher(new BlockingHandler(new updateQuestions())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler responses() {
        return Handlers.routing()
                .post("", new Dispatcher(new BlockingHandler(new addResponses())))
                .get("", new Dispatcher(new findAllResponses()))
                .get("/{responseId}", new Dispatcher(new findResponsesById()))
                .put("/{responseId}", new Dispatcher(new BlockingHandler(new updateResponses())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler student() {
        return Handlers.routing()
                .post("", new Dispatcher(new BlockingHandler(new addStudent())))
                .get("", new Dispatcher(new findAllSudents()))
                .get("/{studentId}", new Dispatcher(new findStudentById()))
                .put("/{studentId}", new Dispatcher(new BlockingHandler(new updateStudent())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler subjects() {
        return Handlers.routing()
                .post("", new Dispatcher(new BlockingHandler(new addSubjects())))
                .get("", new Dispatcher(new findAllSubjects()))
                .get("/{subjectId}", new Dispatcher(new findSubjectsById()))
                .put("/{subjectId}", new Dispatcher(new BlockingHandler(new updateSubjects())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler teacher() {
        return Handlers.routing()
                .post("", new Dispatcher(new BlockingHandler(new addTeachers())))
                .get("", new Dispatcher(new findAllTeachers()))
                .get("/{teacherId}", new Dispatcher(new findTeachersById()))
                .put("/{teacherId}", new Dispatcher(new BlockingHandler(new updateTeacher())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

}
