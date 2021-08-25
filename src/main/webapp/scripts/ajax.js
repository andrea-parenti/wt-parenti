/**
 * Creates a request and sends it to the server.
 * @param method the http method to use.
 * @param url the url of the request.
 * @param formElement the form that contains useful parameters for the request.
 * @param cbFunction the function invoked to manage the request progress and the response.
 * @param contentType the requested type of content.
 * @param reset a boolean for resetting the form.
 */
function makeCall(method, url, formElement, cbFunction, contentType, reset = true) {
    const request = new XMLHttpRequest();
    request.onreadystatechange = () => {
        cbFunction(request);
    }
    request.open(method, url);
    if (contentType) {
        request.setRequestHeader("Content-Type", contentType);
    }
    if (!formElement) {
        request.send();
    } else {
        request.send(new FormData(copy(formElement)));
    }
    if (formElement && reset === true) {
        formElement.reset();
    }
}