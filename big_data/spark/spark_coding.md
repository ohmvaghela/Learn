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

## Running sprak application in different modes
- Say we have a spark application mySparkApp.py
1. Local Mode (--master local[*])
    - `spark-submit --master local[*] mySparkApp.py`
    - local[*] : Run with a many theads available
2. Standalone mode
    - start the master and slaves
        - `start-master.sh && start-slaves.sh spark://master:7077`
    - Now to run it in standalone
        - `spark-submit --master spark://localhost:7077 word_count_sparksession.py`
3. Yarn
    - Before this we need to add spark config files in hdfs and change few spark configs
        - Spark-defaults.conf
           - ` spark.yarn.jars hdfs://localhost:9000/spark-jars/*`
        - Add jars to hdfs
            - `hdfs dfs -mkdir -p /spark-jars && hdfs dfs -put $HOME/bigdata/spark/jars/*.jar /spark-jars/`
        - Create spark event
            - `hdfs dfs -mkdir -p /spark-events && hdfs dfs -chmod 1777 /spark-events`
      
    - start yarn
        - `start-dfs.sh && start-yarn.sh`
    - submit application
        - `spark-submit --master yarn --deploy-mode cluster word_count_sparksession.py`
    - deploy mode : cluster, client
        - cluster
            - Driver runs on the cluster
            - Recommended for production
        - client
            - Driver runs locally
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

- More advanced transformation
    - Assuming this is data

    ```py
    data = [
        ("apple", 2),
        ("banana", 5),
        ("orange", 3),
        ("apple", 4),
        ("banana", 1),
        ("apple", 1)
    ]
    
    rdd = sc.parallelize(data)
    ```

    - Transformations
 
    ```py

    mapped_rdd = rdd.map(lambda x: (x[0], x[1] * 10))
    filtered_rdd = rdd.filter(lambda x: x[1] >= 3)

    # flatMap : It iterates over twice like in this case x[0] is first element of every tuple
    flatmapped_rdd = rdd.flatMap(lambda x: list(x[0]))
    
    # ReduceByKey : Group each key and perform aggregation on values
        # By default if it is tuple first value is treated as key and second as pair
    ## 1. Sum of all values in group
    reduced_rdd = rdd.reduceByKey(lambda a, b: a + b)
    ## Output : The above will become Reduced RDD: [('apple', 7), ('banana', 6), ('orange', 3)]
    ## 2. Max value group by key
    max_rdd = rdd.reduceByKey(lambda x, y: max(x, y))
    print("Max by Key:", max_rdd.collect())
    ## Output : [('apple', 3), ('banana', 4), ('orange', 5)]
    
    # GroupByKey groups by key and perform operations on values
    ## 1. combine values to list
    grouped_rdd = rdd.groupByKey().mapValues(list)
    print("Grouped RDD:", [(k, v) for k, v in grouped_rdd.collect()])
    ### Grouped RDD: [('apple', [2, 4, 1]), ('banana', [5, 1]), ('orange', [3])]
    ## 2. combines values as set
    grouped_rdd = rdd.groupByKey().mapValues(set)
    print("Grouped as Sets:", [(k, v) for k, v in grouped_rdd.collect()])
    ### Grouped as Sets: [('apple', {1, 2, 4}), ('banana', {1, 5}), ('orange', {3})]
    ## 3. Counting elements in each list
    group_count_rdd = rdd.groupByKey().mapValues(lambda v: len(list(v)))
    print("Group Count RDD:", group_count_rdd.collect())
    ### Group Count RDD: [('apple', 3), ('banana', 2), ('orange', 1)]

    # AggregateByKey
    data = [("a", 1), ("a", 2), ("b", 3), ("a", 4), ("b", 5)]
    rdd = sc.parallelize(data)
    ## 1. Find count and sum 
    sum_and_count_rdd = rdd.aggregateByKey((0, 0),  # Initial value (sum, count)
                             lambda acc, value: (acc[0] + value, acc[1] + 1),  # Local combiner -> acc : (sum,count), value : second element of input tuple
                             lambda acc1, acc2: (acc1[0] + acc2[0], acc1[1] + acc2[1])  # Global combiner
                            )
    print("[sum,count]:", sum_and_count_rdd.collect())
    ## Output : [sum,count] : [("a",7,3),("b",8,2)]
    ## 2. Counting min and max for each
    min_max_rdd = rdd.aggregateByKey((float('inf'), float('-inf')),
                                 lambda acc, value: (min(acc[0], value), max(acc[1], value)),
                                 lambda acc1, acc2: (min(acc1[0], acc2[0]), max(acc1[1], acc2[1]))
                                )

    print("Min and Max by Key:", min_max_rdd.collect())
    ## Output : [('a', (1, 4)), ('b', (3, 5))]

    # CountByKey
    data = [("a", 1), ("b", 2), ("a", 3), ("b", 4), ("a", 5)]
    rdd = sc.parallelize(data)
    ## 1. Calculate sum and count by key
    sum_count_rdd = rdd.combineByKey(lambda value: (value, 1),  # (sum, count)
                                     lambda acc, value: (acc[0] + value, acc[1] + 1), # acc : (sum,count), value : second element of input tuple
                                     lambda acc1, acc2: (acc1[0] + acc2[0], acc1[1] + acc2[1]))
    
    print("Sum and Count by Key:", sum_count_rdd.collect())
    ## Output : [('a', (9, 3)), ('b', (6, 2))]

    ```

    | Method        | Initialization | Suitable for                      | Avoid When                               |
    | ------------- | -------------- | ----------------------------------- | ---------------------------------------- |
    | `reduceByKey` | No custom init | Simple commutative/associative ops     | Complex types/operations               |
    | `aggregateByKey` | Custom init  | Different combine and merge ops      | Simple aggregations                      |
    | `combineByKey` | Custom init  | Different init and merge logic        | Basic sum, max, min                     |

    - Actions

    ```py
    line_count = rdd.count()
    all_lines = rdd.collect()
    first_3_lines = rdd.take(3)

    ```

    - collect v/s take
    
      | Feature | `take(n)` | `collect()` |
      |---|---|---|
      | Purpose | Get first n elements | Get all elements |
      | Use Case | Sampling or quick checks | When full dataset is needed |
      | Memory Usage | Lower, only n elements | High, can crash driver |
      | Performance | Faster with large RDDs | Slower if RDD is large |
      | Internal Behavior | Stops after collecting n | Gathers all partitions |
    
    - Working with text file in SparkContext
        - Say we have a rdd of text file named textFileRdd
    
        ```py
        word_counts = (textFileRdd
                   .flatMap(lambda line: line.split(" "))
                   .map(lambda word: (word, 1))
                   .reduceByKey(lambda a, b: a + b))
        
        # Collect and print results
        for word, count in word_counts.collect():
            print(f"{word}: {count}")
        ```

## Cache and Persistance in SparkContext

```py
rdd.cache() # cache() is a shorthand for persist(StorageLevel.MEMORY_ONLY).
upper_rdd.persist(StorageLevel.MEMORY_AND_DISK)
```

| Storage Level        | Description                                                                     |
| --------------------- | ------------------------------------------------------------------------------- |
| `MEMORY_ONLY`         | Stores RDD as deserialized objects in memory. Recomputes if data doesn't fit.       |
| `MEMORY_AND_DISK`    | Stores in memory, spills to disk if memory is insufficient.                     |
| `MEMORY_ONLY_SER`     | Stores RDD as serialized objects (Java serialization). Saves space, slower access. |
| `MEMORY_AND_DISK_SER` | Combination of memory and disk with serialization.                             |
| `DISK_ONLY`          | Stores RDD only on disk. Good for extremely large datasets.                     |
| `MEMORY_ONLY_2`       | Same as `MEMORY_ONLY` but with 2x replication for fault tolerance.                |
| `MEMORY_AND_DISK_2`  | Same as `MEMORY_AND_DISK` but with 2x replication for fault tolerance.           |

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
  spark = SparkSession.builder
          .master("local")
          .config("spark.sql.shuffle.partitions", "50") 
          .appName("MyApp")
          .getOrCreate()

  # Exections

  spark.stop()
  ```

- More configs (`.config("spark.sql.shuffle.partitions", "50")`)

    | Parameter | Description | Example |
    |---|---|---|
    | `spark.sql.shuffle.partitions` | Number of partitions for shuffle operations (default: 200) | 50 |
    | `spark.executor.memory` | Memory allocated to each executor | 4g |
    | `spark.driver.memory` | Memory allocated to the driver | 2g |
    | `spark.executor.cores` | Number of cores per executor | 4 |
    | `spark.sql.autoBroadcastJoinThreshold` | Max size for broadcast joins | 10MB |
    | `spark.serializer` | Serializer for performance optimization | `org.apache.spark.serializer.KryoSerializer` |

- Other methods

  ```py
  # Loading and creating/updating csv file
  ## Read
  df = spark.read.csv("file:///path/to/local/file.csv", header=True, inferSchema=True)
  df = spark.read.csv("/path/to/hadoop/file.csv", header=True, inferSchema=True)
  
  ## Write DataFrame 
  df.write.mode("overwrite").parquet("file:///output/dir/path") ## Store as parquet file
  df.write.csv("file:///path/to/dir", header=True, mode="overwrite")
  df.write.csv("hdfs://namenode_host:9000/path/to/dir", header=True, mode="overwrite")
  ### Other modes : Other options: "append", "ignore", "error".
  ### part-00000-*.csv  # The actual data (partitioned output)
  ### _SUCCESS          # An empty file indicating successful completion

  ## Basic Transformation
  df = df.filter(df["Age"] > 30).select("Name", "Age")

  ## Basic aggragation
  df.groupBy("Age").count().show()

  ## Create a temperory view and run sql query on it
  df.createOrReplaceTempView("people")
  spark.sql("SELECT Name, Age FROM people WHERE Age > 30").show()
  ## Cannot use subquery 

  ## Schema handling
  ### Creating schema
  from pyspark.sql.types import StructType, StructField, StringType, IntegerType
  schema = StructType([
      StructField("Name", StringType(), True),
      StructField("Age", IntegerType(), True)
  ])
  df = spark.read.schema(schema).csv("path/to/file.csv", header=True)
  ### Now if cvs file has more then 2 columns they will be ignored
  ```

- More transformations and actions

    ```py
    # Transformation Examples
    df_filtered = df.filter(df["Age"] > 30)  # Filter rows
    df_selected = df_filtered.select("Name")  # Select specific column
    df_with_column = df.withColumn("AgePlus10", df["Age"] + 10)  # Add a new column
    df_sorted = df.orderBy(df["Age"].desc())  # Sort the DataFrame
    df_dropped = df.drop("Age")  # Drop a column
    
    # Action Examples
    df_filtered.show()          # Display data on the console
    print(df.count())           # Count the number of rows
    print(df.collect())         # Collect all data as a list of rows
    print(df.first())           # Return the first row
    print(df.take(2))           # Return the first 2 rows as a list

    # Transforamtion + Aggragation
    ## GroupBy and Aggregation
    df.groupBy("Age").count().show()
    ## Filter, Sort, and Show
    df.filter(df["Age"] > 30).orderBy(df["Age"]).show()
    ```



- Reading Text file word count

    ```py
    
    text_df = spark.read.text("file:///path/to/file")
    from pyspark.sql.functions import explode, split
    word_counts = (text_df
               .select(explode(split(text_df.value, " ")).alias("word"))
               .groupBy("word")
               .count())
    word_counts.show()
    ```

## Partitions
### SparkContext
- Defining partitions while creating rdd

    ```py
    rdd = sc.textFile("sample.txt", minPartitions=10)
    ```

- Changing number of partitions

    ```py
    rdd = rdd.repartition(5)  # Increase partitions
    rdd = rdd.coalesce(2)     # Decrease partitions (more efficient for downscaling)
    ```

- Changing number of cores for an pyspark app

    ```py
    spark-submit --master local[2] word_count.py  # 2 cores
    spark-submit --master local[4] word_count.py  # 4 cores
    ```

### SparkSession
- Creating session with specific cores

    ```py
    spark = SparkSession.builder \
        .appName("PartitionAndCoreExample") \
        .master("local[4]") \  # Use 4 CPU cores
        .getOrCreate()
    ```

- Setting default partitions while creating SparkSession

    ```py
    spark = SparkSession.builder \
        .appName("PartitionAndCoreExample") \
        .config("spark.default.parallelism", "8") \  # Default partitions for RDD operations
        .config("spark.sql.shuffle.partitions", "8") \  # Default partitions for DataFrame shuffles
        .getOrCreate()
    ```

- Creating dataFrame with specific partitions

    ```py
    df = spark.read.csv("large_dataset.csv", header=True, inferSchema=True, minPartitions=20)
    df = spark.read \
        .option("header", "true") \
        .option("inferSchema", "true") \
        .csv("large_dataset.csv") \
        .repartition(20)
    ```

- Saving file with partitions

    ```py
    df.write.partitionBy('year', 'month').parquet('file:///path/to/dir')
    df.write.partitionBy('year', 'month').parquet('file:///path/to/dir').filter("day = monday")
    df.write.option('compression', 'snappy').parquet('file:///path/to/dir')
    ```
## Database with spark
- Connecting to db and loading data
 
```py
df = spark.read \
    .format("jdbc") \
    .option("url", "jdbc:mysql://localhost:3306/temp") \
    .option("dbtable", "student") \
    .option("user", "root") \
    .option("password", "root") \
    .option("numPartitions", 10) \
    .option("partitionColumn", "id") \
    .option("lowerBound", "1") \
    .option("upperBound", "1000") \
    .load()
```

- Adding data to db

```py
from pyspark.sql import SparkSession

# Create Spark Session
spark = SparkSession.builder \
    .appName("MySQL Write") \
    .config("spark.jars", "/home/ubuntu/bigdata/spark/jars/mysql-connector-java-8.0.33.jar") \
    .getOrCreate()

# Sample DataFrame
data = [("John", 28), ("Alice", 24), ("Bob", 30)]
columns = ["name", "age"]
df = spark.createDataFrame(data, columns)

# MySQL Connection Properties
mysql_url = "jdbc:mysql://localhost:3306/mydatabase"
mysql_properties = {
    "user": "root",
    "password": "password",
    "driver": "com.mysql.cj.jdbc.Driver"
}

# Write DataFrame to MySQL Table
df.write \
    .mode("append") \  # Modes: "append", "overwrite", "ignore", "error"
    .jdbc(url=mysql_url, table="people", properties=mysql_properties)

print("✅ Data written successfully to MySQL")
```

## Working with JSON
- Read json file
- Types
    - multple json in one

        ```
        {"id":1, "name":"ohm"}
        {"id":2, "name":"ohm1"}
        ``` 

        ```py
        df = spark.read.json("file:///path/to/jsonfile.json")
        ```

    - Reading json array

        ```
        [
        {"id":1, "name":"ohm"},
        {"id":2, "name":"ohm1"}
        ]
        ```

        ```py
        df = spark.read.json("file:///path/to/jsonfile.json", multiLine=True)
        ```


