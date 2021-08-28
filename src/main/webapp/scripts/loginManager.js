/**
 * IIFE for client-side login management
 */
(function () {
    const eventObject = {
        handleEvent: function (e) {
            const form = e.target.closest("form");
            if (form.checkValidity()) {
                makeCall("POST", "Login", form, this.callBack);
            } else {
                form.reportValidity();
            }
        },
        callBack: function (req) {
            console.log(req.readyState);
            if (req.readyState == XMLHttpRequest.DONE) {
                const res = req.responseText;
                switch (req.status) {
                    case 200:
                        const user = JSON.parse(res);
                        console.log(user.loginData.id);
                        console.log(user.loginData.role);
                        console.log(user.personalData.name);
                        console.log(user.personalData.surname);
                        sessionStorage.setItem('id', user.loginData.id);
                        sessionStorage.setItem("role", user.loginData.role);
                        sessionStorage.setItem("name", user.personalData.name);
                        sessionStorage.setItem("surname", user.personalData.surname);
                        if (user.loginData.role == "PROFESSOR") {
                            window.location.href = "homeProfessor.html";
                        } else if (user.loginData.role == "STUDENT") {
                            window.location.href = "homeStudent.html";
                        }
                        break;
                    case 400:
                        document.getElementById("errorBox").className = "displayed";
                        document.getElementById("errorMessage").textContent = res;
                        break;
                    case 418:
                        document.getElementById("errorBox").className = "displayed";
                        document.getElementById("errorMessage").textContent =
                            "The server is a teapot: ask its for a tea, not for a coffee";
                        break;
                    case 500:
                        document.getElementById("errorBox").className = "displayed";
                        document.getElementById("errorMessage").textContent = res;
                        break;
                    case 502:
                        document.getElementById("errorBox").className = "displayed";
                        document.getElementById("errorMessage").textContent = res;
                        break;
                    default:
                        document.getElementById("errorBox").className = "displayed";
                        document.getElementById("errorMessage").textContent = res;
                        break;
                }
            }
        }
    };
    document.getElementById("submit_login_input").addEventListener("click", eventObject, false);
})();