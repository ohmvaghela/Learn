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
- [Search Index configuration](./mongo.md#search-index-configuration)
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

## Atlas Search Nuances
- At the time of index creation they are CPU / memory / I/O intensive, but run on seperate 


## Search Indices
- Mongo has two main processes
  - `mongod` and `mongot`
- `mongod` is main deamon that runs mongo
- `mongot` is sidecar, only called for `Atlas Search` or `Vector Search` queries i.e. `$search`
- each replica of `mongod` has a `mongot` for `Co-Located Architecture`
- And `mongot` updates using the `oplogs`(operation logs)
- If `mongot` fails it wont affect `mongod` as `mongot` is not critial process
  - so all search queries will throw error
- Updates in `mongot`
  - A working set is stored in-memory
  - Full data is stored in storage
    - Full Lucene index is persisted on the same storage as mongod data files (so disk is shared). Segments are written in compressed form.
- Both mongod and mongot have different RAM but same storage, so queries on mongot wont affect mongod
	- Though both have same VM but, dymanic schedular have prevents `mongot` from throttling `mongod`


### Apache Lucence
- Search engine toolkit written in java
- `mongot` uses it for seach index storage
- It does primarily these things
  - Tokenize the searchable fields set for indexing
    - Eg. "Ohm is software engineer" to "Ohm", "is", "software", "engineer"
  - Each tokenized work is mapped to docuemnt_ids
  - When a search query is called `mongot` ties to map most relevent doc using algos like `TD-IDS`, `BM25`...

### Index building
- normal index at `mongod` :
	- Foreground : stop read-write on a collection's cluster and create index
 		- Has downtime
	- Background : concurrent, slow, but without any downtime
- Search index on `mongot`:
	- Foreground : `mongot` will use extensive CPU, can lead to downtime <u> `rarely used` </u>
	- Background : Incremental, rolling build. less CPU throttling, wont result in downtime <u> `mostly used in production` </u>
 		- Only metadata lock at start/end. Uses RAM throttling.
   - In both the case can hold lock on metadata: so new field insertion should be avoided
- `Falls off the oplog` while builing
	- mongot scans the entire document and for the recent changes it depends on `oplogs`
 	- So once the collection is scanned the `oplogs` are scanned for new entires
  	- Now `oplogs` have a limitation
  		- oplogs have limit on size of logs it can store, and write rate Eg:
  	 	- Say Oplog size: 20 GB and Write rate: 5 GB/hour
  	  	- So max oplog covers roughly 4 hours of history in worst case
  	- So assmune `mongot` performed a full db scan and then it finds oplogs have some missing records then it throws error and starts entire process again
- Another pain point: Horizontal scalling
	- `Adding a shard to an already sharded collection`
 	- Mongodb performs initial sync on db for craetion of replica set
  	- New shard creates it own `mongot` luance from scratch
  	- So Until this `initial sync`(mongod replica) + `index build`(mongot) is complete,
  		- `$search` queries across the collection may fail — because the new shard doesn’t yet have a valid Search index.


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

#### 1. Search Natural Language Text

* Searches on Lucene's tokenized words.
* Performs **Stemming**: reduces words to their root form (`“running” → “run”`).
* **Stop words removal**: ignores common words like “the”, “in”, “of”.
* Returns a **relevance-based ranking** score along with the results.

**Example of a basic query:**

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

**Sample results:**

```json
[
  { "title": "Music Festival 2025", "score": 9.1 },
  { "title": "Rock Music Festival", "score": 7.3 },
  { "title": "Summer Concert", "score": 3.5 }
]
```

---

### 2. Other Features

#### a. Fuzzy

* Allows misspells up to a certain limit.

**Example:**

```js
db.events.aggregate([
  {
    $search: {
      index: "eventSearchIndex",
      text: {
        query: "festval", // typo
        path: "title",
        fuzzy: { maxEdits: 1 } // typo tolerance
      }
    }
  }
])
```

#### b. Autocomplete

* Returns suggested completions for partial words.

```js
db.events.aggregate([
  {
    $search: {
      index: "eventSearchIndex",
      autocomplete: {
        query: "roc",          // partial input
        path: "title",
        fuzzy: { maxEdits: 1 } // typo tolerance
      }
    }
  }
])
```

#### c. Highlighting

* Returns the exact parts of the text that match the query.

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

**Sample result:**

```js
{
  "title": "Rock Music Festival 2025",
  "description": "Join us for a music festival in Mumbai this summer.",
  "highlights": [
    {
      "path": "description",
      "texts": ["music festival"]
    }
  ]
}
```

#### d. Synonyms

* Must be defined at index creation.
* Example mapping with synonyms:

```js
{
  "mappings": {
    "dynamic": false,
    "fields": {
      "description": {
        "type": "string",
        "analyzer": "lucene.standard",
        "synonyms": [
          { "name": "carSynonyms", "inputs": ["car", "automobile", "vehicle"] },
          { "name": "bikeSynonyms", "inputs": ["bike", "bicycle", "cycle"] }
        ]
      }
    }
  }
}
```

**Query using synonyms:**

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

#### e. Combining Features

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

#### f. Compound

* Supports combining multiple criteria:

  * `must`: required
  * `should`: optional, boosts score
  * `mustNot`: exclude
  * `filter`: required, doesn’t affect score (e.g., range filters)

```js
db.products.aggregate([
  {
    $search: {
      compound: {
        must: [{ text: { query: "laptop", path: "title" } }],
        should: [{ text: { query: "gaming", path: "description", score: { boost: { value: 2 } } } }],
        mustNot: [{ text: { query: "refurbished", path: "condition" } }],
        filter: [{ range: { path: "price", gte: 500, lte: 2000 } }]
      }
    }
  }
])
```

#### g. Range

* Search text within numeric or date ranges.

```js
db.events.aggregate([
  {
    $search: {
      range: {
        path: "event_date",
        gte: ISODate("2024-01-01"),
        lte: ISODate("2025-12-31")
      }
    }
  }
])
```

#### h. Phrase

* Matches exact word order.

```js
db.articles.aggregate([
  {
    $search: {
      phrase: {
        query: "climate change impact",
        path: "content"
      }
    }
  }
])
```


### Search Meta (`$SearchMeta`)
- Returns metadata about search rather then the docs it self like aggregation
- We use them when we need 
  - Counts
  - Facets (groupings)
  - Explain plans
  - Highlight previews (without fetching all docs)
- Eg:
  #### Count
  - number of doc that matches the name `Rock Concert`

    ```js
    db.events.aggregate([
      {
        $searchMeta: {
          index: "eventSearchIndex",
          text: {
            query: "rock concert",
            path: ["title", "description"]
          }
        }
      }
    ])
    ```
    ```
    {
      "count": { "lowerBound": 25 }
    }
    ```

  #### Faceted Search 
  - `Count` + `Grouping` like group by in search indices
  - Eg: Number of docs that match `name` field `concert` and group by `city` and `start_date`

    ```js
    db.events.aggregate([
      {
        $searchMeta: {
          index: "eventSearchIndex",
          facet: {
            operator: {
              text: { query: "concert", path: "description" }
            },
            facets: {
              cityFacet: { type: "string", path: "city" },
              dateFacet: { type: "date", path: "start_date" }
            }
          }
        }
      }
    ])
    ```
  - Highlight preview
    - Preview snippets from matching documents showing exactly where the query terms appear.

    ```js
    db.events.aggregate([
      {
        $searchMeta: {
          index: "eventSearchIndex",
          facet: {
            operator: { text: { query: "concert", path: "description" } },
            facets: {
              cityFacet: { type: "string", path: "city" },
              dateFacet: { type: "date", path: "start_date" }
            }
          },
          highlight: { path: "description" }
        }
      }
    ])
    ```
    ```json
    {
      "highlight": [
        { "description": ["...the <em>concert</em> was amazing..."] },
        { "description": ["...live <em>concert</em> in Mumbai..."] }
      ]
    }
    ```

  > #### Explain plans
  > To see why and how results are generated, use `explain` flag to `true`
  > It provides:
  > - How documents were scored.
  > - How facets/buckets were computed.
  > - How queries are optimized internally.
  > Eg:
  > ```js
  > db.events.aggregate([
  >   {
  >     $searchMeta: {
  >       index: "eventSearchIndex",
  >       facet: {
  >         operator: { text: { query: "concert", path: "description" } },
  >         facets: {
  >           cityFacet: { type: "string", path: "city" },
  >           dateFacet: { type: "date", path: "start_date" }
  >         }
  >       },
  >       explain: true
  >     }
  >   }
  > ])
  > ```

<details>
<summary>Facet Results & Explain Details</summary>

	> ```json
	> {
	>   "facet": {
	>     "cityFacet": {
	>       "buckets": [
	>         { "_id": "Mumbai", "count": 20 },
	>         { "_id": "Delhi", "count": 15 },
	>         { "_id": "Bangalore", "count": 10 }
	>       ]
	>     },
	>     "dateFacet": {
	>       "buckets": [
	>         { "_id": "2025-01-01T00:00:00Z", "count": 5 },
	>         { "_id": "2025-01-05T00:00:00Z", "count": 8 },
	>         { "_id": "2025-01-10T00:00:00Z", "count": 12 }
	>       ]
	>     }
	>   },
	>   "explain": {
	>     "index": "eventSearchIndex",
	>     "parsedQuery": {
	>       "type": "text",
	>       "path": ["description"],
	>       "query": "concert"
	>     },
	>     "scoreDetails": {
	>       "totalDocsMatched": 45,
	>       "BM25Components": [
	>         {
	>           "field": "description",
	>           "term": "concert",
	>           "idf": 1.5,
	>           "tf": 1,
	>           "scoreContribution": 1.5
	>         }
	>       ]
	>     },
	>     "facetsEvaluated": {
	>       "cityFacet": "using string buckets on field 'city'",
	>       "dateFacet": "using date buckets on field 'start_date'"
	>     },
	>     "indexTraversal": "Lucene inverted index traversal info here"
	>   }
	> }
	> ```

</details>

## Search Index configuration
- Configuration options
	- Dynamic mapping : bool
 		- Auto-index new fields with default index type
   		- Discouraged
	- Stored Source Fields: array
 		- Name of the fields to store in raw form
   		- Say I have Stored field `description` then with _id, full `description` field will be stored so no fetching is required
	- type of index
 		- `autocomplete`, `string`, `text`, `date`, `number`
   	- minGram, maxGram (only autocompelete supports it)
   		- Affects tokenization: how to tokenize
   	 	- Say minGram is 3 and maxGram is 10
   	  	- So it will tokenize all substrings from length 3-10 and not more then 10
- Eg:

```json
{
  "mappings": {
    "dynamic": true,   // automatically index new fields if not explicitly defined
    "fields": {
      "name": {
        "type": "autocomplete",
        "minGrams": 2,
        "maxGrams": 5,
        "foldDiacritics": true,
        "analyzer": "lucene.standard"
      },
      "description": {
        "type": "text",
        "analyzer": "lucene.standard",
        "searchAnalyzer": "lucene.standard",
        "foldDiacritics": false
      },
      "tags": {
        "type": "string"   // exact match for filters
      },
      "price": {
        "type": "number"   // numeric range queries
      },
      "available": {
        "type": "boolean"
      },
      "createdAt": {
        "type": "date"
      },
      "location": {
        "type": "geo",
        "coordinates": { "lat": "latField", "lon": "lonField" }
      },
      "metadata": {
        "type": "object",
        "dynamic": true, // nested object auto-indexed
        "fields": {
          "color": { "type": "string" },
          "size": { "type": "string" }
        }
      }
    }
  },
  "storedSourceFields": ["name", "description", "price"],  // store original values for retrieval
  "synonyms": [
    {
      "name": "car_synonyms",
      "synonyms": [
        { "input": ["car", "automobile"], "synonymFor": "vehicle" },
        { "input": ["bike", "bicycle"], "synonymFor": "cycle" }
      ]
    }
  ]
}
```

