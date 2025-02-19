# Spark
- It is `open source` + `in-memory` + `Distributed` `computing system`
- Used for big data processing + big data analytics
- Used for data with high volume and velocity
- It was created to overcome limitations of `Hadoop MapReduce`
- Reason for high-speed : Uses `in-memeory storage` + `DAG`

> ### DAG in Spark
> - Unlike mapReduce which process one task at a time, it can process muliple tasks
> - Also unlike mapReduce it can avoid data storing for intermediate steps
> - Say we have log file and we want to calculate count of each type of error
>   - Then in Spark we can perfrom `mapping` + `reducing` across multiple nodes and then aggregate it as stage 1
>   - After that we can pass it to next step
>   - If it fails then it can revert to last step and perfrom execution again 

| Feature               | Hadoop MapReduce          | Apache Spark                              |
|-----------------------|---------------------------|------------------------------------------|
| Processing Model      | Disk-based                | In-memory                                |
| Execution Speed       | Slower (due to disk I/O)  | 100x faster for in-memory, 10x for disk-based |
| Ease of Use           | Complex Java-based API    | Easy-to-use APIs (Scala, Python, Java, R) |
| Real-time Processing  | No                        | Yes (Spark Streaming)                    |
| Iterative Processing  | No                        | Yes (Used in ML, Graph Processing)       |

## Spark Supports
- Batch Processing (like hadoop mapreduce but much faster)
- Real in-time stream processing
- SQL-Based Queries
- Machine Learning
- Graph Processing

## Spark Componenets
1. Spark Core
2. Spark SQL
    > ## - Uses Catalyst Optimizer for efficient query execution?
3. Spark Streaming
    > ## - Uses micro-batches for near real-time computation
4. MLlib
5. GraphX

## Spark Architecture
> ## Add image of spark architecture and replace these
![image](https://github.com/user-attachments/assets/51e00a97-c148-4b03-8c67-15937ef1ab73)
![image](https://github.com/user-attachments/assets/cd2e0115-a383-421f-abb4-9263f70fa2c3)

- Parts of Spark Architecture
    - Driver Program (In the master node) : Coordinates execution
    - WorkerNode (Executor) : Performs computation
    - Cluster Manager : Manages resources allocation
- Driver Program
    - Entry point in spark
    - Converts user defined logic like RRD transforamtions and actions into DAG
    - Schedules tasks for execution for executor node
    - Responsibilities
        - Job Submission
        - DAG construction
        - Task scheduling : Breaks DAG into stages and tasks
        - Monitering Execution
        - Communication with executor
- Executors
    - Runs on worker node
    - Stores RRD cache for reuse
    - Returns output to driver
- Cluster manager
    - Responsibe for allocating resources (CPU and Memory)
    - Not responsible for running task
    - Launches worker node, and moniter for its health
 
## Spark execution process
- User submits spark application
- `Driver manager` initializes itself and connects with `cluster manager`
- DAG is created out of transformation and activities
- DAG is divided into to stages and tasks
- `Task executor` executes the tasks parallely and stores intermediate results
- Result is sorted and shuffeled if necessary
- Result is collected and returned to `Driver manager` 


## RDD (Resilient Distributed Dataset)
- Fundamental Data Structure of Spark
- Before Spark, Hadoop MapReduce required manual handling of distributed data using disk-based storage
- Properties
    - Resilient : Fault tolerant, if RDD fails it recomputes from lineage
    - Partitioned/ Distributed : RDDs are splited into multiple partitions and processed parallely
    - Immutable : Once created cannot be changed. Instead transformations are applied and new RDDs are created
    - Lazy execution : Only computed when action is triggered
- 3 primary ways of creating RDD
    - Parallelizing an existing collection
      - `rdd = spark.sparkContext.parallelize([1, 2, 3, 4, 5])`
  - Reading from external like HDFS, S3, file
      - `rdd = spark.sparkContext.textFile("hdfs://path/to/file.txt")`
  - Transforming existing RDD
      - `rdd2 = rdd.map(lambda x: x * 2)`

 # Last chat is left 

