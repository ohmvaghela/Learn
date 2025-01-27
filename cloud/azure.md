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


---

Different Data source stories in cloud
ACID properties
types of nosql
types of cloud computing service IaaS, PaaS, SaaS
types of cloud like public, private, hybrid
Azure Cloud basics
Overview of azure service
azure resource group
azure storage
blob storage and blob storage access tier
types of blob : block, Append, Page
Hot, Cold, Archive tier in blob
File Storage
- Storage Queue
- Table Storage
Storage Performance tier
- Locally redundant storage (LRS)
- Zone Redundant Storage (ZRS)
- Geo-Redundant Storage (GRS)
- Read Access GeoRedundant Storage (RA-GRS)
Azure Storage Account types

Cosmos DB and its feature
Cosmos DB multimodel apis
Table storage v/s Cosmos DB
provision cosmos db account
hierarchy azure cosmos db account
azure cosmos containers
azure cosmos items
cosmos db sql api
creating and managing data
consistancy level in cosmos db
consistany, availability and performance tradeoffs
time to live feature
mongodb with cosmosdb

partitioning in cosmos db
- Physical partitioning
- Logical Partitioning
- hot partitioning
partitioning and horizontal scaling
request units
dedicated v/s shared throughput
single partitioning v/s cross partitioning
composite key / automatic indexing
table api in cosmos db
sql vs table api
distribute data globally
connecting to preferrd region
manual vs automatic failover
cosmos db germlin api
cosmos etcd api
  

parag parkeh flexi cap fund : Groww 
exit load : cost is related 
SIP 
small cap and mid cap : 
nifty index


legent and platinum rupay indusbank