# MongDB practice 

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

## To wirte code in another editor 
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