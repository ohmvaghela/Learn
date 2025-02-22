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
      

    - `Checkpointing` 
      - Storing the incomming data
      - The stored data can be metadata, or the actual data (which is stateful)
      - Old checkpoints are auto deleted when not required, i.e. aggregation is completed

    - `watermarking`
      - It maintains a watermarking threshold.
        - if the data arrives late then watermarking threshold then it is discarded
        - Else the aggregation is updated with the new data

    - Windowing visualization

    ```
    Incoming Data: |--A--B--C--|--D--E--F--|--G--H--I--|--J--K--L--|--M--N--O--|
    Time (seconds):     0-10          10-20        20-30       30-40       40-50

    30s Window (0-30s): |--A--B--C--D--E--F--G--H--I--|
    30s Window (10-40s):         |--D--E--F--G--H--I--J--K--L--|
    30s Window (20-50s):                 |--G--H--I--J--K--L--M--N--O--|
    ```
