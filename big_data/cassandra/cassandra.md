# Cassandra

- Unlike RDBMS where entities are defined first  
  - then model is created 
  - Finally queries are written for applcation
- But in Cassendra queries to be fired are defined first 
  - Then model is created 
  - Then data is defined

## Cassandra vs RDBMS

| Cassandra | RDBMS |
|-|-|
| `Cassandra Modeling Methodology` | `Sample Relational Model Methodology` |
| Flow : Application -> Model -> Data | Flow : Data -> Model -> Application |
| Model is build on queires to be fired | Model is based on entities to be stored |
| Distrubuted arch | Single Point of failure |
| Use CAP theorm | ACID prop |
| Denormalized | Normalized + Joins and Indices |
| Referential Integrity `not` Enforced | Referential Integrity Enforced |

## Tunable Consistency
- The level of consistency can be defined

## CAP
- Cassendra works on AP (Availability + Partition Tolerance)

## Decentralized
- In Cassendra tables are not normalized 
- Instead related data is stored together
- And Joins are not supported in cassendra
- Example : Say we need to create tables for youtube comments 
  - Here we will define table for two types of queries
    1. Fetching comments on a video
    2. Fetching comments done by user
  - Generally in RDBMS we craete User table, Video table and comments table
  - But in cassendra two table will be created
    1. Vides table with comments included which are present in collection like `videoID1, array<commentIDs>`
    2. User table with comments included which are present in collection like `UserID1, array<commentIDs>`
  - This is denormalized but for performing queries it is very efficient 

![alt text](image.png)


## Cassandra Architecture
- ### Decentraized
  - No node is superior to other 
  - All nodes have different roles
  - And there is no centralized controller
  - Hence if one node becomes unavailable then other node is added
- ### Request reciever node
  - The request reciever node becomes `coordinator node` for that request
  - If consistency is required then the `coordinator node` requests data from `replica ndoes`
  - The `replica nodes` are calculated using `cosistent hashing also`
- ### Primary Key and Partitioning
  - `Primary Key` consist of two types of keys
    - (Mandatory) Partition Keys (Including composite key)
    - (Optional) Clustering column
  - While creating a table the first set defines Partition key and rest defines clustering columns
  
    ```sql
    CREATE TABLE learn_cassandra.users_by_country (
      country text,
      user_email text,
      first_name text,
      last_name text,
      age smallint,
      PRIMARY KEY ((country), user_email)
      --- country : Partition Key
      --- user_email : Clustering column
    );
    ```
- ### KeySpace
  - Alternate to Database in RDBMS
  - It is a top level container used for storing tables
  - While creating one we can define
    - Replication Strategy --> `'class':` 
    - Replication Factor --> `'replication_factor':`
  - Eg.
    ```sql
    CREATE KEYSPACE something
      WITHREPLICATION={'class':'SimpleStrategy', 'replication_factor':2};
    ```
  - `USE` command can be used to set `KeySpace`
  - Else we need to define it while craeting table like : `<keyspace-name>.<table-name>`


## DataTypes

- ### Basic Data Types in Cassandra

| Data Type  | Description |
|------------|------------|
| `ascii`    | Stores US-ASCII characters only. |
| `text`     | Stores UTF-8 encoded strings. |
| `varchar`  | Alias for `text`. |
| `int`      | 32-bit signed integer. Range: -2,147,483,648 to 2,147,483,647. |
| `bigint`   | 64-bit signed integer. Useful for storing large numbers. |
| `varint`   | Arbitrary precision integer. Can store very large numbers but is slower than `int` or `bigint`. |
| `float`    | 32-bit floating-point number. Less precise than `double`. |
| `double`   | 64-bit floating-point number. More precise but uses more storage. |
| `decimal`  | Arbitrary precision floating-point number. More accurate but requires more processing power. |
| `boolean`  | Stores `true` or `false`. |

- ### UUID and TimeUUID

  - **UUID (Universally Unique Identifier)**
    - 128-bit identifier, unique across space and time.
    - Useful for ensuring uniqueness without relying on sequential IDs.
    - Example: `550e8400-e29b-41d4-a716-446655440000`.
    - Generated using `uuid()` function in CQL.

  - **TimeUUID (Version 1 UUID with Timestamp)**
    - Special type of UUID that includes a timestamp component.
    - Useful for sorting events chronologically.
    - Example: `f81d4fae-7dec-11d0-a765-00a0c91e6bf6`.
    - Generated using `timeuuid()` function in CQL.

- ### INET (IP Address)
  - **INET Data Type**
    - Stores IPv4 or IPv6 addresses.
    - Useful for tracking user activity, logging, and networking.
    - Example: `'192.168.1.1'` (IPv4) or `'2001:db8::ff00:42:8329'` (IPv6).

- ### Collection 
  - Storing group of data 
  1. List
      - Ordered collection (May contain duplicates)
      - While creating table use 
        - Syntax : `field_name LIST<Basic-Datatype>` 
        - Example : `emails LIST<TEXT>`
      - Inserting data

        ```sql
        INSERT INTO users (id, name, emails) 
        VALUES (uuid(), 'John Doe', ['john@example.com', 'doe@example.com']);
        ```
      
      - Limitation
        - Since lists maintain order, updates can be expensive
        - Retriving specific element is not possible
  2. Set
      - Unordered Unique collection
      - Syntax : `field_name SET<Basic-Datatype>` 
      - Example : `emails SET<TEXT>`

        ```sql
        INSERT INTO students (id, name, subjects) 
        VALUES (uuid(), 'Alice', {'Math', 'Science', 'History'});
        ```

      - Limitation : Removing elements is efficient, but ordering is not guaranteed
  3. Map  
      - Unoredered unique key-value pair
      - Syntax : `field_name MAP<Key: Value >` 
      - Example : `skills MAP<TEXT, INT>`
      
        ```sql
        INSERT INTO employees (id, name, skills) 
        VALUES (uuid(), 'Bob', {'Java': 5, 'Python': 3, 'SQL': 4});
        ```
    
    - Limitatation : There is no direct way to get all keys or values efficiently
- ### Tuple
  - fixed-size ordered collection of elements 
  - Eg in create table  
    - `activity tuple<timestamp, text>`
  - Insert

    ```sql
    INSERT INTO user_activity (user_id, activity) 
    VALUES (uuid(), (toTimestamp(now()), 'Login'));
    ```

  - But cannot be partially update

- ### UDT (User Defined Type)
  - Creating custom data type
  - Eg.
  
    ```sql
    CREATE TYPE address (
        street text,
        city text,
        zipcode int
    );
    ```

  - This can be used in another table

    ```sql
    CREATE TABLE users (
        user_id UUID PRIMARY KEY,
        name text,
        home_address frozen<address> -- Using UDT as a column type
    );
    ```

  - Inserting data

    ```sql
    INSERT INTO users (user_id, name, home_address) 
    VALUES (uuid(), 'John Doe', { street: '123 Main St', city: 'Mumbai', zipcode: 400001 });
    ```

  - UDT cannot be partially modified
  - Cannot be used as partition key 

| Data Type  | Description | Table Definition | Insertion Example | Insertion Format Example |
|------------|-------------|------------------|-------------------|--------------------------|
| **Timestamp** | Consistent format: `YYYY-MM-DD HH:MM:SS`<br>Stores both date and time | `event_time timestamp` | ```sql INSERT INTO events (event_id, event_name, event_time)```<br>``` VALUES (uuid(), 'Product Launch', toTimestamp(now())); ``` | `2025-02-06 15:30:45` |
| **Date** | Stores only date in `YYYY-MM-DD` format | `event_date date` | ```sql INSERT INTO events (event_id, event_name, event_date) ```<br>``` VALUES (uuid(), 'Conference', '2025-03-15'); ``` | `2025-03-15` |
| **Time** | Stores only time in `HH:MM:SS` format | `event_start_time time` | ```sql INSERT INTO events (event_id, event_start_time)```<br>``` VALUES (uuid(), '12:30:00'); ``` | `12:30:00` |
| **Duration** | Stores duration. Two format options:<br>1️⃣ ISO 8601 format: `P[n]Y[n]M[n]DT[n]H[n]M[n]S` or `P[n]W`<br>2️⃣ ISO 8601 alternative format: `P[YYYY]-[MM]-[DD]T[hh]:[mm]:[ss]` | `event_duration duration` | ```sql INSERT INTO events (event_id, event_name, event_duration) ```<br>``` VALUES (uuid(), 'Task', 'P2Y3M5DT6H30M'); ``` | `P2Y3M5DT6H30M` (ISO 8601 format)<br>`P2025-03-15T12:30:00` (ISO 8601 alternative format) |

## Commands
- ### Truncate
  - If used then data from persistance storage will be removed
  - If once removed then can be restored from backup
  - If used with `JMX` command will delete from all nodes
  - If even one node is down then the command will fail
- ### Alter
  - All functionalites are available except
  - Cannot change primary key column
- ### Source 
  - Used to run a `.cql` file
- ### Where
  - **We need to mention `partition key` when using `where` clause**

