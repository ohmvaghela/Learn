# Spark Stream

## Stream data
- Continous flow of data generated in real-time
- Unlike batch data, which is collected, processed in chunks and sent.
  - Stream data is sent in real time
- Characteristics
  - Continous
  - Real-time processing
  - Unbounded : No filed size
  - Time sensitive
  - Transient : Data is lost, unless explicitely stored
  - Hetrogeneous : Can come from any source and can be structured, semi-structured, unstructured

| Feature | Stream | Batch |
|---|---|---|
| Data | Continuous | Fixed-size |
| Processing Style | Real-Time (low latency) | Scheduled (high latency) |
| Data Size | Unbounded | Bounded |
| Data Handling | Event-by-event or micro-batch | Full dataset at once |
| Use Cases | Monitoring, alerting, ETL, real-time analytics | Historical analysis, reporting |
| Examples | Kafka streams, sensor data, social media feeds | Daily sales reports, data backups |


## StreamingContext
- Main entrypoint to Spark Streaming
- It creates DStream and manages exection of streaming computations

## DStream
- It is high level abstraction for streaming data in spark
- Internally, it is `sequence of RDDs over time`

## Streaming methods
1. Data Ingestion methods
    - Push-Based : Data is pushed into spark streaming. (Eg. Kafka)
    - Pull-Based : Spark Stream pulls data periodically (Eg. file, sockets)
2. Handling Late Date
    - Late data 
      - Event or record that arrvies after expected processing time 
        - This may be due to network delays or processing lags
      - To handle this we use `windowing`, `checkpointing`, and `watermarking` 

    - `Windowing`
      - allows aggregating data over sliding window 
      - Like say first 30-sec data is collected
        - then window is moved 10 sec and again data is collected
        - This creates an overlap
      
    - `watermarking`
      - It maintains a watermarking threshold.
        - if the data arrives late then watermarking threshold then it is discarded
        - Else the aggregation is updated with the new data

    - `Checkpointing` 
      - Storing the incomming data
      - The stored data can be metadata, or the actual data (which is stateful)
      - Old checkpoints are auto deleted when not required, i.e. aggregation is completed
      - It is done automatically by spark when required
      - when we are using watermarking or/and windowing then checkpoint is applied automatically
      - The time for which checkpointing is done is based on max(`watermarking threshold`, `windowing size`) 
      - Other way is using `persist()`, `cache()` to specifically mention checkpointing 

    - Windowing visualization

    ```
    Incoming Data: |--A--B--C--|--D--E--F--|--G--H--I--|--J--K--L--|--M--N--O--|
    Time (seconds):     0-10          10-20        20-30       30-40       40-50

    30s Window (0-30s): |--A--B--C--D--E--F--G--H--I--|
    30s Window (10-40s):         |--D--E--F--G--H--I--J--K--L--|
    30s Window (20-50s):                 |--G--H--I--J--K--L--M--N--O--|
    ```

## Structured v/s Unstructured Stream
- Unstructured stream : Data is treated as stream of raw bytes or lines of text
  - Data is stored as DStream
  - Used with StreamingContext
- Structured Stream : Treats data as stream of rows, with defined schema
  - Similar to DataFrame and DataSet
  - Used with SessionContext
## Structured Streaming : Triggers
- Defines how frequently a microbatch of data is processed
- Types :
  - Unspecified Trigger
    - By default
    - Processes data as fast as possible without any specific interval
  - Fixed interval (MicroBatches)
    - We can specify the interval after which batch is processed
    - `query = wordCounts.writeStream.trigger(processingTime='10 seconds').start()`
  - One-time trigger
    - Process data only once to complete the job 
    - `query = wordCounts.writeStream.trigger(once=True).start()`
## Stateful v/s Stateless operations
- Stateless operations
  - Where each microbatch is processed independently, **without info of past batches**
  - Eg. `map()`, `filter()`, `select()`, `groupBy()`
- Stateful operations
  - Operations which require the track of past batches
  - Eg. `Windowing`, `Count-based aggregations`, `Join-based operations` 

## Physical and Logical Plan in Spark
- When a spark job is recieved it is first converted to `logical plan` which is a high level description of operations
- Then Spark's `Catalyst Optimizer` generates `physical plans` which is actual execution stratergy

## Catalyst Optimizer
- It is query optimization framework using in **Spark SQL**
- Responisibilites : 
  - Logical Plan Optimization : 
    - Simplify query and remove redundancies
  - Rule based optimization : 
    - Rewrite query to use more efficient data processing stratergies
  - Cost-based Optimization : 
    - Choosing an optimal plan based on cost model
- Features
  - Predicate Pushdown :
    - Move filters closer to data source
  - Projection Pushdown : 
    - Moving select operation to reduce data volume in early phase

## Tungsten Engine
- Memory management : Allow spark to manage memory on its own
- Code generation : Generate specialized bytecode for operations like aggregations, reducing JVM overhead

## Handling Data Skew
- There are instances when the a partition may hold huge amount of data compared to other partitions
- To handle this spark use `Salting` and `Broadcast Join`
- ### Salting
  - On skewed partition, salting is done on primary key to distribute data efficiently
- ### Broadcase Join
  - When perfroming join operations we can broadcast a dataFrame which is small in size and also define a threshold 
  - Now this small dataFrame will be broadcasted to all the worker-nodes and hence join operations will be optimized
  - Ways to do broadcast Join

    ```py
    # Say we have 2 DataFrames "smallDF", "largeDF"
    result = largeDF.join(broadcast(smallDF), "id")
    result = largeDF.join(smallDF.hint("broadcast"), "id")
    ```