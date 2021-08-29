{
    let personalMessage, courses, dates, examSession, examDetail, report, multipleInsertions,
        pageOrchestrator = new PageOrchestrator();

    const optionsArray = ["", "missing", "rejected", "sent back", "18", "19", "20", "21", "22", "23", "24", "25", "26",
        "27", "28", "29", "30", "30L"];

    window.addEventListener("load", () => {
        if ((sessionStorage.getItem("role") == null) || (sessionStorage.getItem("id") == null) ||
            (sessionStorage.getItem("name") == null) || (sessionStorage.getItem("surname") == null)) {
            window.location.href = "login.html";
        } else {
            pageOrchestrator.start();
            pageOrchestrator.refresh();
        }
    }, false);

    function PersonalMessage(name, surname, messageContainer) {
        this.message = "Nice to see you again, " + name + " " + surname;

        this.show = () => {
            messageContainer.textContent = this.message;
        };
    }

    function Courses(options) {
        this.alertContainer = options['alert'];
        this.coursesContainer = options['coursesContainer'];
        this.coursesTable = options['coursesTable'];
        this.coursesList = options['coursesList'];

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.coursesContainer.style.visibility = "hidden";
        };

        this.show = (next) => {
            const self = this;
            makeCall("GET", "GetCoursesProfessor", null, (req) => {
                if (req.readyState == XMLHttpRequest.DONE) {
                    const res = req.responseText;
                    switch (req.status) {
                        case 200:
                            const courses = JSON.parse(req.responseText);
                            self.alertContainer.style.visibility = "hidden";
                            self.coursesContainer.style.visibility = "visible";
                            self.update(courses);
                            if (next) next();
                            break;
                        default:
                            self.alertContainer.textContent = res;
                            self.alertContainer.style.visibility = "visible";
                            self.coursesContainer.style.visibility = "hidden";
                            break;
                    }
                }
            });
        };

        this.update = (courses) => {
            this.coursesList.innerHTML = "";
            courses.forEach((course) => {
                this.appendRow(course);
            });
        };

        this.appendRow = (course) => {
            let tr = document.createElement("tr");

            let td_code = document.createElement("td");
            let a_code = document.createElement("a");
            a_code.textContent = course.code;
            a_code.setAttribute("courseId", course.id);
            a_code.href = "#";
            a_code.addEventListener("click", (e) => {
                //report.reset();
                examDetail.reset();
                examSession.reset();
                dates.show(e.target.getAttribute("courseId"));
            }, false);
            td_code.appendChild(a_code);

            let td_name = document.createElement("td");
            let a_name = document.createElement("a");
            a_name.textContent = course.name;
            a_name.setAttribute("courseId", course.id);
            a_name.href = "#";
            a_name.addEventListener("click", (e) => {
                report.reset();
                examDetail.reset();
                examSession.reset();
                dates.show(e.target.getAttribute("courseId"));
            }, false);
            td_name.appendChild(a_name);

            tr.appendChild(td_code);
            tr.appendChild(td_name);

            this.coursesList.appendChild(tr);
        };

        this.autoClick = (courseId) => {
            let anchorToClick = (courseId) ? document.querySelector("a[courseId='" + courseId + "']")
                : this.coursesList.querySelectorAll("a")[0];
            if (anchorToClick) anchorToClick.dispatchEvent(new Event("click"));
        };
    }

    function Dates(options) {
        this.alertContainer = options['alert'];
        this.datesContainer = options['datesContainer'];
        this.datesTable = options['datesTable'];
        this.datesList = options['datesList'];

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.datesContainer.style.visibility = "hidden";
        };

        this.show = (courseId) => {
            const self = this;
            makeCall("GET", "GetSessionDatesProfessor?courseId=" + courseId, null, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        const res = req.responseText;
                        switch (req.status) {
                            case 200:
                                const dates = JSON.parse(res);
                                self.alertContainer.style.visibility = "hidden";
                                self.datesContainer.style.visibility = "visible";
                                self.update(dates);
                                break;
                            case 403:
                                window.location.href = "login.html";
                                window.sessionStorage.removeItem("id");
                                window.sessionStorage.removeItem("role");
                                window.sessionStorage.removeItem("name");
                                window.sessionStorage.removeItem("surname");
                                break;
                            default:
                                self.alertContainer.textContent = res;
                                self.alertContainer.style.visibility = "visible";
                                self.datesContainer.style.visibility = "hidden";
                                break;
                        }
                    }
                }
            );
        };

        this.update = (dates) => {
            this.datesList.innerHTML = "";
            dates.forEach((date) => {
                this.appendRow(date);
            });
        };

        this.appendRow = (date) => {
            let tr = document.createElement("tr");

            let td = document.createElement("td");
            let a = document.createElement("a");
            a.textContent = date.date;
            a.setAttribute("examSessionId", date.id);
            a.href = "#";
            a.addEventListener("click", (e) => {
                report.reset();
                examDetail.reset();
                examSession.reset();
                examSession.show(e.target.getAttribute("examSessionId"));
            }, false);
            td.appendChild(a);

            tr.appendChild(td);

            this.datesList.appendChild(tr);
        };
    }

    function ExamSession(options) {
        this.alertContainer = options['alert'];
        this.examsContainer = options['examsContainer'];
        this.examsTable = options['examsTable'];
        this.examsList = options['examsList'];
        this.examSessionForm = options['examSessionForm'];

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.examsContainer.style.visibility = "hidden";
        };

        this.show = (examSessionId) => {
            let self = this;
            makeCall("GET", "GetSessionDetails?examSessionId=" + examSessionId, null, (req) => {
                if (req.readyState == XMLHttpRequest.DONE) {
                    const res = req.responseText;
                    switch (req.status) {
                        case 200:
                            const exams = JSON.parse(res);
                            self.alertContainer.style.visibility = "hidden";
                            self.examsContainer.style.visibility = "visible";
                            self.update(exams);
                            self.examSessionForm.querySelector("input[type='hidden']").value = examSessionId;
                            break;
                        case 403:
                            window.location.href = "login.html";
                            window.sessionStorage.removeItem("id");
                            window.sessionStorage.removeItem("role");
                            window.sessionStorage.removeItem("name");
                            window.sessionStorage.removeItem("surname");
                            break;
                        default:
                            self.alertContainer.textContent = res;
                            self.alertContainer.style.visibility = "visible";
                            self.examsContainer.style.visibility = "hidden";
                            break;
                    }
                }
            });
        };

        this.update = (exams) => {
            this.examsList.innerHTML = "";
            exams.forEach((exam) => {
                this.appendRow(exam);
            });
        };

        this.appendRow = (exam) => {
            let tr = document.createElement("tr");

            let td_mat = document.createElement("td");
            td_mat.textContent = exam.student.matriculation;
            tr.appendChild(td_mat);

            let td_name = document.createElement("td");
            td_name.textContent = exam.student.surname + ", " + exam.student.name;
            tr.appendChild(td_name);

            let td_email = document.createElement("td");
            td_email.textContent = exam.student.email;
            tr.appendChild(td_email);

            let td_bc = document.createElement("td");
            td_bc.textContent = exam.student.bachelorCourse;
            tr.appendChild(td_bc);

            let td_gr = document.createElement("td");
            if (!exam.result) {
                td_gr.textContent = "not defined yet";
            } else if (exam.result == "PASSED") {
                td_gr.textContent = exam.grade;
                if ((exam.grade == 30) && exam.laude) {
                    td_gr.textContent += " cum laude";
                }
            } else {
                td_gr.textContent = exam.result.toLowerCase();
            }
            tr.appendChild(td_gr);

            let td_st = document.createElement("td");
            td_st.textContent = exam.status.toLowerCase();
            tr.appendChild(td_st);

            let td_a = document.createElement("td");
            td_a.classList.add("transparent");
            if ((exam.status == "NOT_INSERTED_YET") || (exam.status == "INSERTED")) {
                let a = document.createElement("a");
                a.textContent = "Edit";
                a.classList.add("button");
                a.id = "edit";
                a.setAttribute("examId", exam.id);
                a.href = "#";
                a.addEventListener("click", (e) => {
                    examDetail.show(e.target.getAttribute("examId"));
                });
                td_a.appendChild(a);
            }
            tr.appendChild(td_a);

            this.examsList.appendChild(tr);
        };

        this.registerEvents = () => {
            this.examSessionForm.querySelector("input[type='button'].publish").addEventListener("click", (e) => {
                const self = this;
                const form = e.target.closest("form");
                const examSessionId = form.querySelector("input[type = 'hidden'].examSessionId").value;
                makeCall("POST", "Publish", form, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        const res = req.responseText;
                        switch (req.status) {
                            case 200:
                                self.show(examSessionId);
                                break;
                            case 403:
                                window.location.href = "login.html";
                                window.sessionStorage.removeItem("id");
                                window.sessionStorage.removeItem("role");
                                window.sessionStorage.removeItem("name");
                                window.sessionStorage.removeItem("surname");
                                break;
                            default:
                                self.alertContainer.textContent = res;
                                self.alertContainer.style.visibility = "visible";
                                break;
                        }
                    }
                });
            });

            this.examSessionForm.querySelector("input[type='button'].report").addEventListener("click", (e) => {
                const self = this;
                const form = e.target.closest("form");
                const examSessionId = form.querySelector("input[type = 'hidden'].examSessionId").value;
                makeCall("POST", "Report", form, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        const res = req.responseText;
                        switch (req.status) {
                            case 200:
                                self.show(examSessionId);
                                report.show(res);
                                break;
                            case 403:
                                window.location.href = "login.html";
                                window.sessionStorage.removeItem("id");
                                window.sessionStorage.removeItem("role");
                                window.sessionStorage.removeItem("name");
                                window.sessionStorage.removeItem("surname");
                                break;
                            default:
                                self.alertContainer.textContent = res;
                                self.alertContainer.style.visibility = "visible";
                                break;
                        }
                    }
                });
            });

            this.examSessionForm.querySelector("input[type='button'].multiple").addEventListener("click", (e) => {
                const form = e.target.closest("form");
                const examSessionId = form.querySelector("input[type = 'hidden'].examSessionId").value;
                multipleInsertions.show(examSessionId);
            });
        };
    }

    function ExamDetail(options) {
        this.alertContainer = options['alert'];
        this.selectedExamContainer = options['selectedExamContainer'];
        this.examDataContainer = options['examDataContainer'];
        this.updateForm = options['updateForm'];
        this.matriculationNode = options['matriculationNode'];
        this.nameNode = options['nameNode'];
        this.gradeNode = options['gradeNode'];
        this.statusNode = options['statusNode'];

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.selectedExamContainer.style.visibility = "hidden";
        };

        this.show = (examId) => {
            const self = this;
            makeCall("GET", "GetExamDetail?examId=" + examId, null, (req) => {
                if (req.readyState == XMLHttpRequest.DONE) {
                    const res = req.responseText;
                    switch (req.status) {
                        case 200:
                            const exam = JSON.parse(res);
                            self.update(exam);
                            self.alertContainer.style.visibility = "hidden";
                            self.selectedExamContainer.style.visibility = "visible";
                            break;
                        case 403:
                            window.location.href = "login.html";
                            window.sessionStorage.removeItem("id");
                            window.sessionStorage.removeItem("role");
                            window.sessionStorage.removeItem("name");
                            window.sessionStorage.removeItem("surname");
                            break;
                        default:
                            self.alertContainer.textContent = res;
                            self.alertContainer.style.visibility = "visible";
                            self.selectedExamContainer.style.visibility = "hidden";
                            break;
                    }
                }
            });
        };

        this.update = (exam) => {
            this.matriculationNode.textContent = "Matriculation: " + exam.student.matriculation;
            this.nameNode.textContent = "Student: " + exam.student.surname + " " + exam.student.name;
            this.statusNode.textContent = "Status: " + exam.status;
            if (exam.status == "NOT_INSERTED_YET") {
                this.gradeNode.textContent = "The grade hasn't been inserted yet";
            } else {
                if (exam.result == "PASSED") {
                    this.gradeNode.textContent = "Inserted grade: " + exam.grade;
                    if ((exam.grade == 30) && exam.laude) {
                        this.gradeNode.textContent += " cum laude";
                    }
                } else {
                    this.gradeNode.textContent = exam.result;
                }
            }
            this.updateForm.querySelector("input[type='hidden'].examId").value = exam.id;
            this.updateForm.querySelector("input[type='hidden'].examSessionId").value = exam.examSession.id;
        };

        this.registerEvents = () => {
            this.updateForm.querySelector("input[type='button'].update").addEventListener("click", (e) => {
                const self = this;
                const form = e.target.closest("form");
                const examSessionId = form.querySelector("input[type = 'hidden'].examSessionId").value;
                makeCall("POST", "Update", form, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        const res = req.responseText;
                        switch (req.status) {
                            case 200:
                                self.reset();
                                examSession.show(examSessionId);
                                break;
                            case 403:
                                window.location.href = "login.html";
                                window.sessionStorage.removeItem("id");
                                window.sessionStorage.removeItem("role");
                                window.sessionStorage.removeItem("name");
                                window.sessionStorage.removeItem("surname");
                                break;
                            default:
                                self.alertContainer.textContent = res;
                                self.alertContainer.style.visibility = "visible";
                                break;
                        }
                    }
                });
            });
        };
    }

    function Report(options) {
        this.alertContainer = options['alert'];
        this.reportContainer = options['reportContainer'];
        this.reportTable = options['reportTable'];
        this.reportedExamsList = options['reportedExamsList'];
        this.reportDataNode = options['reportDataNode'];

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.reportContainer.style.visibility = "hidden";
        };

        this.show = (reportId) => {
            const self = this;
            makeCall("GET", "GetReport?reportId=" + reportId, null, (req) => {
                if (req.readyState == XMLHttpRequest.DONE) {
                    const res = req.responseText;
                    switch (req.status) {
                        case 200:
                            const reportInfo = JSON.parse(res);
                            self.update(reportInfo);
                            self.alertContainer.style.visibility = "hidden";
                            self.selectedExamContainer.style.visibility = "visible";
                            break;
                        case 403:
                            window.location.href = "login.html";
                            window.sessionStorage.removeItem("id");
                            window.sessionStorage.removeItem("role");
                            window.sessionStorage.removeItem("name");
                            window.sessionStorage.removeItem("surname");
                            break;
                        default:
                            self.alertContainer.textContent = res;
                            self.alertContainer.style.visibility = "visible";
                            self.selectedExamContainer.style.visibility = "hidden";
                            break;
                    }
                }
            });
        };

        this.update = (reportInfo) => {
            this.reportedExamsList.innerHTML = "";
            reportInfo.exams.forEach((exam) => {
                this.appendRow(exam);
            });
            this.reportDataNode.textContent = "Report created on " + reportInfo.creation + " for " + reportInfo.course + " - " + reportInfo.date;
        };

        this.appendRow = (exam) => {
            let tr = document.createElement("tr");
            let td_mat = document.createElement("td");
            td_mat.textContent = exam.student.matriculation;
            let td_name = document.createElement("td");
            td_name.textContent = exam.student.surname + ", " + exam.student.name;
            let td_gr = document.createElement("td");
            if (exam.result == "PASSED") {
                td_gr.textContent = exam.grade;
                if ((exam.grade == 30) && exam.laude) {
                    td_gr.textContent += " cum laude";
                }
            } else {
                td_gr.textContent = exam.result;
            }
            tr.appendChild(td_mat);
            tr.appendChild(td_name);
            tr.appendChild(td_gr);
            this.reportedExamsList.appendChild(tr);
        };

        this.registerEvents = () => {
            this.reportContainer.querySelector("input[type='button'].close").addEventListener("click", (e) => {
                this.reportDataNode.textContent = "";
                this.reportedExamsList.innerHTML = "";
                this.reportContainer.style.visibility = "hidden";
            });
        };
    }

    function MultipleInsertions(options) {
        this.alertContainer = options['alert'];
        this.formContainer = options['formContainer'];
        this.form = options['form'];
        this.table = options['table'];
        this.list = options['list'];

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.formContainer.style.display = "none";
            this.list.innerHTML = "";
        };

        this.show = (examSessionId) => {
            const self = this;
            this.showCover();
            makeCall("GET", "GetSessionDetails?examSessionId=" + examSessionId + "&multiple=1", null, (req) => {
                if (req.readyState == XMLHttpRequest.DONE) {
                    const res = req.responseText;
                    switch (req.status) {
                        case 200:
                            const exams = JSON.parse(res);
                            this.form.querySelector("input[type = 'hidden']").value = examSessionId;
                            self.update(exams);
                            self.alertContainer.style.visibility = "hidden";
                            self.formContainer.style.display = "block";
                            break;
                        case 403:
                            window.location.href = "login.html";
                            window.sessionStorage.removeItem("id");
                            window.sessionStorage.removeItem("role");
                            window.sessionStorage.removeItem("name");
                            window.sessionStorage.removeItem("surname");
                            break;
                        default:
                            self.alertContainer.textContent = res;
                            self.alertContainer.style.visibility = "visible";
                            self.formContainer.style.display = "none";
                            break;
                    }
                }
            });
        };

        this.update = (exams) => {
            this.list.innerHTML = "";
            exams.forEach((exam) => {
                this.appendRow(exam);
            });
        };

        this.appendRow = (exam) => {
            let tr = document.createElement("tr");

            let td_mat = document.createElement("td");
            td_mat.textContent = exam.student.matriculation;
            tr.appendChild(td_mat);

            let td_name = document.createElement("td");
            td_name.textContent = exam.student.surname + ", " + exam.student.name;
            tr.appendChild(td_name);

            let td_gr = document.createElement("td");
            if (!exam.result || exam.status == "NOT_INSERTED_YET") {
                td_gr.textContent = "not defined yet";
            } else if (exam.result == "PASSED") {
                td_gr.textContent = exam.grade;
                if ((exam.grade == 30) && exam.laude) {
                    td_gr.textContent += " cum laude";
                }
            } else {
                td_gr.textContent = exam.result.toLowerCase();
            }
            tr.appendChild(td_gr);

            let td_st = document.createElement("td");
            td_st.textContent = exam.status.toLowerCase();
            tr.appendChild(td_st);

            let td_sel = document.createElement("td");
            let sel = document.createElement("select");
            sel.name = exam.id;
            for (let index in optionsArray) {
                let opt = document.createElement("option");
                opt.value = optionsArray[index];
                opt.innerHTML = optionsArray[index];
                sel.appendChild(opt);
            }
            td_sel.appendChild(sel);
            tr.appendChild(td_sel);

            this.list.appendChild(tr);
        };


        this.registerEvents = () => {
            this.formContainer.querySelector("input[type='button'].cancel").addEventListener("click", (e) => {
                this.hideCover();
                this.reset();
            });

            this.formContainer.querySelector("input[type='button'].update").addEventListener("click", (e) => {
                const self = this;
                const form = e.target.closest("form");
                const examSessionId = form.querySelector("input[type = 'hidden'].examSessionId").value;
                let grades = [];
                for (let tr of this.list.rows) {
                    const sel = tr.querySelector("select");
                    const examId = sel.name;
                    const grade = sel.options[sel.selectedIndex].value;
                    const element = {
                        "examId": examId,
                        "grade": grade
                    };
                    if (grade != "") {
                        grades.push(element);
                    }
                }
                const json = JSON.stringify(grades);
                makeJsonCall("POST", "Update?multiple=1", json, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        const res = req.responseText;
                        switch (req.status) {
                            case 200:
                                self.hideCover();
                                self.reset();
                                examSession.show(examSessionId);
                                break;
                            case 403:
                                window.location.href = "login.html";
                                window.sessionStorage.removeItem("id");
                                window.sessionStorage.removeItem("role");
                                window.sessionStorage.removeItem("name");
                                window.sessionStorage.removeItem("surname");
                                break;
                            default:
                                self.hideCover();
                                self.reset();
                                self.alertContainer.textContent = res;
                                self.alertContainer.style.visibility = "visible";
                                self.formContainer.style.display = "none";
                                break;
                        }
                    }
                });
            });
        };

        this.showCover = () => {
            let coverDiv = document.createElement('div');
            coverDiv.id = 'cover';
            document.body.style.overflowY = 'hidden';
            document.body.append(coverDiv);
        };

        this.hideCover = () => {
            document.getElementById('cover').remove();
            document.body.style.overflowY = '';
        };
    }

    function PageOrchestrator() {
        let alertContainer = document.getElementById("alert");

        this.start = () => {
            document.getElementById("logout").addEventListener("click", (e) => {
                sessionStorage.removeItem("id");
                sessionStorage.removeItem("role");
                sessionStorage.removeItem("name");
                sessionStorage.removeItem("surname");
                makeCall("GET", "Logout", null, (x) => {});
            });

            personalMessage = new PersonalMessage(
                sessionStorage.getItem("name"),
                sessionStorage.getItem("surname"),
                document.getElementById("personal-message")
            );
            personalMessage.show();

            courses = new Courses(
                {
                    alert: alertContainer,
                    coursesContainer: document.getElementById("courses-container"),
                    coursesTable: document.getElementById("courses-table"),
                    coursesList: document.getElementById("courses-list")
                }
            );

            dates = new Dates(
                {
                    alert: alertContainer,
                    datesContainer: document.getElementById("dates-container"),
                    datesTable: document.getElementById("dates-table"),
                    datesList: document.getElementById("dates-list")
                }
            );

            examSession = new ExamSession(
                {
                    alert: alertContainer,
                    examsContainer: document.getElementById("exams-container"),
                    examsTable: document.getElementById("exams-table"),
                    examsList: document.getElementById("exams-list"),
                    examSessionForm: document.getElementById("examSession-form")
                }
            );
            examSession.registerEvents();

            examDetail = new ExamDetail(
                {
                    alert: alertContainer,
                    selectedExamContainer: document.getElementById("selected-exam-container"),
                    examDataContainer: document.getElementById("exam-data"),
                    matriculationNode: document.getElementById("matriculation"),
                    nameNode: document.getElementById("name"),
                    gradeNode: document.getElementById("grade"),
                    statusNode: document.getElementById("status"),
                    updateForm: document.getElementById("update-grade-form")
                }
            );
            examDetail.registerEvents();

            report = new Report(
                {
                    alert: alertContainer,
                    reportContainer: document.getElementById("report-container"),
                    reportDataNode: document.getElementById("report-data"),
                    reportTable: document.getElementById("report-table"),
                    reportedExamsList: document.getElementById("reported-exams-list")
                }
            );
            report.registerEvents();

            multipleInsertions = new MultipleInsertions(
                {
                    alert: alertContainer,
                    formContainer: document.getElementById("multiple-form-container"),
                    form: document.getElementById("multiple-form"),
                    table: document.getElementById("exams-multiple-table"),
                    list: document.getElementById("exams-multiple-list")
                }
            );
            multipleInsertions.registerEvents();
        };

        this.refresh = (courseId) => {
            alertContainer.textContent = "";
            courses.reset();
            dates.reset();
            examSession.reset();
            examDetail.reset();
            report.reset();
            multipleInsertions.reset();
            courses.show(() => {
                courses.autoClick(courseId);
            });
        };
    }
}
;