## Apache Hadoop
- Distributed computing environment
- Hadoop is designed for write-once and read many times
- It consisted of 3 main componenets to handle big data
  - [HDFS](./hadoop.md/#1-hdfs-hadoop-distributed-file-storage-system)
  - [MapReduce](./hadoop.md/#2-mapreduce-in-hadoop)
  - [YARN](./hadoop.md/#3-yarn-yet-another-resource-negotiator)

## 1. HDFS (Hadoop Distributed File Storage System)
  - Data stored across machines
  - It has two types of node
      - One `NameNode(Master Node)`
      - Multiple `DataNode(Slave Node)`
  - `NameNode` is responsible for namespace operations like opening, closing and renaming files and dirs
      - Stores meta data like file path, data nodes, replicas etc.
  - `DataNode` actual worker node, jobs include reading, writing, processing etc.
      - Also perfrom creation, deletion, and replication
      - Actually stores data
  - Whenever data comes to `NameNode` it is divided into chunks and then sent to `DataNode`
  - Why?
      - Ease of fetching data as multiple source of incoming data
  - `Replicas set`:
      - For each block replica sets are created to keep data fault tolerant
      - And the number of copies of data is called `replication factor` which is generally 3
      - replication factor can be modified and the info is stored in `NameNode`
  - `HeartBeat`
      - A signal that is continously sent from `DataNode` to `NameNode`
      - If `NameNode` does not recieve this signal then that `DataNode` is considered dead
  - `Balancing`
      - If a `DataNode` is dead then `NameNode` will signal `DataNodes` containing data of dead `DataNode` to replicate
  - Limitations
      - High Latency
          - Application often require low latency
          - But sometimes HDFS is not able to do that
          - As it as high throughput so it can compromise on Latency sometimes
      - Small File Problem
          - Having multiple small files can create problem
          - As for each file there will lots of movement in DataNodes
          - Hence it makes it inefficient  
  - ## `NameNode`
    - It maintains metadata in two files `FsImage` and `EditLogs`
    - **Fsimage (File System Image)**
      - A snapshot of HDFS at a give point in time
      - It contains
        - Files and Dir structure
        - Permissions
        - Block Locations
        - Replicaion details
    - **EditLogs**
      - Log record of all the changes since last FsImage checkpoint
  - ## `Secoundary NameNode` 
    - Merges `FsImage` and `EditLogs` at a fixed interval of time
    - This helps in preventing Editlogs to become very larger
    - > No longer used (discarded after hadoop 2.x)
    >  - ## `Checkpoint Node`
    >    - Same as `secoundary NameNode` 
    >    - Used for Non-HA(high Availability) Setup
    >    - Not widely used
    >  - ## `Backup NameNode`
    >    - A more advanced version of CheckpointNode but depricated.
    >    - Maintains an in-memory copy of the FsImage, reducing recovery time.
  - ## `Standby NameNode`
    - Part of `HDFS HA setup`
    - Maintains copy of NameNode's metadata and takes over immediately if active namenode fails
    - Uses shared storage system (like NFS or QJM - Quorum Journal Manager) to stay in sync
  - ## `Block size`
    - In HDFS files are divided into blocks and stored
    - default block size is 128MB, but can be altered
  - ## `Rack`
    - It is physical collection of DataNodes
    - A large cluster consist of multiple racks
  - ## `Rack awareness`
    - It is data placemenet stratergy
    - It improves `fault tolerance`, `data locality`, `network traffic optimisation` 
  - ## `Rack awareness policies`
    - There should not be more then 1 replica in same datanode
    - There should not be more then 2 replica in same rack
    - Number of racks should be less then number of DataNode
      - Number of racks should be significantly smaller then number of dataNode, but not necessary smaller then replicaiton factor
  - ## `Operations`
    - Block operations : Operation between NameNode and DataNode
    - Metadata operations : Operation between FS client and NameNode

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
  7.1 Once the above is done we perform `combiner` operations
        - Example: `(are, 1), (are, 1), (are, 1)` → `(are, [1, 1, 1])`  → `(are, 3)`
        - Next this data from multiple combiner is passed to reducer and the same is done 
  8. **Reducer Phase:**  
      After the mapping phase, the Reducer performs the following:
      - **Shuffling:** Groups identical keys.  
        - Example: `(are, 1), (are, 1), (are, 1)` → `(are, [1, 1, 1])`  
      - **Sorting:** Aggregates the values and formats the output.  
        - Example: `(are, [1, 1, 1])` → `(are, 3)`
  - ## Overall flow
    - Say these are files to be used
      ```sh
      File 1: "apple banana apple orange"
      File 2: "banana apple banana"
      ```
    - Two mapper maps the files

      | Mapper 1 Output (File 1) | Mapper 2 Output (File 2) |
      |----------------|----------------|
      | (apple, 1) | (banana, 1) |
      | (banana, 1) | (apple, 1) |
      | (apple, 1) | (banana, 1) |
      | (orange, 1) | (banana, 1) |

    - Combiner aggregates the files locally
   
      | Combiner 1 Output (Mapper 1) | Combiner 2 Output (Mapper 2) |
      |----------------|----------------|
      | (apple, 2) | (banana, 2) |
      | (banana, 1) | (apple, 1) |
      | (orange, 1) | (banana, 1) |

    - Partitioner : Assigns keys to each reducer

      | Key | Hash Function (`hash(key) % numReducers`) | Reducer Assigned |
      |------|----------------|----------------|
      | (apple, 2) | `hash(apple) % 2 = 0` | Reducer 1 |
      | (banana, 1) | `hash(banana) % 2 = 1` | Reducer 2 |
      | (banana, 2) | `hash(banana) % 2 = 1` | Reducer 2 |
      | (orange, 1) | `hash(orange) % 2 = 1` | Reducer 2 |

    - Reducer perfromes reduce
   
      | Reducer 1 Input | Reducer 2 Input |
      |-----------------|-----------------|
      | (apple, 2) | (banana, 1) |
      | (apple, 1) | (banana, 2) |
      | | (orange, 1) |

  - Final output

      | Key | Final Count |
      |------|------------|
      | apple | 3 |
      | banana | 3 |
      | orange | 1 |



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


## Managing huge load of small files in hadoop
1. Combining small files and making zip or tar out of it before storing
    - Overhead for layer abstraction
2. Using HAR(Hadoop Archine) : structure made for storing archine
    - Overhead for layer abstraction (but efficient)
3. External Metadata store
    - Using external database storage like HBase 