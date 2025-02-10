# MongDB practice 

## Context
- [Creating DB](./mongo_coding.md#creating-db)
- [Creating Collection](./mongo_coding.md#creating-collection)
- [Creating Collection with Fix schema](./mongo_coding.md#creating-collection-with-fix-schema)
- [Inserting data into collection](./mongo_coding.md#inserting-data-into-collection)
- [Find Document](./mongo_coding.md#find-document)
- [Unusual DataTypes in MongoDB](./mongo_coding.md#unusual-datatypes-in-mongodb)
- [Running startup script](./mongo_coding.md#running-startup-script)
- [Creating default startup script](./mongo_coding.md#creating-default-startup-script)
- [Changing mongosh interface](./mongo_coding.md#changing-mongosh-interface)
- [To write code in another editor](./mongo_coding.md#to-write-code-in-another-editor)
- [Limiting the size of collection](./mongo_coding.md#limiting-the-size-of-collection)
- [Indexing](./mongo_coding.md#indexing)
- [TTL](./mongo_coding.md#ttl)
- [Search on Index](./mongo_coding.md#search-on-index)
- [Find with condition (where/order by)](./mongo_coding.md#find-with-condition-whereorder-by)
- [Find in array](./mongo_coding.md#find-in-array)
- [Aggregate, Group and Match](./mongo_coding.md#aggregate-group-and-match)
- [Counting total number of documents](./mongo_coding.md#counting-total-number-of-documents)
- [Importing data](./mongo_coding.md#import-data)

## Creating DB
- When we use `use <database-name>` it creates database 
- But only stores it when first data is added
- Example

  ```js
  use test_db;
  // till this point db is created
  // but database is not stored
  db.createCollection('collection1');
  // at this point test_db is stored in database
  show databases;
  // this will display all db including test_db
  ```

## Creating Collection

  ```js
  // select the db to be used
  use test_db;
  // To create collection use
  db.createCollection('collection1');
  // to list all collectons
  show collections;
  ```

## Creating Collection with Fix schema
  - Required : defines which fields are mandatory
    - Extra fields can also be added
  - properties : rules that fields must follow
    - These can be nested as well like example of address in the code
  - bsonType : defines the data types like [object, string, int, bool ...]
  - `$jsonSchema` : use JSON schema validator 

    ```js
    db.createCollection("<collection-name>", {
      validator : {
          $jsonSchema: {
              bsonType: "object",
              required: ["_id", "name", "age", "email"],
              properties: {
                  _id: { bsonType:"string", description: "Must be unique string" },
                  name: {bsonType:"string"},
                  age: {bsonType: "int", minimum:18, maximum:60},
                  email: { bsonType: "string", pattern: "^.+@.+\\..+$", description: "Must be a valid email format" },
                  items: {bsonType: "array", minItems: 1, item: {bsonType: "string"}},
                  address: {
                      bsonType: "object",
                      required: ["city", "zip"],
                      properties: {
                          city: { bsonType: "string" },
                          zip: { bsonType: "string", pattern: "^[0-9]{6}$" } // Enforcing 6-digit ZIP
                      }
                  }
              }
          }
      }
    });
    ```

  - Validator can also be applied directly

    ```js
    db.create("user",{
      validator:{
        salary: {$gte: 30000}
      }
    })
    ```

  - To restrict from adding extra fields to table use `additionalProperties: false` in `$jsonSchema`

    ```js
    db.create("users",{
      validator: {
        $jsonSchema: {
          bsonType: "object",
          required: ["_id", "name", "age", "email"],
          additionalProperties: false
        }
      }
    });
    ```

  - We can also add optional adding of fields using `oneOf`

    ```js
    db.createCollection("users", {
      validator: {
        $jsonSchema: {
          bsonType: "object",
          oneOf: [
            { required: ["phoneNumber"] },
            { required: ["email"] }
          ]
        }
      }
    });
    ```


## Inserting data into collection
  - One document at a time

    ```js
    db.collcetion1.insertOne({
      "name":"ohm",
      "salary":1000000,
      ... // few more fields
    });
    ```
    
  - Multiple document at a time

    ```js
    db.collection1.insertMany([
      {
      "name":"ohm",
      "salary":1000000
      },
      {
      "name":"ohm",
      "salary":1000000
      },
      {
      "name":"ohm",
      "salary":1000000
      }
    ]);
    ```
  
  - Inserting array 

    ```js
    db.products.insertOne({
      name: "Electronics",
      items: ["Laptop", "Smartphone", "Tablet"]
    });
    ```

## Find Document
- Base syntax

  ```js
  db.collection.find(query, projection);
  db.collection.find(query, projection).limit(n); 
  db.collection.findOne(query, projection);
  db.collection.find(query).sort({ field: 1 });  // Ascending order
  db.collection.find(query).sort({ field: -1 }); // Descending order
  db.collection.find().skip(n); // skip first n docuemnt
  db.collection.find(query).count(); // count number of docs
  db.collection.distinct("field", query); // return only distinct values form doc and not full doc
  ```

- Find all document
  
  ```js
  db.collection1.find();
  db.employees.find().sort({ "salary": 1 });
  ```

- Find with conditition

  ```js
  // in projection -> 0 : dont show value, 1 : show value
  db.employees.find({ "department": "Engineering" }, { "name": 1, "salary": 1, "_id": 0 });
  // Find only one document
  db.employees.findOne({ "department": "Finance" }, { "name": 1, "_id": 0 });
  db.employees.findOne();
  ```


## Unusual DataTypes in MongoDB
- Object

  ```js
  {
    "name": "John",
    "address": {
      "city": "Delhi",
      "zip": "110001"
    }
  }
  ```

- ObjectID

  ```js
  {
    "_id": ObjectId("507f1f77bcf86cd799439011")
  }
  ```

- Date

  ```js
  {
    "createdAt": ISODate("2024-02-07T10:00:00Z")
  }  
  ```

- TimeStamp

  ```js
  {
    "lastUpdated": Timestamp(1612137600, 1)
    // First Value (1612137600): Represents the epoch time (UNIX timestamp) in seconds since January 1, 1970 (UTC).
    // Second Value (1): Represents an incremental counter that differentiates multiple operations occurring in the same second
  }
  ```

- NumberDecimal

  ```js
  {
    "price": NumberDecimal("1234.56789")
  }
  ```

- BinData
  - 64bit encoded data like image, pdf etc

  ```js
  {
    "profilePicture": BinData(0, "iVBORw0KGgoAAAANS...")
  }
  ```

## Running startup script

- To pass args into file using following
- The first arg is always db name converntionally
```
mongosh myDatabase --file startup.js arg1 arg2 arg3
```

```js
// Get arguments from the command line
var dbName = process.argv[2]; // First argument (database name)
var arg1 = process.argv[3];   // Second argument
var arg2 = process.argv[4];   // Third argument
var arg3 = process.argv[5];   // Fourth argument

// Validate database name
if (!dbName) {
  print("Error: No database name provided!");
  quit();
}

// Switch to the database
db = db.getSiblingDB(dbName);

// Print a welcome message
print("Connected to database: " + db.getName());

// Print the provided arguments
print("Argument 1:", arg1);
print("Argument 2:", arg2);
print("Argument 3:", arg3);

// Example: Use the arguments in an operation
db.settings.insertOne({
  setting1: arg1,
  setting2: arg2,
  setting3: arg3
});

print("Inserted settings with provided arguments.");
```

- To run multiple files

```js
mongosh --file script1.js --file script2.js
```

## Creating default startup script

- To run a startup script when the mongosh starts update create `.mongoshrc.js` and run it

```js
// .mongoshrc.js
// Auto-select database
db = db.getSiblingDB("myDatabase");

// Print a welcome message
print("Welcome to MongoDB Shell! Connected to: " + db.getName());

// Display available collections
print("Collections: ", db.getCollectionNames());

// Define a helper function
function getUserByEmail(email) {
  return db.users.findOne({ email: email });
}

// Print confirmation
print("Startup script executed!");
```

- If you dont want to run this on start use

```
mongosh --norc
```

## Changing mongosh interface 

- create .mongoshrc.js and add

```js
// Function to get hostname
function getHostName() {
    const status = db.runCommand({ hostInfo: 1 });
    return status.system.hostname;
}

// Custom prompt function
prompt = function() {
    return "[User: " + db.runCommand({ connectionStatus: 1 }).authInfo.authenticatedUsers[0]?.user +
           " | Host: " + getHostName() +
           " | DB: " + db.getName() + "] > ";
};

print("Custom prompt set! Host:", getHostName(), "| Database:", db.getName());
```

## To write code in another editor 
- use `EDITOR="vim" mongosh` to open mongo bash
- Or add `EDITOR="vim"` in `.bashrc` 

## Limiting the size of collection
- We use `capped` option

```js
db.createCollection("myCollection", {
  capped: true, // limit size of collection 
  size: 1024, // (mandatory) : max size of collection in bytes
  max: 100, // max number of documnets in collection
});
```

- Using it with validator

```js
db.createCollection("cl2", {
    capped: true,
    size: 1024,
    max: 1,
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["name", "age"],
            properties: {
                name: {
                    bsonType: "string",
                    description: "Must be a string"
                },
                age: {
                    bsonType: "int",
                    minimum: 18,
                    description: "Must be at least 18"
                }
            }
        }
    }
});
```

## Indexing
- To create index

  ```js
  db.<collection-name>.createIndex({<field-name>:1/-1});// 1 : ASC | -1 DESC
  db.collection1.createIndex({income:1});
  ```

- To create compound index

  ```js
  db.<collection-name>.createIndex({<field-name>:1/-1,<field-name>:1/-1, ...});
  db.collection1.createIndex({salary:-1,name:1});
  ```

- Prevent duplicate values into collection
  - If the collection already has same values for the field provided
    - Index wont be created
  - Once index is created with `unqiue`, we cannot add duplicate values
  
  ```js
  db.<collection-name>.createIndex({<field-name>:1/-1}, {unique: true});
  db.collection1.createIndex({salary:1}, {unique: true});
  ```

- To skip null values while indexing use `sparse indexing` 
  - Does not index null values

  ```js
  db.<collection-name>.createIndex({<field-name>: 1/-1}, {sparse: true});
  db.collection1.createIndex({phone1: 1}, {sparse: true});
  ```

## TTL 
- To add TTL(Time To Live) you can create an index on a field on which you want to apply TTL
- The field must be a date, timestamp field
- You have to explicitly mention the field on which you are applying TTL

  ```js
  db.<collection-name>.createIndex({ <field-name>: 1 }, { expireAfterSeconds: <expiry-time-in-seconds/> }); // TTL for 10 sec
  db.collection1.createIndex({ createdAt: 1 }, { expireAfterSeconds: 10 }); // TTL for 10 sec
  ```

## Search on Index
- To enable text search on a field use following

  ```js
  db.<collection-name>.createIndex({<field-name>:"text"});
  db.collection1.createIndex({context:"text"});
  ```

  - For searching use following syntax

    ```js
    db.<collection-name>.find({$text: { $search: "string to be seached"}});
    db.articles.find({ $text: { $search: "mongodb tutorial" } });
    ```

- We can apply seach on multiple fields in document
  
  ```js
  db.<collection-name>.createIndex({<field-name>:"text", <field-name>:"text"});
  ```

  - We cannot apply find on perticular field it will be applied on all field
  
    ```js
    db.<collection-name>.find($text: {$search: "string to be searched"});
    db.articles.find({ $text: { $search: "database tutorial" } });
    ```


## Find with condition (where/order by)
- To apply where clause

  ```js
  db.<collection-name>.find({<field-name>: {<condition>}, <field-name>: {<condition>}, ...});
  db.user.find({ age: {$gt: 25}}); // find for age greater then 25
  db.user.find({ age: {$gt: 25}, city: "Surat"}); // find for age greater then 25
  ```

- Other operators

  | SQL Operator            | MongoDB Equivalent                     |
  |-------------------------|----------------------------------------|
  | = (equal to)           | `{ field: value }` or `{ field: { $eq: value } }` |
  | != (not equal to)      | `{ field: { $ne: value } }`            |
  | > (greater than)       | `{ field: { $gt: value } }`            |
  | < (less than)         | `{ field: { $lt: value } }`            |
  | >= (greater than or equal) | `{ field: { $gte: value } }`       |
  | <= (less than or equal) | `{ field: { $lte: value } }`         |

- Sort the output

  ```js
  db.<collection-name>.find().sort({<field-name>: 1/-1});
  db.user.find().sort({age: 1});
  ```

- Using regex on find

  ```js
  db.<collection-name>.find({<field-name>: <pattern/>})
  db.users.find({ name: /John/ });
  ```

- Using `in` and `not in` clause

  ```js
  // in clause
  db.<collection-name>.find({<field-name>: { $in : ["something","something", ...]}})
  db.user.find({city: {$in : ["surat", "blr", "pune"]}})
  
  // not in clause
  db.<collection-name>.find({<field-name>: { $nin : ["something","something", ...]}})
  db.users.find({ city: { $nin: ["New York", "Los Angeles"] } });
  ```

- Finding in nested document
  - Just use `.` for nested docs

  ```js
  db.users.find({ "address.city": "New York" });
  ```

  - Another way is 

  ```js
  // may return even if one condition matches
  db.users.find({ orders: { orderId: 101, total: 50 } });
  ```

  - Another way

  ```js
  // only all the condition exist
  db.users.find({
    orders: { $elemMatch: { orderId: 101, total: { $gt: 60 } } }
  });
  
  // more
  db.users.find({ "address.state": { $exists: true } });
  db.users.find({ orders: { $size: 2 } });
  ```

## Find in array
- Exact match

  ```js
  db.users.find({ tags: ["mongodb", "database"] });
  // ✅ { "_id": 1, "tags": ["mongodb", "database"] }
  // ❌ { "_id": 2, "tags": ["database", "mongodb", "NoSQL"] }
  ```

- Document can contain extra

  ```js
  db.users.find({ tags: "mongodb" });
  db.users.find({ tags: { $all: ["mongodb", "database"] } });
  // ✅ { "_id": 1, "tags": ["mongodb", "database"] }
  // ✅ { "_id": 2, "tags": ["database", "mongodb", "NoSQL"] }
  ```

- Document matches the number fo elements

  ```js
  db.users.find({ tags: { $size: 3 } });
  ```

- For nested array

  ```js
  // Does not enforce all conditions
  db.users.find({
    orders: { orderId: 101, total: { $gt: 60 } }
  });

  // Enforces all conditions
  db.users.find({
    orders: { $elemMatch: { orderId: 101, total: { $gt: 60 } } }
  });
  ```

## Aggregate, Group and Match
- Match : Used for filtering
- Group : Used for grouping 
- Aggregate functions group offer
  - sum
  - avg
  - min
  - max
  - push
  - first
  - last

- Using group and match together

  ```js
  db.<collecction-name>.aggregate([
    { $match : { <field-name>: { <match-options> } } }
    { $group: { _id: "$<field-name>", <output-field name>: {$<aggregate_fn>: "$<field to be aggregated"} }}
  ]);
  
  db.orders.aggregate([
  { $match: { amount: { $gt: 200 } } },
  { $group: {
    _id: "$customerId", 
    totalSpent: { $sum: "$amount" }, 
    avgSpent: { $avg: "$amount" }, 
    maxSpent: { $max: "$amount" }} 
    }
  ]);
  // total spent will be the name of field that will be shown as output
  // these will be grouped by customerId
  // sum of amount field will be done
  ```

- To count number of docs, use any arbitanry value with sum

  ```js
  db.user.aggregate([
    { $group: {
      _id: "$customerId",
      count: { $sum : 1 }
    }}
  ])
  ```

## Counting total number of documents

1. Using estimatedDoumentCount()
    
    ```js
    db.<collection-name>.estimatedDoumentCount();
    ```

2. Using count

    ```js
    db.<collection-name>.count(query);
    db.user.count({
      age: { $gt: 30 }
    });
    ```

3. Distinct

    ```js
    db.<collection-name>.distinct( field, query );
    db.user.distinct( "category", { price : { $gt: 4000 } } );
    ```

## Import Data
### JSON
- To import json file like

  ```json
  { "_id" : { "$oid" : "50b59cd75bed76f46522c34e" }}
  { "_id" : { "$oid" : "50b59cd75bed76f46522c34f" }}
  { "_id" : { "$oid" : "50b59cd75bed76f46522c34e" }}
  { "_id" : { "$oid" : "50b59cd75bed76f46522c34f" }}
  ```

- Use

  ```
  mongoimport --db <db-name> --collection <collection-name> --file <file-path> 
  ```

- If the json file is in array format like following 

  ```json
  [
    { "_id" : { "$oid" : "50b59cd75bed76f46522c34e" }},
    { "_id" : { "$oid" : "50b59cd75bed76f46522c34f" }},
    { "_id" : { "$oid" : "50b59cd75bed76f46522c34e" }},
    { "_id" : { "$oid" : "50b59cd75bed76f46522c34f" }}
  ]
  ```

- Then we need to mention it is in array format

  ```
  mongoimport --db <db-name> --collection <collection-name> --file <file-path> --jsonArray
  ```

### CSV
- If csv file does not have header

  ```
  mongoimport --db <db-name> --collection <collection-name> --type csv --file <file-path> 
  ```

- If csv file does has header

  ```
  mongoimport --db <db-name> --collection <collection-name> --type csv --file <file-path> --headerline
  ```


