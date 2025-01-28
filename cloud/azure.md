# Azure

## Types of data store in Azure
- `Azure Blob Store` for unstructured data like images, video, audio etc
- `Azure SQL database`
- `Azure Cosmos Database` for NoSQL data
- `Azure Datalake` for storing large scale analytics data
- `Azure table storage` for key value pair storage
- `Azure event hub` for real time data streaming
- `Azure data factory` for collecing and managing data from different source

> ### Name 4 types of NoSQL database

## Azure Services
- Azure VMs
- Azure Service Fabrics
  - Helps to build and manage microservices and data applications
- Azure Functions
  - Serverless computing

## Azure Networking Services
- Azure CND
- Azure Express Route : Connects on-premise to azure
- Azure DNS 
- Azure Virtual Networks : Just like AWS virtual networks
- Azure Traffic Manager : Reverse proxy, failure recovery
- Azure Load balancer
- Azure VPN gateway

## Azure Storage Services

| Service | Info |
|-|-|
| `Azure Blob Store` | for unstructured data like images, video, audio etc |
| `Azure SQL database` | |
| `Azure Cosmos Database` | for NoSQL data |
| `Azure Datalake` / `Azure Data Store` | for storing large scale analytics data |
| `Azure table storage` | for key value pair storage |
| `Azure event hub` | for real time data streaming |
| `Azure data factory` | for collecing and managing data from different source |
| `Azure Queue Storage` | For storing large number of messages |
| `Azure File Storage` |  |

## Azure Container Services
- Azure Container Instance : Run containers directly in the cloud without managing servers
- Azure App Service (with Containers)
- Azure Functions (with Container Support)
- Azure Container Registry (ACR)
- Azure Kubernetes Service (AKS)

## Azure Resource Group
- A logical container that hold resources for a perticular project.
- Resources include
  - VMs
  - DBs
  - webapps
  - ...

## Types of Blobs in Azure Blob Storage
- Block Blob
  - Stores binary and text data
  - Good for file, docs, and other large objects
- Append Blob
  - Good for adding data 
  - Good for logs and real time data
- Page Blob
  - Good for random read and write
  - Used for virtual Hard Disk (VHD)

## Blob Storage Access Tiers
- Hot Tier : Frequently accessed data stored here
- Cold Tire : Backups stored here
- Archive Tier : Long-term archival of regulatory or compliance data

## Azure Storage

> ### Server Message Block(SMB) : Protocal that allows file sharing over the network
> - Mainly developed for windows
> - And in azure it is used for data/files sharing over azure server and other on-premise server

1. ### Azure File Storage
    - Fully managed file share and storage
    - Data can be accessed via SMB and NFS

2. ### Azure Queue Storage
    - Used for storing large number of messages
    - Accessed via REST APIs, SDKs.
3. ### Azure Table Store
    - Used for storing key-value pairs
    - Accessed via REST APIs, SDKs.


## Cosmos DB structure  
- Used to store NoSQL data
- We can SQL queries on NoSQL data using the SQL APIs (Not on all)
- The hierarchy
  - Account
  - Database : Has collection of containers
  - Container : Physically data is stored here
  - Items : The data
- The data is pratitioned and stored
- One partition key is defined and logical partitioning is done in container
- Then while storing the data it is Physically pratitioned and stored in container

## Consistancy spectrum in cosmos db
1. Strong consistancy
    - Client always read most recent commited state
    - High latency, Reduced availability in multi region setup

2. Bounded Staleness
    - Client read data within specific time-lag or version lag which is pre-defined
    - Lower latancy then Storng consistancy
    - Less consistancy then Storng consistancy

3. Session Consistancy (Default)
    - Ensures read-your-own-writes
    - Whatever changes are made by clients are visible to clients
    - Like shopping cart at amazon
    - Lower latancy then Bounded Staleness
    - Less consistancy then Bounded Staleness

4. Consistant Prefix Consistancy
    - Read never see out-of-order writes (changes are made in batch)
    - Like say 4 viewers are viewing a doc with one writer
    - So if writer writes 3 batches say batch a,b,c
    - Then batches are reflected one after another and in same order
    - These are handled like transactions
    - Lower latancy then Bounded Staleness
    - Less consistancy then Bounded Staleness

5. Eventual Consistancy
    - No guarantees on order or freshness of reads
    - writes eventually propagate to all replicas.
    - Eg. Social media likes, shares, postes

## Server Side programming and data management tools
1. Item
    - Single Record in a container
    - Each record has PK
    - Partitioning based on Partition key hence easily scalable
2. Triggers
    - JS code that runs before or after any CRUD operations
    - Per-Triggers : Before operations, like varidation before saving data
    - Post-Triggers : After operations, like notification and logs after saving data 
3. UDF (User Defined Functions)
    - A custom JS function that can be used in queries to perform complex operations
    - Eg. 

      ```JS
      function formatName(name) {
        return name.toUpperCase();
      }
      ```
      ```sql
      SELECT udf.formatName(c.name) FROM c
      ```

4. Stored Procedures
    - JS code that runs atomically(as transaction) on server
    - Used for perfroming custom operations on db that requires transactionality


## Request units (RU)
- Cost of a database operation in terms of computation, cpu memory, I/O resources
- It is used as currency for dbs operations
- 1 RU is required to read 1KB document container with default settings
- Other operations like query, writes require more RU

## Azure Table store v/s Cosmos DB table API
- Cosmos DB table API is same as Azure table store 
  - like both store key value pair
- But Cosmos DB offers more advanced features
  - Global distribution available
  - In Azure Table store there is indexing on partition key and row key
  - While in cosmos db it is auto indexing on all fields by default
  - Azure Table store only offer strong consistancy while cosmos offer all 5 levels

## Time to live 
- Cosmos auto delete data after certain time, helping manage storage

## Partitioning 
- Logical Partitioning
  - Logical grouping of data container
- Physical Partitioning
  - Storing data on different servers
- Hot Partitioning
  - happens when a few logical partition recieves relatively high amount of data compared to other partitions
  - If not done then there will be imbalance in data stored 

## Dedicated v/s Shared throughput
- Dedicated
  - A defined amount of RU/s is dedicated to a single container
- Shared 
  - A defined amount of RU/s is dedicated to multiple container
   
## Partitioning
- Single Partitioning
  - All the data is stored in single partition
  - Fast read and write 
  - Limit 50 GB data + 10,000 RU/s
- Cross partition
  - Data stored accross multiple partition
  - Slower read-write when queries involve multiple partitions

## Composite key Partitioning
- Multiple fields combined for indexing



---


sql vs table api
distribute data globally
connecting to preferrd region
manual vs automatic failover
cosmos db germlin api
cosmos etcd api
  
