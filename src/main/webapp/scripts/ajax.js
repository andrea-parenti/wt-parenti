/**
 * Creates a request and sends it to the server.
 * @param method the http method to use.
 * @param url the url of the request.
 * @param formElement the form that contains useful parameters for the request.
 * @param cbFunction the function invoked to manage the request progress and the response.
 */
function makeCall(method, url, formElement, cbFunction) {
    const request = new XMLHttpRequest();
    request.onreadystatechange = () => {
        cbFunction(request);
    }
    request.open(method, url);
    if (formElement == null) {
        request.send();
    } else {
        request.send(new FormData(formElement));
    }
}