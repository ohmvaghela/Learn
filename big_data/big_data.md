# Big Data

## Types of data 
1. Structured data
    - Organized data with predefined format
    - Stored in RDBMS
2. Semi Structured data
     - Partially organized data
     - Lacks in rigid schema
     - Formats like .csv, .json, .xml etc...
3. Unstructured data
     - Data that does not have structure
     - Apache spark and hadoop like advanced tools are used to handle them
     - Data can be image, video, text, raw docs etc

## big data
- Data set that are extremely large and cannot be managed by traditional database because
1. Too Big
  - Data set in massive size, like TB, PB or even EB
  - Can be sensor data, stock data etc.
2. Too unstructured
  - Majority of big data is semi or unstructured so difficult to handle
  - Social media videos, satlite images
3. Too continous
  - Real time data like streaming performance, stock market data etc.

## Data based on its characteristics
1. Big data
2. Fast data
3. Infinite data

## Data based on how it is produced
1. Batch processing data : High amount of static data to be produced
2. Interactive data (Online data) processing
   - Online transactions data like hotel booking, banking transaction
3. Streaming data processing
  - Near real time data processing
  - Like live stream performance, sensor data

## Big Data process
1. Store
     - Storing vast amount of data
     - The storage should be able to handle 3Vs
       - Volume
       - Variety
       - Velocity
     - Solutions
       - HDFS (Hadoop Distributed File system)
       - Cloud storage : S3, GCS
       - NoSQL db like : MongoDB, cassendra
2. Process
     - Raw data needs to be cleaned, formated and aggregated
     - Parallel processing to handle huge load of data
     - Solutions
       - Hadoop MapReduce
       - Apache Spark
       - Apache kafka, Apache flink
3. Scale
     - Server should have
       - Horizontal scalling capability
       - Elastic scaling capability
       - High availability 

## Ways of scaling 
1. Veritcal scaling / Scaling up
2. Horizontal scaling / Scaling out / Sharding 

## Ways to build system
1. Single monolithic server
2. Distributed cluser of nodes 

## Apache Hadoop
- Distributed computing environment
- Hadoop is designed for write-once and read many times
- It consisted of 3 main componenets to handle big data
  1. HDFS (Hadoop Distributed File Storage System)
       - Data stored across machines
       - It has two types of node
           - One NameNode(Master Node)
           - Multiple DataNode(Slave Node)
       - NameNode is responsible for namespace operations like opening, closing and renaming files and dirs
           - Stores meta data like file path, data nodes, replicas etc.
       - DataNode actual worker node, jobs include reading, writing, processing etc.
           - Also perfrom creation, deletion, and replication
           - Actually stores data
       - Whenever data comes to NameNode it is divided into chunks and then sent to DataNode
       - Why?
           - Ease of fetching data as multiple source of incoming data
       - Replicas set:
           - For each block replica sets are created to keep data fault tolerant
           - And the number of copies of data is called `replication factor` which is generally 3
           - replication factor can be modified and the info is stored in NameNode
       - `HeartBeat`
           - A signal that is continously sent from DataNode to NameNode
           - If NameNode does not recieve this signal then that DataNode is considered dead
       - `Balancing`
           - If a DataNode is dead then NameNode will signal DataNodes containing data of dead DataNode to replicate
       - Limitations
           - Low Latency
               - Application often require low latency
               - But sometimes HDFS is not able to do that
               - As it as high throughput so it can compromise on Latency sometimes
           - Small File Problem
               - Having multiple small files can create problem
               - As for each file there will lots of movement in DataNodes
               - Hence it makes it inefficient  
  2. MapReduce
  3. YARN

- Advantage of 3x replication schema in HDFS
    - Ensures fault tolerance

---------------------------------------------------
- Apache Hadoop theory
- Core componenets of apache hadoop
  - HDFS (File system)
  - MapReduce (Programming Model)
  - How Hadoop Storead data
- Big Data Hadoop Processing
  - Distributed computing
    - MapReduce
    - Spark
  - Resource Management
    - Spark Standalone
    - YARN
    - Mesos
  - Distributed Storage
    - HDFS
    - Cassandra
    - AWS S3
- Apache Spark thoery
- Data Lake
  - Data Architecture
- CAP Theorm
- NoSQL dbs
