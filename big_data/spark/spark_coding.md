# Spark Practical

## Starting standalone spark 
- To see executor details : localhost:4040
```bash
# Start standalone spark master 
# UI : localhost:8080
start-master.sh

# Start slaves and connects them with masters
start-slaves.sh spark://master:7077 

jps

# To stop them
stop-master.sh
stop-slaves.sh 
```

## Pyspark
- Similar to python REPL but with `SparkContext(sc)` and `SparkSession(spark)`
- Running it

```bash
# With default options
pyspark

# defining options
pyspark --master spark://master:7077
    --num-executors 4
    --executor-memory 2G
    --driver-memory 1G
```

## SparkContext 
- SparkContext(sc) : Used to be primary entrypoint to spark earlier
- Role
  - Connect to spark cluster
  - Create RDD
  - Manage job execution and task scheduling
- Mainly used for low level opertaions like operations and configuration
- Used for directly working in RDD
- Using sprakContext with pyspark bash (sprakContext auto initialized in bash)

  ```py
  # See configurations
  print(sc)

  # Creating RDD from sample data
  data = [1,2,3,4]
  rdd = sc.parallelize(data)

  ## Performing transformation
  rdd1 = rdd.map(lambda x: x*2)

  ## Performing actions
  result = rdd1.collect()
  pritn(result)
  ```

- Using sprakContext with python script
  - To run this application use `spark-submit my_spark_app.py`
  ```py
  # my_spark_app.py
  from pyspark import SparkContext, SparkConf

  # Create sprak config object
  configs = SparkConf().setAppName("FirstSparkApp").setMaster("local[*]")

  # Create sprakContext object
  sc = SparkContext(conf = configs)

  # Operations
  rdd = sc.parallelize([10, 20, 30, 40, 50])
  result = rdd.filter(lambda x: x > 20).collect()
  print(result)  # Output: [30, 40, 50]
  
  # Stop the SparkContext
  sc.stop()
  ```

- Different ways of creating `rdd`

  ```py
  # From existing data
  rdd = sc.parallelize([1, 2, 3, 4])

  # From file
  rdd = sc.testFile("/path/to/file.txt")

  # From whole dir
  rdd = sc.wholeTextFiles("/path/to/dir")
  # Read from Hadoop-compatible sources
  hadoop_rdd = sc.sequenceFile("/path/to/sequencefile")
  ```

- RDD transformation and action operations

  ```py
  # Transformation: map
  mapped_rdd = rdd.map(lambda x: x * 2)
  
  # Transformation: filter
  filtered_rdd = rdd.filter(lambda x: x % 2 == 0)

  # Action: collect
  print(filtered_rdd.collect())  # Output: [2, 4]
  
  # Action: count
  print(rdd.count())  # Output: 4

  # Action: take
  print(filtered_rdd.take(1))  # Output: [2, 4]
  ```

- collect v/s take

  | Feature | `take(n)` | `collect()` |
  |---|---|---|
  | Purpose | Get first n elements | Get all elements |
  | Use Case | Sampling or quick checks | When full dataset is needed |
  | Memory Usage | Lower, only n elements | High, can crash driver |
  | Performance | Faster with large RDDs | Slower if RDD is large |
  | Internal Behavior | Stops after collecting n | Gathers all partitions |

## SprakSession
- Serve as entrypoint to interact with spark functionalities and create DataFrame and DataSet
- It also serves as unified interface for `SparkContext`, `SQLContext`, `StreamingContext`, `HiveContext`, etc...
- Use SparkSession when working with higher level apis like DataFrame, DataSet, SQL etc..
- Sample use of SparkSession

  ```py
  data = [("Alice", 34), ("Bob", 45)]
  df = spark.createDataFrame(data, ["Name", "Age"])
  df.show()
  ```

- If creating app using python script use following to initialize it

  ```py
  from pyspark.sql import SparkSession
  spark = SparkSession.builder.master("local").appName("MyApp").getOrCreate()

  # Exections

  spark.stop()
  ```

- Other methods

  ```py
  # Loading and creating/updating csv file
  ## Read
  df = spark.read.csv("file:///path/to/local/file.csv", header=True, inferSchema=True)
  df = spark.read.csv("/path/to/hadoop/file.csv", header=True, inferSchema=True)
  
  ## Write DataFrame to Parquet
  df.write.mode("overwrite").parquet("output/path")
  df.write.csv("file:///home/ubuntu/Desktop/temp/spark/output_csv", header=True, mode="overwrite")
  df.write.csv("hdfs://namenode_host:9000/user/ubuntu/output_csv", header=True, mode="overwrite")
  ### Other modes : Other options: "append", "ignore", "error".
  ### part-00000-*.csv  # The actual data (partitioned output)
  ### _SUCCESS           # An empty file indicating successful completion

  ```


