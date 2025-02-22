# Spark Stream Practical

## StreamingContext initialization and stopping
- `Batch interval`: is the time interval for the data is collected and batched together for processing

```py
from pyspark import SparkConf, SparkContext
from pyspark.streaming import StreamingContext

conf = SparkConf().setMaster("local[2]").setAppName("MyStreamingApp")
sc = SparkContext(conf = conf)

# Batch interval of 1 sec
ssc = StreamingContext(sc, 1) 
```

- Starting StreamingContext

```py
ssc.start()
```

- Stopping StreamingContext

```py
# Wait over here indefinately
ssc.awaitTermination() 
# Wait for 60 max
ssc.awaitTerminationOrTimeout(60) 
# If you want to stop execution
scc.stop(stopSparkContext= True, stopGracefully=True) 
```

## Creating DStream
- From Socket Source

  ```py
  lines_DStream = ssc.socketTextStream("localhost",9999)
  ```

- From files in a dir
  - If a new file is created then it processes it's content/data

  ```py
  lines = ssc.textFileStream("file:///path/to/dir")
  ```

- From kafka
  - **Yet to read**

## DStream Output Operations
- Print first 10 elements of each RDD

  ```py
  lines.pprint()
  ```

- Save DStream as text file

  ```py
  lines.saveAsTextFiles("fie:///path/to/file", "txt")
  ```

- Custom action on each RDD

  ```py
  lines.foreachRDD(lambda rdd: rdd.foreach(print))
  ```

## Example word count code

```py
from pyspark import SparkContext
from pyspark.streaming import StreamingContext

sc = SparkContext("local[2]", "StreamWordCountExample")
ssc = StreamingContext(sc,1) # 1 : Time internal for batching

# Connect to port 9999
lines = scc.socketTextStream("localhost",9999)

# RDD transformations
words = lines.flatMap(lambda x : x.split(" "))
word_count = words.map(lambda x : (x,1)).reduceByKey(lambda a,b : a+b)

word_count.pprint()

ssc.start()
ssc.awaitTermination()
```

## Stopping stream

1. Using File System Check
    - Define `TERMINATION_PATH` if the file exist then stop the execution
    - Define a functio to check if the file exist

    ```py
    import os
    import sys

    def check_termination():
        if os.path.exists(TERMINATION_FILE):
            print("Termination file detected. Stopping gracefully...")
            ssc.stop(stopSparkContext=True, stopGracefully=True)
            sys.exit(0)
    ```

    - Add this to every iteration 
    
    ```py
    from pyspark import SparkContext
    from pyspark.streaming import StreamingContext

    sc = SparkContext("local[2]", "NetworkWordCount")
    ssc = StreamingContext(sc, 5)

    lines = ssc.socketTextStream("localhost", 9999)
    lines.foreachRDD(lambda rdd: check_termination())

    ##################################
    # Other transformation and print #
    ##################################

    ssc.start()

    try:
        ssc.awaitTermination()
    except KeyboardInterrupt:
        print("Interrupted by user. Stopping...")
        ssc.stop(stopSparkContext=True, stopGracefully=True)
    ```

2. Using HDFS check

```py
TERMINATION_FILE = "hdfs:///user/ubuntu/spark/stop_file"

def check_termination():
    # Access the Hadoop FileSystem through the SparkContext
    fs = sc._jvm.org.apache.hadoop.fs.FileSystem.get(sc._jsc.hadoopConfiguration())
    termination_path = sc._jvm.org.apache.hadoop.fs.Path(TERMINATION_FILE)
    
    # Check if the file exists in HDFS
    if fs.exists(termination_path):
        print("Termination file detected in HDFS. Stopping gracefully...")
        ssc.stop(stopSparkContext=True, stopGracefully=True)
        sys.exit(0)
```

## Using SparkSession to do the same
- We will be using `readStream` to read stream

1. Reading from port
    - To publish to port : `nc -lk 9999`

    ```py
    # Read streaming data from TCP socket
    socket_df = spark.readStream \
        .format("socket") \
        .option("host", "localhost") \
        .option("port", 9999) \
        .load()
    ```

2. Reading from file

    ```py
    file_df = spark.readStream \
        .format("text") \
        .option("path", "/path/to/directory") \
        .option("maxFilesPerTrigger", 1) \
        .load()
    ```
- Output modes
  - Append: Only new rows are added to the result table.
  - Complete: The entire result table is outputted to the sink.
  - Update: Only rows that were updated in the result table are outputted.

- Tranforming input stream (data frame)

  ```py
    socket_df = socket_df.selectExpr("CAST(value AS STRING)")
  ```

- Logging the output in console

  ```py
  query = socket_df.writeStream \
      .outputMode("append") \
      .format("console") \
      .start()

  query.awaitTermination()
  ```


