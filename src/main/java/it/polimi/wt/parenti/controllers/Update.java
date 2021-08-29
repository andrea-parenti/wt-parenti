package it.polimi.wt.parenti.controllers;

import com.google.gson.reflect.TypeToken;
import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.dao.ExamDAO;
import it.polimi.wt.parenti.dao.ProfessorDAO;
import it.polimi.wt.parenti.utils.Controller;
import it.polimi.wt.parenti.utils.Utility;
import it.polimi.wt.parenti.utils.enumerations.ExamResult;
import it.polimi.wt.parenti.utils.enumerations.ExamStatus;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;
import java.util.List;

@MultipartConfig
public class Update extends Controller {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final List<String> options = Utility.initializeOptions();

    public Update() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        var multiple = StringEscapeUtils.escapeJava(request.getParameter("multiple"));
        if ((multiple != null) && !(multiple.isBlank()) && (multiple.equals("1"))) {
            try {
                var sb = new StringBuilder();
                var br = request.getReader();
                var str = "";
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                System.out.println(sb.toString());
                List<Insertion> updates = Utility.getJsonParser().fromJson(sb.toString(), new TypeToken<List<Insertion>>() {
                }.getType());

                var p = (Professor) request.getSession().getAttribute("professor");
                var pDao = new ProfessorDAO(connection);
                var eDao = new ExamDAO(connection);
                try {
                    for (var update : updates) {
                        var examId = Integer.parseInt(update.examId);
                        var checkedExam = pDao.checkExam(p.getId(), examId);
                        if (checkedExam.isEmpty()) {
                            writeHttpResponse(response, HttpServletResponse.SC_FORBIDDEN, "text/plain", "Forbidden query on course!");
                            return;
                        }
                        var exam = checkedExam.get();
                        if (exam.getStatus() != ExamStatus.NOT_INSERTED_YET && exam.getStatus() != ExamStatus.INSERTED) {
                            writeHttpResponse(response, HttpServletResponse.SC_FORBIDDEN, "text/plain", "Forbidden operation!");
                            return;
                        }

                        Integer grade = null;
                        Boolean laude = null;
                        var result = ExamResult.fromString(update.grade);
                        if (result == null || result == ExamResult.PASSED) {
                            if (update.grade.equals("30L")) {
                                grade = 30;
                                laude = true;
                                result = ExamResult.PASSED;
                            } else {
                                grade = Integer.parseInt(update.grade);
                                if (grade < 18 || grade > 30) {
                                    writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Invalid \"grade\" parameter!");
                                    return;
                                }
                                result = ExamResult.PASSED;
                            }
                        }
                        eDao.updateGrade(examId, result, grade, laude);
                    }
                    writeHttpResponse(response, HttpServletResponse.SC_OK, "text/plain", null);
                    return;
                } catch (NumberFormatException e) {
                    writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Invalid \"grade\" parameter's format!");
                    e.printStackTrace();
                } catch (SQLException e) {
                    writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Error on database query or connection!");
                    e.printStackTrace();
                }

            } catch (IOException e) {
                writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "IO error");
                e.printStackTrace();
            }
        } else {
            var selectedExam = StringEscapeUtils.escapeJava(request.getParameter("examId"));
            if ((selectedExam == null) || selectedExam.isBlank()) {
                writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Missing necessary \"examId\" parameter!");
                return;
            }

            var gradeParam = StringEscapeUtils.escapeJava(request.getParameter("grade"));
            if (gradeParam == null || !options.contains(gradeParam)) {
                writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Missing necessary \"grade\" parameter!");
                return;
            }

            var p = (Professor) request.getSession().getAttribute("professor");
            var pDao = new ProfessorDAO(connection);
            var eDao = new ExamDAO(connection);
            try {
                var examId = Integer.parseInt(selectedExam);
                var checkedExam = pDao.checkExam(p.getId(), examId);
                if (checkedExam.isEmpty()) {
                    writeHttpResponse(response, HttpServletResponse.SC_FORBIDDEN, "text/plain", "Forbidden query on course!");
                    return;
                }
                var exam = checkedExam.get();
                if (exam.getStatus() != ExamStatus.NOT_INSERTED_YET && exam.getStatus() != ExamStatus.INSERTED) {
                    writeHttpResponse(response, HttpServletResponse.SC_FORBIDDEN, "text/plain", "Forbidden operation!");
                    return;
                }

                Integer grade = null;
                Boolean laude = null;
                var result = ExamResult.fromString(gradeParam);
                if (result == null || result == ExamResult.PASSED) {
                    if (gradeParam.equals("30L")) {
                        grade = 30;
                        laude = true;
                        result = ExamResult.PASSED;
                    } else {
                        grade = Integer.parseInt(gradeParam);
                        if (grade < 18 || grade > 30) {
                            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Invalid \"grade\" parameter!");
                            return;
                        }
                        result = ExamResult.PASSED;
                    }
                }
                eDao.updateGrade(examId, result, grade, laude);
                writeHttpResponse(response, HttpServletResponse.SC_OK, "text/plain", null);
                return;
            } catch (NumberFormatException e) {
                writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Invalid \"grade\" parameter's format!");
                e.printStackTrace();
            } catch (SQLException e) {
                writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Error on database query or connection!");
                e.printStackTrace();
            }
        }
    }

    private class Insertion {
        private final String examId;
        private final String grade;

        private Insertion(String examId, String grade) {
            this.examId = examId;
            this.grade = grade;
        }
    }
}
