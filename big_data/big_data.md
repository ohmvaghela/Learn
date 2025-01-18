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
  ## 1. HDFS (Hadoop Distributed File Storage System)
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
    ## 2. MapReduce in Hadoop
    
    **Overview:**  
    MapReduce is used when a file is fetched, or a query/business logic needs to be executed. It handles the **read operations** (not write) and is ideal for processing large datasets.
    
    ### Workflow:
    1. **Client Request Example:**  
       - For instance, a client requests the word count of each word in a text file.
    
    2. **Job Tracker:**
       - A **Job Tracker** is created to monitor and track the progress of the task.
       - The Job Tracker requests metadata from the **NameNode**.
    
    3. **Splits and DataNodes:**
       - The number of splits corresponds to the number of **DataNodes** storing the data.  
       - Each DataNode is referred to as a **split** in the MapReduce process.
    
    4. **Mappers:**
       - Mappers are created in a quantity equal to the number of splits.
       - Each Mapper operates on a split (i.e., a single DataNode).
    
    5. **Task Tracker:**
       - A **Task Tracker** (a slave process of the Job Tracker) manages all operations on a DataNode.  
       - Task Trackers send a heartbeat signal at fixed intervals to the Job Tracker.  
       - If a Task Tracker fails to send a heartbeat, it is marked as dead, and a new Task Tracker is created to process the task on another replica of the DataNode.
    
    6. **Record Reader:**
       - In each DataNode, the **Record Reader** processes data to generate basic key-value pairs.
       - **Key:** Byte offset of the record.  
       - **Value:** The actual data.  
       - **Example:**  
         If a file contains:  
         ```
         Hello I am GeeksforGeeks  
         How can I help you
         ```
         The Record Reader outputs:  
         - `(0, "Hello I am GeeksforGeeks")`  
         - `(26, "How can I help you")`  
    
    7. **Mapper Logic:**
       - The Mapper applies business logic to the data and generates key-value pairs.  
       - **Example for word count:**  
         ```
         Input: (0, "Hello I am GeeksforGeeks")  
         Output: (Hello, 1), (I, 1), (am, 1), (GeeksforGeeks, 1)
         ```  
         ```
         Input: (26, "How can I help you")  
         Output: (How, 1), (can, 1), (I, 1), (help, 1), (you, 1)
         ```
    
    8. **Reducer Phase:**  
       After the mapping phase, the Reducer performs the following:
       - **Shuffling:** Groups identical keys.  
         - Example: `(are, 1), (are, 1), (are, 1)` → `(are, [1, 1, 1])`  
       - **Sorting:** Aggregates the values and formats the output.  
         - Example: `(are, [1, 1, 1])` → `(are, 3)`

    # 3. YARN (Yet Another Resource Negotiator)
    
    ## Parts of YARN
    
        1. **Resource Manager (RM):**
           - Manages allocation of resources across the cluster.
           - Acts as a single point of contact for the client.
           - Allocates the first container to launch the **Application Master** (AM) on a suitable node.
        
        2. **Node Manager (NM):**
           - Manages resources and containers on its specific node.
           - Reports the node's status (e.g., resource availability, health) and sends heartbeats to the Resource Manager.
           - Responsible for launching and shutting down containers on its node.
        
        3. **Application Master (AM):**
           - Manages the execution of the application (e.g., MapReduce job or Spark job).
           - Requests additional containers/resources from the Resource Manager based on the application's requirements.
           - Coordinates the execution of tasks in the allocated containers and tracks their progress.
           - Reports the application status to the client through the Resource Manager.
        
        4. **Container:**
           - A unit of resource (CPU, memory) allocated by the Resource Manager to run a specific task.
           - Containers execute tasks as requested by the Application Master.
        
        
    ## Working of YARN
        
        1. **Client Request:**
           - The client submits a job (e.g., a MapReduce job or Spark script) to the Resource Manager.
        
        2. **Resource Manager Allocates AM:**
           - The Resource Manager selects a **Node Manager** with sufficient resources and schedules the **Application Master** by allocating the first container for it.
        
        3. **Node Manager Launches AM:**
           - The selected Node Manager launches the Application Master in the allocated container.
        
        4. **Application Master Requests Resources:**
           - Once running, the Application Master calculates the resource requirements for the application and requests additional containers from the Resource Manager.
        
        5. **Resource Negotiation:**
           - The Resource Manager negotiates and allocates resources (containers) to the Application Master based on cluster availability.
        
        6. **Containers Initialized:**
           - The allocated containers are initialized and managed by the respective Node Managers on the nodes where they are hosted.
        
        7. **Task Execution:**
           - The Application Master orchestrates the execution of tasks within the allocated containers and monitors their progress.
        
        8. **Completion and Reporting:**
           - After the application's tasks are completed, the Application Master reports the status back to the Resource Manager.
           - The Node Managers also report the status of their containers to the Resource Manager.
           - The Resource Manager sends the final status or output back to the client.
        
    
    ## FAQs
    
        1. **Can Application Master and Containers Be on Different Nodes?**
           - Yes, the Application Master and its allocated containers can run on different nodes. YARN dynamically allocates containers across the cluster based on resource availability.
        
        2. **Is Node Manager Responsible for Scheduling the Application Master?**
           - No, **the Resource Manager schedules the Application Master** and selects the Node Manager to host it. The Node Manager simply launches the Application Master on its node after receiving instructions from the Resource Manager.


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
