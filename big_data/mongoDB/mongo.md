# MongoDB

## Context
- [Base](./mongo.md#base)
- [Storage Engine](./mongo.md#storage-engine)

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


