
var express = require('express');
var app = express();

app.use(express.static(__dirname + '/public'));

app.get('/', function (req, res) {
    res.redirect('login.html')
});

var server = app.listen(8801, function () {
    var port = server.address().port;
    console.log("Node server listening at http://localhost:%s", port)
});
