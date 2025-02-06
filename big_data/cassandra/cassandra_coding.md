# Cassandra Practical

## Crreating Keyspace

```sql
CREATE KEYSPACE store 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'} ;
```

## Create table

```sql
create table <keyspace-name>.<table-name> (
    field-name field-type,
    field-name field-type,
    ...,
    primary key(field-name, ...));
---
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

## Inserting value into table
- You cannot insert multiple rows using insert into

| ✅Correct | ❌Incorrect |
|-|-|
| `INSERT INTO table1 (id) VALUES (uuid());`<br>`INSERT INTO table1 (id) VALUES (uuid());`<br>`INSERT INTO table1 (id) VALUES (uuid());`<br> | `INSERT INTO table1 (id) VALUES (uuid()),(uuid()),(uuid()),(uuid());` |

- Other ways to adding multiple values at once

```sql
BEGIN BATCH
    INSERT INTO users (id, name, age) VALUES (uuid(), 'Alice', 25);
    INSERT INTO users (id, name, age) VALUES (uuid(), 'Bob', 30);
    INSERT INTO users (id, name, age) VALUES (uuid(), 'Charlie', 28);
APPLY BATCH;
---
COPY users (id, name, age) FROM 'users.csv' WITH HEADER = TRUE;
```
