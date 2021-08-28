{
    let courses, dates, examSession, reports, pageOrchestrator = new PageOrchestrator();

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

    function Courses(_alertContainer, _coursesTable) {
        this.alertContainer = _alertContainer;
        this.coursesTable = _coursesTable;
        this.coursesListContainer = _coursesTable.getElementsByTagName("tbody")[0];

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.coursesTable.style.visibility = "hidden";
        };

        this.show = (next) => {
            const self = this;
            makeCall("GET", "GetTaughtCourses", null, (req) => {
                if (req.readyState == XMLHttpRequest.DONE) {
                    const res = req.responseText;
                    const json = JSON.parse(req.responseText);
                    switch (req.status) {
                        case 200:
                            if (json.length == 0) {
                                self.alertContainer.textContent = "No courses yet!";
                                self.alertContainer.style.visibility = "visible";
                                return;
                            }
                            self.alertContainer.style.visibility = "hidden";
                            self.update(json);
                            if (next) next();
                            break;
                        case 400:
                            console.log(json.displayMessage);
                            break;
                        case 403:
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem("id");
                            window.sessionStorage.removeItem("role");
                            window.sessionStorage.removeItem("name");
                            window.sessionStorage.removeItem("surname");
                            break;
                        default:
                            self.alertContainer.textContent = json.displayMessage;
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
                //resultDetail.reset();
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
                //resultDetail.reset();
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

    function Dates(_alertContainer, _datesTable) {
        this.alertContainer = _alertContainer;
        this.datesTable = _datesTable;
        this.datesListContainer = _datesTable.getElementsByTagName("tbody")[0];

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.datesTable.style.visibility = "hidden";
        };

        this.show = (courseId) => {
            const self = this;
            makeCall("GET", "GetSessionDates?courseId=" + courseId, null, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        const res = req.responseText;
                        const json = JSON.parse(res);
                        switch (req.status) {
                            case 200:
                                if (json.length == 0) {
                                    self.alertContainer.textContent = "No exams yet!";
                                    self.alertContainer.style.visibility = "visible";
                                    self.datesTable.style.visibility = "hidden";
                                    return;
                                }
                                self.alertContainer.style.visibility = "hidden";
                                self.datesTable.style.visibility = "visible";
                                self.update(json);
                                break;
                            case 400:
                                console.log(json);
                                break;
                            case 403:
                                window.location.href = req.getResponseHeader("Location");
                                window.sessionStorage.removeItem("id");
                                window.sessionStorage.removeItem("role");
                                window.sessionStorage.removeItem("name");
                                window.sessionStorage.removeItem("surname");
                                break;
                            default:
                                self.alertContainer.textContent = json;
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
                //resultDetail.reset();
                examSession.reset();
                examSession.show(e.target.getAttribute("examSessionId"));
            }, false);
            td.appendChild(a);

            tr.appendChild(td);

            this.datesListContainer.appendChild(tr);
        };
    }

    function ExamSession(_alertContainer, _examsTable, _publishAnchor, _reportAnchor, _multipleAnchor, _multipleModalForm, _updateModalForm) {
        this.alertContainer = _alertContainer;
        this.examsTable = _examsTable;
        this.examsListContainer = _examsTable.getElementsByTagName("tbody")[0];
        this.publishAnchor = _publishAnchor;
        this.reportAnchor = _reportAnchor;
        this.multipleAnchor = _multipleAnchor;
        this.multipleModalForm = _multipleModalForm;
        this.updateModalForm = _updateModalForm;

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.examsTable.style.visibility = "hidden";
            this.multipleModalForm.style.visibility = "hidden";
            this.updateModalForm.style.visibility = "hidden";
        };

        this.show = (examSessionId) => {
            var self = this;
            makeCall("GET", "RegisteredStudents?examSessionId=" + examSessionId, null, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        const res = req.responseText;
                        switch (req.status) {
                            case 200:
                                const exams = JSON.parse(res);
                                self.updateModalForm.style.visibility = "hidden";
                                self.multipleModalForm.style.visibility = "hidden";
                                if (exams.length == 0) {
                                    self.alertContainer.textContent = "No registered students yet!";
                                    self.alertContainer.style.visibility = "visible";
                                    return;
                                }
                                self.publishAnchor.setAttribute("examSessionId", examSessionId);
                                self.reportAnchor.setAttribute("examSessionId", examSessionId);
                                self.multipleAnchor.setAttribute("examSessionId", examSessionId);
                                self.alertContainer.style.visibility = "hidden";
                                self.update(exams);
                                break;
                            case 400:
                                console.log(res);
                                break;
                            case 403:
                                window.location.href = req.getResponseHeader("Location");
                                window.sessionStorage.removeItem("id");
                                window.sessionStorage.removeItem("role");
                                window.sessionStorage.removeItem("name");
                                window.sessionStorage.removeItem("surname");
                                break;
                            default:
                                self.alertContainer.textContent = res;
                                break;
                        }
                    }
                }
            );
        };

        this.update = function (exams) {
            this.examsListContainer.innerHTML = "";
            courses.forEach((exam) => {
                this.appendRow(exam);
            });
        };

        this.appendRow = (exam) => {
            let tr = document.createElement("tr");
            let td_id;

            row = document.createElement("tr");

            studentid = document.createElement("td");
            studentid.textContent = registeredStudent.studentId;
            row.appendChild(studentid);

            surname = document.createElement("td");
            surname.textContent = registeredStudent.studentSurname;
            row.appendChild(surname);

            name = document.createElement("td");
            name.textContent = registeredStudent.studentName;
            row.appendChild(name);

            email = document.createElement("td");
            email.textContent = registeredStudent.studentEmail;
            row.appendChild(email);

            corsoDiLaurea = document.createElement("td");
            corsoDiLaurea.textContent = registeredStudent.corsoDiLaurea;
            row.appendChild(corsoDiLaurea);

            grade = document.createElement("td");
            grade.textContent = registeredStudent.grade;
            row.appendChild(grade);

            status = document.createElement("td");
            status.textContent = registeredStudent.status;
            row.appendChild(status);

            linkcell = document.createElement("td");
            anchor = document.createElement("a");
            linkcell.appendChild(anchor);
            linkText = document.createTextNode("Modify");

            anchor.appendChild(linkText);
            anchor.setAttribute("examid", registeredStudent.examId);
            anchor.addEventListener("click", (e) => {
                resultDetail.show(e.target.getAttribute("examid"));
            }, false);

            anchor.href = "#";
            row.appendChild(linkcell);

            self.studentcontainerbody.appendChild(row);

            this.examsListContainer.style.visibility = "visible";
        };

        this.registerEvents = () => {

        };
    }

    function PageOrchestrator() {
        this.start = () => {

        };

        this.refresh = (course) => {

        };
    }
}