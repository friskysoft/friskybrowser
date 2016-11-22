
function login() {
    var errmsg;
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    if (!username && !password) {
        errmsg = "Username and Password cannot be empty";
    } else if (!username) {
        errmsg = "Username cannot be empty";
    } else if (!password) {
        errmsg = "Password cannot be empty";
    } else if (username == "test" && password == "t3st") {
        window.location = "home.html";
        return;
    } else {
        errmsg = "Bad Credentials";
    }

    if (errmsg) {
        var flash_msg_element = document.getElementById("flash-message");
        flash_msg_element.innerHTML = errmsg;
        flash_msg_element.style = "display: block";
    }
}