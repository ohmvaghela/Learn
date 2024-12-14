const {MongoClient} = require("mongodb");

const url = "mongodb://localhost:27017";
const client = new MongoClient(url);

client.connect()
    .then(() => console.log('client connected succefully'))
    .catch(error => console.log('failed to connect : ',error));

const getCollection = async function(){
    const db = client.db("dummy");
    const collection =  db.collection("sales");
    return collection;
};
module.exports = getCollection;