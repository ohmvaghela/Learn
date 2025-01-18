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
