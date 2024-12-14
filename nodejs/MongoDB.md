# MongoDB

- Database
    - Collection (Tables:Analogy)
        - Documents `{name:"ohm",id:"2037",age:"21"}`
            - fields (`name:"ohm"`) this singley is known as field

# mongoDB initial command

- Start
```
sudo systemctl start mongod
```
- Stop
```
sudo systemctl stop mongod
```
- Reload
```
sudo systemctl daemon-reload
```
- Status
```
sudo systemctl status mongod
```


## Start

    mongosh

- show all database

        show dbs 
- Create database or use if existing

        use newDB 
- Delete database

        newDB.dropDatabase()
- View Collections

        show collections
- Insert Single Data (If collection exist then data will added to that collection or else new collection will be created)

        db.user.insertOne({name:"ohm", roll:"19ME02037"})
- Insert Multiple data

        db.user.insertMany([ {name:"ohm"}, {name:"ohm1"}, {name:"ohm2"} ])
- Find data 

        db.user.find() // if no argumnt is passed then entire collection will be displayed
- Find only first 2 data 

        db.user.find().limit(2)
- Skip first three and show next two

        db.user.find().skip(3).find(2)
- Sort data and find only 2

        db.user.find().sort({name, 1}).limit(2)// sort by key => name, 1 => ascending, -1 => descending

# Connecting mongodb with nodejs
```js

const {MongoDB} = require('mongodb');
const url = 'mongodb://localhost:27017';
const client = new MongoDB(url); // our nodejs is client which fetches data from mongodb database

async function getData()// async as data fetching might take time
{
    // await waits till promise in not completed
    let result = await client.connect();//client.connect is promise hence await is used
    let db = result.db('MyDB');//MyDB is database created by me in localhost
    let collection = db.collection('studentData');//access pericular collecion
    let response = await collection.find();//collection.find() is also promise
    console.log(respnonse);//this will print data
}

```
## But we prefer storing connections in different folder

- This file connects to DB, so connection is made only once
- File name: `mongoconnect.js`

    ```js
    const {MongoDB} = require('mongodb');
    const url = 'mongodb://localhost:27017';
    const client = new MongoDB(url);

    async function getData()
    {
        let result = await client.connect();
        db = result.db('myDB');
        return db.collection('studentData');
    }

    module.exports = getData;

    ```
- Main file

    ```js
    const collection = require('./mongoconnect');
    const collect = await colelction();
    // read file
    const Read = async ()=>{
        const data = await collect.find().toArray();
        console.log(data);
    }

    const Insert = async ()=>{
        const result = await collect.insert(
            {name:"abc", roll:"19ME01001"},
            {name:"cdr", roll:"19ME01002"},
        );
        console.log(result);//will return acknowledgement, insertCount, insertID
    }

    const Upadate = async ()=>{
        const result = await collect.update(
            {name:"abc",roll:"19ME01001"},
            {   $set: {name:"oaoo"}     }
        );
        console.log(result);
    }

    const Delete = async ()=>{
        const result = await collect.deleteOne(
            {name:"abc"}
        );
        console.warn(result);
    }
    ```

- > why cant we pass `body` in `GET` method?
- > What is `body`
