const express = require("express");
const app = express();
const fs = require('fs');

const rs = fs.createReadStream('../README.md');
rs.on('open', function () {
  console.log('The file is open');
});

app.get("/", (req, res) => {
    res.send("Hello, world!");
});

app.listen(8000, () => {
    console.log("Server is listening on port 8000");
});