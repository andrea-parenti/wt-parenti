{
    let personalMessage, courses, dates, examDetail, pageOrchestrator = new PageOrchestrator();

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
            makeCall("GET", "GetCoursesStudent", null, (req) => {
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
                examDetail.reset();
                dates.show(e.target.getAttribute("courseId"));
            }, false);
            td_code.appendChild(a_code);

            let td_name = document.createElement("td");
            let a_name = document.createElement("a");
            a_name.textContent = course.name;
            a_name.setAttribute("courseId", course.id);
            a_name.href = "#";
            a_name.addEventListener("click", (e) => {
                examDetail.reset();
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
            makeCall("GET", "GetSessionDatesStudent?courseId=" + courseId, null, (req) => {
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
                examDetail.reset();
                examDetail.show(e.target.getAttribute("examSessionId"))
            }, false);
            td.appendChild(a);

            tr.appendChild(td);

            this.datesList.appendChild(tr);
        };
    }

    function ExamDetail(options) {
        this.alertContainer = options['alert'];
        this.selectedExamContainer = options['selectedExamContainer'];
        this.examDataContainer = options['examDataContainer'];
        this.refuseForm = options['refuseForm'];
        this.gradeNode = options['gradeNode'];
        this.courseNode = options['courseNode'];
        this.professorNode = options['professorNode'];
        this.dateNode = options['dateNode'];

        this.reset = () => {
            this.alertContainer.style.visibility = "hidden";
            this.selectedExamContainer.style.visibility = "hidden";
        };

        this.show = (examSessionId) => {
            const self = this;
            makeCall("GET", "GetExamResult?examSessionId=" + examSessionId, null, (req) => {
                if (req.readyState == XMLHttpRequest.DONE) {
                    const res = req.responseText;
                    switch (req.status) {
                        case 200:
                            const exam = JSON.parse(res);
                            console.log(exam);
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
            this.courseNode.textContent = exam.examSession.course.name + " - " + exam.examSession.course.code;
            this.professorNode.textContent = exam.examSession.course.professor.surname + ", " + exam.examSession.course.professor.name;
            this.dateNode.textContent = exam.examSession.date;
            this.refuseForm.querySelector("input[type='hidden'].examId").value = exam.id;
        };

        this.registerEvents = () => {
            this.refuseForm.querySelector("input[type='button'].refuse").addEventListener("click", (e) => {
                const self = this;
                const form = e.target.closest("form");
                makeCall("POST", "Refuse", form, (req) => {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        const res = req.responseText;
                        switch (req.status) {
                            case 200:
                                self.reset();
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

            examDetail = new ExamDetail(
                {
                    alert: alertContainer,
                    selectedExamContainer: document.getElementById("selected-exam-container"),
                    examDataContainer: document.getElementById("exam-data"),
                    gradeNode: document.getElementById("grade"),
                    courseNode: document.getElementById("course"),
                    professorNode: document.getElementById("professor"),
                    dateNode: document.getElementById("date"),
                    refuseForm: document.getElementById("refuse-grade-form")
                }
            );
            examDetail.registerEvents();
        };

        this.refresh = (courseId) => {
            alertContainer.textContent = "";
            courses.reset();
            dates.reset();
            examDetail.reset();
            courses.show(() => {
                courses.autoClick(courseId);
            });
        };
    }
}
;