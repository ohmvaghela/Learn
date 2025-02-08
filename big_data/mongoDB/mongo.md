# MongoDB

## Context
- [Base](./mongo.md#base)
- [Storage Engine](./mongo.md#storage-engine)
- [BSON vs JSON](./mongo.md#bson-vs-json)
- [Indexing](./mongo.md#indexing)
- [Replication](./mongo.md#replication-in-mongodb)
- [Partitioning v/s Sharding](./mongo.md#partitioning-vs-sharding)
- [Sharded Cluster](./mongo.md#sharded-cluster)

## BASE 
- BASE
  - Baiscally Available
    - The system prioritizes availability over consistency
    - Even in the face of failures, partitions, or network issues, the system remains operational and continues to serve requests.
  - Soft state
    - The system allows intermediate states and does not require immediate consistency.
    - Data updates may happen in the background, and applications should be able to handle this without crashing.
    - The consistency will occur eventually so the data updation may occur by background process
    - So the system should be able to handle it and not crash
  - Eventual Consistenct
    - The data in all the servers will not update immediately when the client sends update request
    - Instead it will update slowly and in an orderly manner

## Storage Engine
- Defines how MongoDB stores data
- There are two engines that are generally used with mongoDB  
  - WiredTiger(Default Storage Engine)
  - In-Memeory Storage Engine
- WiredTiger
  - Disk storage
  - Provide Document level consistency 
    - Locking only single document for concurreny instead of whole collection
  - Uses Write-Ahead-Logging(WAL) : Write in logs beforing filling data in Disk storage
- In-Memory Storage
  - Provide Document level consistency
  - In memory storage (RAM storage)
  - Non persistant : Data lost when system restart
  - Very fast


## BSON v/s JSON
- BSON (Binary JSON) is extended version of JSON
- BSON adds more data types and optimizing storage and retrieval speed


## Indexing
- When we create index on a field mongodb creates a B-Tree (Binary tree) for efficient search
- To create index on a field use 

  ```js
  db.users.createIndex({ income: 1 });
  // Say there were the incomes in the collection
  // [{ income: 10 }, { income: 20 }, { income: 30 }, { income: 40 }, { income: 50 }, { income: 60 }, { income: 70 }]
  ```

- So B-Tree will look like

  ```
          [40]
        /    \
    [20]       [60]
    /   \      /   \
  [10]  [30] [50]  [70]
  ```

## Replication in MongoDB
- Helps in 
  - FaultTolerace
  - Redundancy
  - High Availabilty
- Architecture
  - Primary Node : Accept read and write
  - Secondary Node : Syncs with primary and Only support read
  - Arbiter Node : Elects Secondary Node to primary when primary fails
- How MongoDB Replication Works
  - Writes go to the Primary node.
  - Secondary nodes replicate data asynchronously.
  - If the Primary fails, an election occurs to promote a new Primary.
  - When the original Primary recovers, it becomes a Secondary.
- Starting up replicas

  ```sh
  mongod --port 27017 --dbpath /data/node1 --replSet "myReplicaSet"
  mongod --port 27018 --dbpath /data/node2 --replSet "myReplicaSet"
  mongod --port 27019 --dbpath /data/node3 --replSet "myReplicaSet"
  ```

- Connecting to replica set
  
  ```js
  rs.initiate({
    _id: "myReplicaSet",
    members: [
      { _id: 0, host: "localhost:27017" },
      { _id: 1, host: "localhost:27018" },
      { _id: 2, host: "localhost:27019" }
    ]
  });
  ```

- Check status

  ```js
  rs.status();
  ```

## Partitioning v/s Sharding
- Data is divided into small subsets 
- Like for a collections id 1-1000 is one subset, 1000-2000 is another and so on
- Now when the subsets are stored in single server it is called Partitioning
- When the subsets are stored in seperate server it is called Sharding

## Sharded Cluster
- Sharded Cluster has many componenets
  - `Shards` : Actual data store
  - `Config Server` : Metadata and config details of cluster
  - `Query Router(Mongos)` : Direct client query to correct `shard`

- Implementing sharding
  - Enable sharding
    - `sh.enableSharding("myDatabase")`
  - Create index on shardkey
    - `db.users.createIndex({ user_id: 1 }) `
  - Shard collection
    - `sh.shardCollection("myDatabase.users", { user_id: "hashed" })`
  - Here `user_id` is `shard key`
- Types of sharding
  1. Rangle based : Like age, salary `{ age: 1 }`
  2. Hash Based : `{ user_id: "hashed" }`
  3. Zone Based : `{ region : 1}`
