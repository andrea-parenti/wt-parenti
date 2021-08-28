{
    let personalMessage, courses, dates, examSession, examDetail, report, multipleModalForm,
        pageOrchestrator = new PageOrchestrator();

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

    function Courses(_alertContainer, _coursesContainer) {
        this.alertContainer = _alertContainer;
        this.coursesContainer = _coursesContainer;
        this.coursesTable = this.coursesContainer.getElementById("courses-table");
        this.coursesListContainer = this.coursesTable.getElementsByTagName("tbody")[0];

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
            this.coursesListContainer.innerHTML = "";
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
                reports.reset();
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
                reports.reset();
                examDetail.reset();
                examSession.reset();
                dates.show(e.target.getAttribute("courseId"));
            }, false);
            td_name.appendChild(a_name);

            tr.appendChild(td_code);
            tr.appendChild(td_name);

            this.coursesListContainer.appendChild(tr);
        };

        this.autoClick = (courseId) => {
            let anchorToClick = (courseId) ? document.querySelector("a[courseId='" + courseId + "']")
                : this.coursesListContainer.querySelectorAll("a")[0];
            if (anchorToClick) anchorToClick.dispatchEvent(new Event("click"));
        };
    }

    function Dates(_alertContainer, _datesContainer) {
        this.alertContainer = _alertContainer;
        this.datesContainer = _datesContainer;
        this.datesTable = this.datesContainer.getElementById("dates-table");
        this.datesListContainer = this.datesTable.getElementsByTagName("tbody")[0];

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
            this.datesListContainer.innerHTML = "";
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
                reports.reset();
                examDetail.reset();
                examSession.reset();
                examSession.show(e.target.getAttribute("examSessionId"));
            }, false);
            td.appendChild(a);

            tr.appendChild(td);

            this.datesListContainer.appendChild(tr);
        };
    }

    function ExamSession(_alertContainer, _sessionDataContainer, _multipleModalForm) {
        this.alertContainer = _alertContainer;
        this.sessionDataContainer = _sessionDataContainer;
        this.examsTable = this.sessionDataContainer.getElementById("session-details");
        this.examsListContainer = this.examsTable.getElementsByTagName("tbody")[0];
        this.buttonsContainer = this.sessionDataContainer.getElementById("buttons-container");
        this.publishAnchor = this.buttonsContainer.getElementById("publish");
        this.reportAnchor = this.buttonsContainer.getElementById("report");
        this.multipleAnchor = this.buttonsContainer.getElementById("multiple");
        this.multipleModalForm = _multipleModalForm;

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.sessionDataContainer.style.visibility = "hidden";
            this.multipleModalForm.style.visibility = "hidden";
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
                            self.sessionDataContainer.style.visibility = "visible";
                            self.multipleModalForm.style.visibility = "hidden";
                            self.update(exams);
                            self.publishAnchor.setAttribute("examSessionId", examSessionId);
                            self.reportAnchor.setAttribute("examSessionId", examSessionId);
                            self.multipleAnchor.setAttribute("examSessionId", examSessionId)
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
                            self.sessionDataContainer.style.visibility = "hidden";
                            self.multipleModalForm.style.visibility = "hidden";
                            break;
                    }
                }
            });
        };

        this.update = (exams) => {
            this.examsListContainer.innerHTML = "";
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
            if ((exam.status == "NOT_INSERTED_YET") || (exam.status == "INSERTED")) {
                let a = document.createElement("a");
                a.classList.add("button", "edit");
                a.setAttribute("examId", exam.id);
                a.href = "#";
                a.addEventListener("click", (e) => {
                    examDetail.show(e.target.getAttribute("examId"));
                });
                td_a.appendChild(a);
            }
            tr.appendChild(td_a);

            this.examsListContainer.appendChild(tr);
        };

        this.refresh = (examSessionId) => {
            this.reset();
            this.show(examSessionId);
        }

        this.registerEvents = () => {
            const self = this;

            this.publishAnchor.addEventListener("click", (e) => {
                makeCall("GET", "Publish?examSessionId=" + e.target.getAttribute("examSessionId"), null, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        switch (req.status) {
                            case 200:
                                self.refresh(e.target.getAttribute("examSessionId"))
                                break;
                            case 403:
                                window.location.href = "login.html";
                                window.sessionStorage.removeItem("id");
                                window.sessionStorage.removeItem("role");
                                window.sessionStorage.removeItem("name");
                                window.sessionStorage.removeItem("surname");
                                break;
                        }
                    }
                });
            });

            this.reportAnchor.addEventListener("click", (e) => {
                makeCall("GET", "Report?examSessionId=" + e.target.getAttribute("examSessionId"), null, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        switch (req.status) {
                            case 200:
                                self.refresh(e.target.getAttribute("examSessionId"))
                                break;
                            case 403:
                                window.location.href = "login.html";
                                window.sessionStorage.removeItem("id");
                                window.sessionStorage.removeItem("role");
                                window.sessionStorage.removeItem("name");
                                window.sessionStorage.removeItem("surname");
                                break;
                        }
                    }
                });
            });

            this.multipleAnchor.addEventListener("click", (e) => {
                self.multipleModalForm.show(e.target.getAttribute("examSessionId"), self.refresh);
            });
        };
    }

    function ExamDetail(_alertContainer, _updateGradeContainer) {
        this.alertContainer = _alertContainer;
        this.updateGradeContainer = _updateGradeContainer;
        this.examDataContainer = this.updateGradeContainer.getElementById("exam-data-container");
        this.updateGradeForm = this.updateGradeContainer.getElementById("update-grade-form");

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.updateGradeContainer.style.visibility = "hidden";
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
                            self.updateGradeContainer.style.visibility = "visible";
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
                            self.updateGradeContainer.style.visibility = "hidden";
                            break;
                    }
                }
            });
        };

        this.update = (exam) => {
            this.examDataContainer.innerHTML = "";
            let student_text = "Matriculation: " + exam.student.matriculation + "\n" +
                "Student: " + exam.student.surname + " " + exam.student.name + "\n" +
                "Email: " + exam.student.email + "\n" +
                "Bachelor course: " + exam.student.bachelorCourse + "\n";
            let exam_text;
            if (exam.status == "NOT_INSERTED_YET") {
                exam_text = "The grade hasn't been inserted yet.\n";
            } else {
                if (exam.result == "PASSED") {
                    exam_text = "Inserted grade: " + exam.grade;
                    if ((exam.grade == 30) && exam.laude) {
                        exam_text += " cum laude";
                    }
                    exam_text += "\n";
                } else {
                    exam_text = exam.result.toLowerCase() + "\n";
                }
                exam_text += "Course: " + exam.examSession.course.code + " - " + exam.examSession.course.name + "\n" +
                    "Taken on: " + exam.examSession.date + "\n";
                if (exam.status == "REPORTED") {
                    exam_text += "Reported on: " + exam.report.creation + "\n";
                }
            }
            let examData = document.createTextNode(student_text + exam_text);
            this.examDataContainer.appendChild(examData);
        };

        this.registerEvents = (examSessionObj, examSessionId) => {
            const self = this;

            let onClick = (e) => {
                self.reset();
                examSessionObj.refresh(examSessionId);
            };

            this.updateGradeForm.querySelector("input[type='button']").removeEventListener('click', onClick);
            this.updateGradeForm.querySelector("input[type='button']").addEventListener('click', onClick);
        };
    }

    function Report(_alertContainer, _reportContainer) {
        this.alertContainer = _alertContainer;
        this.reportContainer = _reportContainer;
        this.reportTable = this.reportContainer.getElementById("report-table");
        this.reportedList = this.reportTable.getElementsByTagName("tbody")[0];
        this.closeButton = this.reportContainer.getElementById("close-report");
        this.reportData = this.reportContainer.getElementById("report-data");

        this.reset = () => {
            this.reportData.innerHTML = "";
            this.reportedList.innerHTML = "";
            this.reportContainer.style.visibility = "hidden";
        };

        this.show = (reportId) => {
            const self = this;
            makeCall("GET", "GetReport?reportId=" + reportId, null, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        const res = req.responseText;
                        switch (req.status) {
                            case 200:
                                const report = JSON.parse(req.responseText);
                                self.reportContainer.style.visibility = "visible";
                                self.alertContainer.style.visibility = "hidden";
                                let reportText = "Reported in: " + report.creation;
                                let reportInfo = document.createTextNode(reportText);
                                this.reportData.appendChild(reportInfo);
                                self.update(report.exams);
                                break;
                            default:
                                self.alertContainer.textContent = res;
                                self.reportContainer.style.visibility = "hidden";
                                self.alertContainer.style.visibility = "visible";
                                break;
                        }
                    }
                }
            );
        };

        this.update = (exams) => {
            this.reportedList.innerHTML = "";
            exams.forEach((exam) => {
                this.appendRow(exam);
            });
        };

        this.appendRow = (exam) => {
            let tr = document.createElement("tr");
            let td_mat = document.createElement("td");
            td_mat.textContent = exam.student.matriculation;
            tr.appendChild(td_mat);
            let td_gr = document.createElement("td");
            if (exam.result == "PASSED") {
                td_gr.textContent = exam.grade;
                if ((exam.grade == 30) && exam.laude) {
                    td_gr.textContent += " cum laude";
                }
            } else {
                td_gr.textContent = exam.result.toLowerCase();
            }
            tr.appendChild(td_gr);
            this.reportedList.appendChild(tr);
        };

        this.registerEvents = () => {
            const self = this;

            this.closeButton.addEventListener("click", (e) => {
                self.reset();
            });
        };
    }

    function MultipleModalForm(_alertContainer, _formContainer) {
        this.alertContainer = _alertContainer;
        this.formContainer = _formContainer;
        this.form = this.formContainer.getElementById("multiple-form");
        this.examTable = this.form.getElementById("multiple-table");
        this.examList = this.examTable.getElementsByTagName("tbody")[0];

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.formContainer.style.visibility = "hidden";
        };

        this.show = (examSessionId, callBack) => {
            const self = this;
            makeCall("GET", "GetSessionDetails?examSessionId=" + examSessionId + "&multiple=1", null, (req) => {
                if (req.readyState == XMLHttpRequest.DONE) {
                    const res = req.responseText;
                    switch (req.status) {
                        case 200:
                            const exams = JSON.parse(res);
                            this.form.querySelector("input[type = 'hidden']").value = examSessionId;
                            self.update(exams);
                            self.alertContainer.style.visibility = "hidden";
                            self.formContainer.style.visibility = "visible";
                            callBack(examSessionId);
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
                            self.formContainer.style.visibility = "hidden";
                            break;
                    }
                }
            });
        };

        this.update = (exams) => {
            this.examList.innerHTML = "";
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
            let a = document.createElement("a");
            a.setAttribute("examId", exam.id);
            let sel = document.createElement("select");
            let op01 = document.createElement("option");
            op01.text = "missing";
            op01.value = "missing";
            sel.appendChild(op01);
            let op02 = document.createElement("option");
            op02.text = "rejected";
            op02.value = "rejected";
            sel.appendChild(op02);
            let op03 = document.createElement("option");
            op03.text = "sent back";
            op03.value = "sent back";
            sel.appendChild(op03);
            let op04 = document.createElement("option");
            op04.text = "18";
            op04.value = "18";
            sel.appendChild(op04);
            let op05 = document.createElement("option");
            op05.text = "19";
            op05.value = "19";
            sel.appendChild(op05);
            let op06 = document.createElement("option");
            op06.text = "20";
            op06.value = "20";
            sel.appendChild(op06);
            let op07 = document.createElement("option");
            op07.text = "21";
            op07.value = "21";
            sel.appendChild(op07);
            let op08 = document.createElement("option");
            op08.text = "22";
            op08.value = "22";
            sel.appendChild(op08);
            let op09 = document.createElement("option");
            op09.text = "23";
            op09.value = "23";
            sel.appendChild(op09);
            let op10 = document.createElement("option");
            op10.text = "24";
            op10.value = "24";
            sel.appendChild(op10);
            let op11 = document.createElement("option");
            op11.text = "25";
            op11.value = "25";
            sel.appendChild(op11);
            let op12 = document.createElement("option");
            op12.text = "26";
            op12.value = "26";
            sel.appendChild(op12);
            let op13 = document.createElement("option");
            op13.text = "27";
            op13.value = "27";
            sel.appendChild(op13);
            let op14 = document.createElement("option");
            op14.text = "28";
            op14.value = "28";
            sel.appendChild(op14);
            let op15 = document.createElement("option");
            op15.text = "29";
            op15.value = "29";
            sel.appendChild(op15);
            let op16 = document.createElement("option");
            op16.text = "30";
            op16.value = "30";
            sel.appendChild(op16);
            let op17 = document.createElement("option");
            op17.text = "30L";
            op17.value = "30L";
            sel.appendChild(op17);
            a.appendChild(sel);
            td_gr.appendChild(a);
            tr.appendChild(td_gr);

            this.examList.appendChild(tr);
        };

        this.registerEvents = (examSessionObj) => {
            const self = this;
            this.form.querySelector("input[type='button']").addEventListener("click", (e) => {
                const examSessionId = self.form.querySelector("input[type = 'hidden']").value;
                let insertions = [];
                for (let tr of self.examList.rows) {
                    let a = tr.getElementsByTagName("a")[0];
                    let sel = a.getElementsByTagName("select")[0];
                    let insertion = {
                        key: a.getAttribute("examId"),
                        value: sel.options[sel.selectedIndex].value
                    }
                    insertions.push(insertion);
                }
                let json = JSON.stringify(insertions);
                makeJsonCall("POST", "UpdateGrade?examSessionId=" + examSessionId, json, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        const res = req.responseText;
                        switch (req.status) {
                            case 200:
                                examSessionObj.update(res);
                                self.alertContainer.style.visibility = "hidden";
                                self.formContainer.style.visibility = "hidden";
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
                                self.formContainer.style.visibility = "hidden";
                                break;
                        }
                    }
                })
            });
        };
    }

    function PageOrchestrator() {
        let alertContainer = document.getElementById("alert");

        this.start = () => {
            personalMessage = new PersonalMessage(
                sessionStorage.getItem("name"),
                sessionStorage.getItem("surname"),
                document.getElementById("personal-message")
            )
            personalMessage.show();

            courses = new Courses(
                alertContainer,
                document.getElementById("courses-container")
            );

            dates = new Dates(
                alertContainer,
                document.getElementById("dates-container")
            );

            examSession = new ExamSession(
                alertContainer,
                document.getElementById("exam-session-container"),
                document.getElementById("multiple-form-container")
            );

            examDetail = new ExamDetail(
                alertContainer,
                document.getElementById("selected-exam-container")
            );

            report = new Report(
                alertContainer,
                document.getElementById("report-container")
            );

            multipleModalForm = new MultipleModalForm(
                alertContainer,
                document.getElementById("multiple-form-container")
            );
        };

        this.refresh = (courseId) => {
            alertContainer.textContent = " ";
            courses.reset();
            dates.reset();
            courses.show(() => {
                courses.autoClick(courseId);
            });
            examSession.reset();
            examDetail.reset();
            report.reset();
            multipleModalForm.reset();
        };
    }
}
;