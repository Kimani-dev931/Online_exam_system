package org.example.rest;

import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import org.example.handlers.authentication.ChangePassword;
import org.example.handlers.classes.AddClass;
import org.example.handlers.classes.FindAllClasses;
import org.example.handlers.classes.FindClassById;
import org.example.handlers.classes.UpdateClass;
import org.example.handlers.exam.AddExam;
import org.example.handlers.exam.FindAllExams;
import org.example.handlers.exam.FindExamsById;
import org.example.handlers.authentication.Refresh;
import org.example.handlers.authentication.LoginStudent;
import org.example.handlers.authentication.LoginTeacher;
import org.example.handlers.options.AddOptions;
import org.example.handlers.options.FindAllOptions;
import org.example.handlers.options.FindOptionsById;
import org.example.handlers.options.UpdateOptions;
import org.example.handlers.questions.AddQuestions;
import org.example.handlers.questions.FindAllQuestions;
import org.example.handlers.questions.FindQuestionsById;
import org.example.handlers.questions.UpdateQuestions;
import org.example.handlers.reports.GenerateStudentReport;
import org.example.handlers.reports.Exam_Set_By_Teacher;
import org.example.handlers.reports.Fetch_Exams_Results_For_Student;
import org.example.handlers.reports.Top_5_Student_Scores;
import org.example.handlers.exam.UpdateExam;
import org.example.handlers.responses.AddResponses;
import org.example.handlers.responses.FindAllResponses;
import org.example.handlers.responses.FindResponsesById;
import org.example.handlers.responses.updateResponses;
import org.example.handlers.student.AddStudent;
import org.example.handlers.student.FindAllSudents;
import org.example.handlers.student.FindStudentById;
import org.example.handlers.student.UpdateStudent;
import org.example.handlers.subjects.AddSubjects;
import org.example.handlers.subjects.FindAllSubjects;
import org.example.handlers.subjects.FindSubjectsById;
import org.example.handlers.subjects.UpdateSubjects;
import org.example.handlers.teacher.AddTeachers;
import org.example.handlers.teacher.FindAllTeachers;
import org.example.handlers.teacher.FindTeachersById;
import org.example.handlers.teacher.UpdateTeacher;
import org.example.rest.base.Dispatcher;
import org.example.rest.base.FallBack;
import org.example.rest.base.InvalidMethod;

public class Routes {

    public static RoutingHandler exam() {
        return Handlers.routing()
                .put("/teacher/login", new Dispatcher(new LoginTeacher()))
                .put("/student/login", new Dispatcher(new LoginStudent()))
                .post("", new Dispatcher(new BlockingHandler(new AddExam())))
                .get("", new Dispatcher(new FindAllExams()))
                .get("/{examId}", new Dispatcher(new FindExamsById()))
                .put("/{examId}", new Dispatcher(new BlockingHandler(new UpdateExam())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler reports() {
        return Handlers.routing()
                .put("/teacher/login", new Dispatcher(new LoginTeacher()))
                .get("/exams-set-teacher/{teacherId}", new Dispatcher(new Exam_Set_By_Teacher()))
                .get("/fetch-exam-results-for-student/{studentId}/{examId}", new Dispatcher(new Fetch_Exams_Results_For_Student()))
                .get("/top-5-student-scores/{examId}", new Dispatcher(new Top_5_Student_Scores()))
                .get("/student-report", new Dispatcher(new GenerateStudentReport()))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));

    }

    public static RoutingHandler classes() {
        return Handlers.routing()
                .put("/teacher/login", new Dispatcher(new LoginTeacher()))
                .post("", new Dispatcher(new BlockingHandler(new AddClass())))
                .get("", new Dispatcher(new FindAllClasses()))
                .get("/{classId}", new Dispatcher(new FindClassById()))
                .put("/{classId}", new Dispatcher(new BlockingHandler(new UpdateClass())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler options() {
        return Handlers.routing()
                .put("/student/login", new Dispatcher(new LoginStudent()))
                .put("/teacher/login", new Dispatcher(new LoginTeacher()))
                .post("", new Dispatcher(new BlockingHandler(new AddOptions())))
                .get("", new Dispatcher(new FindAllOptions()))
                .get("/{optionId}", new Dispatcher(new FindOptionsById()))
                .put("/{optionId}", new Dispatcher(new BlockingHandler(new UpdateOptions())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler questions() {
        return Handlers.routing()
                .put("/student/login", new Dispatcher(new LoginStudent()))
                .put("/teacher/login", new Dispatcher(new LoginTeacher()))
                .post("", new Dispatcher(new BlockingHandler(new AddQuestions())))
                .get("", new Dispatcher(new FindAllQuestions()))
                .get("/{questionsId}", new Dispatcher(new FindQuestionsById()))
                .put("/{questionsId}", new Dispatcher(new BlockingHandler(new UpdateQuestions())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler responses() {
        return Handlers.routing()
                .put("/student/login", new Dispatcher(new LoginStudent()))
                .put("/teacher/login", new Dispatcher(new LoginTeacher()))
                .post("", new Dispatcher(new BlockingHandler(new AddResponses())))
                .get("", new Dispatcher(new FindAllResponses()))
                .get("/{responseId}", new Dispatcher(new FindResponsesById()))
                .put("/{responseId}", new Dispatcher(new BlockingHandler(new updateResponses())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler student() {
        return Handlers.routing()
                .put("/teacher/login", new Dispatcher(new LoginTeacher()))
                .put("/student/login", new Dispatcher(new LoginStudent()))
                .post("", new Dispatcher(new BlockingHandler(new AddStudent())))
                .get("", new Dispatcher(new FindAllSudents()))
                .get("/{studentId}", new Dispatcher(new FindStudentById()))
                .put("/{studentId}", new Dispatcher(new BlockingHandler(new UpdateStudent())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler subjects() {
        return Handlers.routing()
                .put("/teacher/login", new Dispatcher(new LoginTeacher()))
                .post("", new Dispatcher(new BlockingHandler(new AddSubjects())))
                .get("", new Dispatcher(new FindAllSubjects()))
                .get("/{subjectId}", new Dispatcher(new FindSubjectsById()))
                .put("/{subjectId}", new Dispatcher(new BlockingHandler(new UpdateSubjects())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler teacher() {
        return Handlers.routing()
                .put("/teacher/login", new Dispatcher(new LoginTeacher()))
                .post("", new Dispatcher(new BlockingHandler(new AddTeachers())))
                .get("", new Dispatcher(new FindAllTeachers()))
                .get("/{teacherId}", new Dispatcher(new FindTeachersById()))
                .put("/{teacherId}", new Dispatcher(new BlockingHandler(new UpdateTeacher())))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler login() {
        return Handlers.routing()
                .put("/teacher", new Dispatcher(new LoginTeacher()))
                .put("/student", new Dispatcher(new LoginStudent()))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler changepassword() {
        return Handlers.routing()
                .put("", new Dispatcher(new ChangePassword()))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler refresh() {
        return Handlers.routing()
                .put("", new Dispatcher(new Refresh()))
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

}
