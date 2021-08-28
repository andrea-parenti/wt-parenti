this.publishForm.querySelector("input[type='button']").addEventListener('click', (e) => {
    const self = this;
    reports.reset();
    const examSessionId = this.publishForm.querySelector("input[type='hidden']").value;
    makeCall("POST", "Publish?examSessionId=" + examSessionId, self.publishForm, (req) => {
        if (req.readyState == XMLHttpRequest.DONE) {
            const res = req.responseText;
            switch (req.status) {
                case 200:
                    self.show(res);
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
    });
});

this.reportForm.querySelector("input[type='button']").addEventListener('click', (e) => {
    const self = this;
    reports.reset();
    const examSessionId = this.reportForm.querySelector("input[type='hidden']").value;
    makeCall("POST", "Publish?examSessionId=" + examSessionId, self.reportForm, (req) => {
        if (req.readyState == XMLHttpRequest.DONE) {
            const res = req.responseText;
            switch (req.status) {
                case 200:
                    self.show(examSessionId);
                    reports.show(res);
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
    });
});