const express = require('express');
const app = express();
app.set('view engine', 'ejs');

const path = require('path');
const dirPath = path.join(__dirname, '/index.html');


app.get('', (req, res) => {
    // res.sendFile(dirPath);
    res.send("main page");
});

app.get('/profile', (_, res) => {
    // res.sendFile(dirPath);
    res.render('profile');
});


app.get('/about', (req, res) => {
    res.send("about page");
});

app.listen(8000);

// const os = require("os");
// console.log(os.freemem()/(1024*1024*1024));
// console.log(os.totalmem()/(1024*1024*1024));
// console.log(os.arch());
// console.log(os.hostname());
// console.log(os.userInfo());