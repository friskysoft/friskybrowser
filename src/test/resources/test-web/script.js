
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
        flash_msg_element.style.cssText = "display: block";
    }
}

function search() {
    var keyword = document.getElementById("search").value;
    var flash_msg_element = document.getElementById("flash-message");
    var spinner = document.getElementById("spinner");
    var search_result = document.getElementById("search-result");

    search_result.style.visibility = "hidden";
    flash_msg_element.style.cssText = "display: block";

    if (keyword) {
        spinner.style.visibility = "visible";
        var loading = setInterval(function() {
            if (spinner.style.visibility = "hidden") {
                search_result.innerHTML = "Showing results for " + keyword;
                search_result.style.visibility = "visible";
                clearInterval(loading);
            }
        }, 2000);
    } else {
        flash_msg_element.innerHTML = "Search keyword is empty!";
        flash_msg_element.style.cssText = "display: block";
        search_result.style.visibility = "hidden";
    }
}

setInterval(function() {
    document.getElementById("spinner").style.visibility = "hidden";
}, 5000);
