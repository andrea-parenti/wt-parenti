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
                const res = req.response;
                switch (req.status) {
                    case 200:
                        let user = JSON.parse(res);
                        console.log(user);
                        document.getElementById("errorBox").className = "masked";
                        document.getElementById("errorMessage").textContent = "";
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
    }
    document.getElementById("submit_login_input").addEventListener("click", eventObject, false);
})();