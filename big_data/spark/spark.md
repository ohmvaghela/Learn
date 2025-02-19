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


## RDD (Resilient Distributed Dataset)
- Fundamental Data Structure of Spark
- Before Spark, Hadoop MapReduce required manual handling of distributed data using disk-based storage
- RDDs simplified distributed computing by providing an immutable, fault-tolerant, and parallel data structure.

