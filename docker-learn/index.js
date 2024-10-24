const express = require("express");
const app = express();
const { createClient } = require("redis");
app.use(express.json());
const client = createClient({
 url: 'redis://redis-container:6379'
});


client.on('error', (err) => {
  console.error('Redis Client Error', err);
});

(async () => {
  await client.connect();
})();

app.get("", (req, res) => {
  res.send("hello redis");
});

app.post("/set", async (req, res) => {

  const data = req.body;
  if(typeof data !== 'object' || data === null){
    res.status(404).send("invalid data");
  }

  try{
    console.log(data);
    if(data.name){
      await client.set('name',data.name);
    }
    if(data.last){
      await client.set('last',data.last);
    }
    res.status(400).send("done")
  }catch(err){
    res.status(500).send(err);
  }


});

app.listen(8000, () => {
  console.log("Server running on port 8000");
});