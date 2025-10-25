# MongoDB

## Context
- [Base](./mongo.md#base)
- [Storage Engine](./mongo.md#storage-engine)
- [BSON vs JSON](./mongo.md#bson-vs-json)
- [Indexing](./mongo.md#indexing)
- [Replication](./mongo.md#replication-in-mongodb)
- [Partitioning v/s Sharding](./mongo.md#partitioning-vs-sharding)
- [Sharded Cluster](./mongo.md#sharded-cluster)
- [Search Indices](./mongo.md#search-indices)

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

## Two Types of architecture in mongodb
1. Replica Set 
    - One node is master others are slaves
    - Only master serves read-write request
    - Slaves serve only read requests
2. Sharded Cluster
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

| Feature          | Replica Set                                     | Sharded Cluster                                   |
|------------------|-------------------------------------------------|---------------------------------------------------|
| **Purpose** | High availability, data redundancy             | Horizontal scalability, large datasets, high availability |
| **Data Storage** | Full dataset on each member                     | Data partitioned and distributed across shards     |
| **Scalability** | Read scalability, limited write scalability      | Read and write scalability                        |
| **Architecture** | Primary-secondary replication                   | Shards, config servers, query routers (mongos)     |
| **Consistency** | Relatively strong consistency, potential for temporary inconsistencies | More complex consistency, trade-offs to consider   |
| **Availability** | High availability within a single dataset, brief failover downtime | Very high availability, individual shard failures less impactful |
| **Complexity** | Simpler setup and management                  | More complex setup and management                 |
| **Use Cases** | Data protection, read-heavy workloads           | Large datasets, high throughput, horizontal scaling |
| **Data Distribution** | Duplicated data across all members | Partitioned data across multiple shards |



## Search Indices
- Mongo has two main processes
  - `mongod` and `mongot`
- `mongod` is main deamon that runs mongo
- `mongot` is sidecar, only called for `Atlas Search` or `Vector Search` queries i.e. `$search`

### Apache Lucence
- Search engine toolkit written in java
- `mongot` uses it for seach index storage
- It does primarily these things
  - Tokenize the searchable fields set for indexing
    - Eg. "Ohm is software engineer" to "Ohm", "is", "software", "engineer"
  - Each tokenized work is mapped to docuemnt_ids
  - When a search query is called `mongot` ties to map most relevent doc using algos like `TD-IDS`, `BM25`...


### Atlas Search v/s Atlas Vector Search
- Atlas Vector saerch is an addon to atlas Search
- For most parts Atlas Search works and when we add ai, probablity and all other things Atlas vector search comes into play
- For most part this doc will be for Atlas Search Except this small section for atlas Vector search

> #### Atlas Vector Search
> - Uses Vector indexing (using HNSW: Hierarchical Navigable Small World graph): youtube HNSW when required
> - Uses Similarity scoring (e.g., cosine similarity, dot product, or Euclidean distance
> - Native integration with text search and filters in the same aggregation pipeline
> - where queries look like
> - db.events.aggregate([
> ```
> {
>    $vectorSearch: {
>      index: "eventHybridIndex",
>      path: "embedding",
>      queryVector: [0.12, 0.94, 0.47, ...], // Needs to be provided be querier
>      numCandidates: 200,
>      limit: 5,
>      filter: {
>        city: "Mumbai"
>      }
>    }
>  },
>  {
>    $project: { title: 1, city: 1, score: { $meta: "vectorSearchScore" } }
>  }
> ])
> ```

### Atlas Search Features
- Search Natural Language Text
  - Searches on lucence's tokanized words
  - Also performs Stemming: reduces words to their root form (“running” → “run”)
  - Stop words removal: ignores common words like “the”, “in”, “of”
  - When returning the result it also returns the score which is `Relevance-based ranking`
- Example of most basic query

```js
db.events.aggregate([
  {
    $search: {
      index: "eventSearchIndex",
      text: { query: "music festival", path: ["title", "description"] }
    }
  },
  { $project: { title: 1, score: { $meta: "searchScore" } } }
])
```
```json
[
  { "title": "Music Festival 2025", "score": 9.1 },
  { "title": "Rock Music Festival", "score": 7.3 },
  { "title": "Summer Concert", "score": 3.5 }
]
```
- Other features include
  1. Fuzzy
     - Allows misspells to a certain limit
     - Example of fuzzy text search

     ```js
      db.events.aggregate([
        {
          $search: {
            index: "eventSearchIndex",
            text: { // Text search
              query: "festval",      // typo
              path: "title",
              fuzzy: { maxEdits: 1 } // typo tolerance
            }
          }
        }
      ])
      ```
  2. Autocomplete
      - Returns the rest of the words completed

     ```js
     db.events.aggregate([
        {
          $search: {
            index: "eventSearchIndex",
            autocomplete: { // Autocompelete
              query: "roc",              // partial input
              path: "title",
              fuzzy: { maxEdits: 1 } // typo tolerace
            }
          }
        }
      ])
     ```

  3. Highlighting
     - Returns highlighted words that matches the query like
```js
db.events.aggregate([
  {
    $search: {
      index: "eventSearchIndex",
      text: {
        query: "music festival",
        path: "description",
        highlight: { path: "description" }
      }
    }
  }
])
```
```js
{
  "title": "Rock Music Festival 2025",
  "description": "Join us for a music festival in Mumbai this summer.",
  "highlights": [ // What exactly matched in the doc
    {
      "path": "description",
      "texts": ["music festival"]
    }
  ]
}
```


  4. Synonyms
     - They need to be defined at the time of index creation
     - example of defining synonyms

     ```js
      {
        "mappings": {
          "dynamic": false,
          "fields": {
            "description": {
              "type": "string",
              "analyzer": "lucene.standard",
              "synonyms": [
                {
                  "name": "carSynonyms",
                  "inputs": ["car", "automobile", "vehicle"]
                },
                {
                  "name": "bikeSynonyms",
                  "inputs": ["bike", "bicycle", "cycle"]
                }
              ]
            }
          }
        }
      }
      ```

      - Now when using the synonyms the match similar works that are present
        - Like in query if we define carSymptoms then while searching it also searchs for works in input provided at time of index creation
        - Eg:

      ```js
        db.events.aggregate([
          {
            $search: {
              index: "eventSearchIndex",
              text: {
                query: "car",
                path: "description",
                synonyms: "carSynonyms"
              }
            }
          }
        ])
      ```

- A combination of all

```js
db.events.aggregate([
  {
    $search: {
      index: "eventSearchIndex",
      text: {
        query: "festval",            // typo
        path: "title",
        fuzzy: { maxEdits: 1 },      // typo tolerance
        synonyms: "eventSynonyms",
        highlight: { path: "title" }
      }
    }
  }
])
```
