# Spark Practical

## Content
- [1. Starting Standalone Spark](./spark_coding.md#1-starting-standalone-spark)
    - [Spark Master and Slave Setup](./spark_coding.md#spark-master-and-slave-setup)
- [2. Pyspark](./spark_coding.md#2-pyspark)
    - [Overview](./spark_coding.md#overview)
    - [Running PySpark](./spark_coding.md#running-it)
- [3. Running sprak application in different modes](./spark_coding.md#3-Running-sprak-application-in-different-modes)
- [4. Spark Context](./spark_coding.md#4-sparkcontext)
    - [Overview](./spark_coding.md#overview)
    - [Data Sources](./spark_coding.md#data-sources)
    - [Running SQL Queries](./spark_coding.md#running-sql-queries)
- [5. Crreating RDD](./spark_coding.md#5-creating-rdds-resilient-distributed-datasets)
- [6. RDD Transformations and Actions](./spark_coding.md#6-RDD-Transformations-and-Actions)
- [7. Advanced RDD Transformations](./spark_coding.md#7-Advanced-RDD-Transformations)
    - [Sample data](./spark_coding.md#sample-data)
    - [Key Transformation](./spark_coding.md#Key-Transformations)
    - [Advanced Transformations](./spark_coding.md#Advanced-Transformations)
    - [Common Actions](./spark_coding.md#Common-Actions)
    - [collect` v/s `take(n)](./spark_coding.md#collect-vs-taken)
    - [Working with text file in SparkContext](./spark_coding.md#Working-with-text-file-in-SparkContext)
- [8. Cache and Persistance in SparkContext](./spark_coding.md#8-Cache-and-Persistance-in-SparkContext)
- [Saving RDD](./spark_coding.md#Saving-RDD)
- [9. SparkSession](./spark_coding.md#9-SparkSession)
    - [Creating DataFrame](./spark_coding.md#Creating-DataFrame)
    - [Creating SparkSession in python script](./spark_coding.md#Creating-SparkSession-in-python-script)
    - [Common Configurations (.config())](./spark_coding.md#Common-Configurations-config)
    - [Reading and Writing Data](./spark_coding.md#Reading-and-Writing-Data)
    - [DataFrame Transformation and Actions](./spark_coding.md#DataFrame-Transformation-and-Actions)
    - [Aggregations](./spark_coding.md#Aggregations)
    - [Schema Handling](./spark_coding.md#Schema-Handling)
    - [Temperary Views and SQL Queries](./spark_coding.md#Temperary-Views-and-SQL-Queries)
    - [Reading Text files and Word count example](./spark_coding.md#Reading-Text-files-and-Word-count-example)
- [10. Partitions](./spark_coding.md#10-Partitions)
    - [SparkContext](./spark_coding.md#SparkContext)
    - [SparkSession](./spark_coding.md#SparkSession)
- [11. Narrow and wide transformation](./spark_coding.md#11-Narrow-and-wide-transformation)
    - [Shuffling](./spark_coding.md#Shuffling)
    - [Narrow Transformation](./spark_coding.md#Narrow-Transformation)
    - [Wide Transformation](./spark_coding.md#Wide-Transformation)
- [12. Database with spark](./spark_coding.md#12-Database-with-spark)
- [13. Working with JSON](./spark_coding.md#13-Working-with-JSON)
- [14. Broadcast variable](./spark_coding.md#14-broadcast-variable)
- [15. Accumulator](./spark_coding.md#15-accumulator)

## 1. Starting Standalone Spark 

### Spark Master and Slave Setup
- **To see executor details:** [localhost:4040](http://localhost:4040)
- **Master UI:** [localhost:8080](http://localhost:8080)

```bash
# Start standalone spark master
start-master.sh

# Start slaves and connect them with the master
start-slaves.sh spark://master:7077 

# Check running processes
jps

# Stop the master and slaves
stop-master.sh
stop-slaves.sh 
```


## 2. Pyspark

### Overview
- PySpark is similar to the Python REPL but includes:
    - `SparkContext(sc)`: For RDD-based operations.
    - `SparkSession(spark)`: Preferred for DataFrame and SQL-based operations.

### Running it

```bash
# Start PySpark with default options
pyspark

# Define options for PySpark
pyspark --master spark://master:7077 \
    --num-executors 4 \
    --executor-memory 2G \
    --driver-memory 1G
```

## 3. Running sprak application in different modes
### Example Application
- Assume we have a Spark application mySparkApp.py

1. **Local Mode** (`--master local[*]`)
    - local[*] : Run with a many theads available
    
    ```bash
    spark-submit --master local[*] mySparkApp.py`
    ```
    
2. **Standalone mode**

    ```bash
    # Start the master and slaves
    start-master.sh && start-slaves.sh spark://master:7077

    # Submit the Spark application
    spark-submit --master spark://localhost:7077 mySparkApp.py
    ```

3. Yarn
    - **Pre-requisites**:
        - update `spark-defaults.conf`

            ```bash
            spark.yarn.jars hdfs://localhost:9000/spark-jars/*
            ```

        - Add jars to HDFS
        
            ```bash
            hdfs dfs -mkdir -p /spark-jars
            hdfs dfs -put $HOME/bigdata/spark/jars/*.jar /spark-jars/
            ```

        - Create spark event
        
            ```bash
            hdfs dfs -mkdir -p /spark-events 
            hdfs dfs -chmod 1777 /spark-events
            ```
      
    - start yarn
        
        ```bash
        start-dfs.sh && start-yarn.sh
        ```

    - submit application
        
        ```bash
        spark-submit --master yarn --deploy-mode cluster word_count_sparksession.py
        ```
        
        - Deploy Modes :
            - **Cluster**: Driver runs on the cluster (Recommended for production).
            - **Client**: Driver runs locally.

## 4. SparkContext
- **SparkContext(sc)** : Used to be primary entrypoint to spark earlier
- Role
    - Connect to Spark cluster.
    - Create RDDs (Resilient Distributed Datasets).
    - Manage job execution and task scheduling.
    - Primarily used for low-level operations and configurations.
- Using SparkContext with PySpark REPL

    ```py
    # See Spark configuration
    print(sc)

    # Creating an RDD from sample data
    data = [1, 2, 3, 4]
    rdd = sc.parallelize(data)

    # Performing a transformation
    rdd1 = rdd.map(lambda x: x * 2)

    # Performing an action
    result = rdd1.collect()
    print(result)  # Output: [2, 4, 6, 8]
    ```

- Using SparkContext in a Python Script

    ```py
    # my_spark_app.py
    from pyspark import SparkContext, SparkConf

    # Create Spark config object
    configs = SparkConf().setAppName("FirstSparkApp").setMaster("local[*]")

    # Create SparkContext object
    sc = SparkContext(conf=configs)

    # RDD Operations
    rdd = sc.parallelize([10, 20, 30, 40, 50])
    result = rdd.filter(lambda x: x > 20).collect()
    print(result)  # Output: [30, 40, 50]

    # Stop the SparkContext
    sc.stop()
    ```

## 5. Creating RDDs (Resilient Distributed Datasets)

```py
# From existing data
rdd = sc.parallelize([1, 2, 3, 4])

# From a file
rdd = sc.textFile("/path/to/file.txt")

# From a whole directory
rdd = sc.wholeTextFiles("/path/to/dir")

# From Hadoop-compatible sources
hadoop_rdd = sc.sequenceFile("/path/to/sequencefile")
```


## 6. RDD Transformations and Actions
- Basic Transformations and Actions

```py
# Sample RDD
rdd = sc.parallelize([1, 2, 3, 4])

# Transformation: map
mapped_rdd = rdd.map(lambda x: x * 2)

# Transformation: filter
filtered_rdd = rdd.filter(lambda x: x % 2 == 0)

# Action: collect
print(filtered_rdd.collect())  # Output: [2, 4]

# Action: count
print(rdd.count())  # Output: 4

# Action: take
print(filtered_rdd.take(1))  # Output: [2]
```

## 7. Advanced RDD Transformations

### Sample Data

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

### Key Transformations
 
```py
# 1. map() : Apply a function to each element
mapped_rdd = rdd.map(lambda x: (x[0], x[1] * 10))

# 2. filter() : Retain elements meeting a condition
filtered_rdd = rdd.filter(lambda x: x[1] >= 3)

# 3. flatMap() : Apply function and flatten results
# Example: Extracting characters from the first element of each tuple
flatmapped_rdd = rdd.flatMap(lambda x: list(x[0]))

# 4. reduceByKey() : Aggregate values by key
# a. Sum of values by key
reduced_rdd = rdd.reduceByKey(lambda a, b: a + b)
# Output: [('apple', 7), ('banana', 6), ('orange', 3)]

# b. Max value by key
max_rdd = rdd.reduceByKey(lambda x, y: max(x, y))
# Output: [('apple', 4), ('banana', 5), ('orange', 3)]

# 5. groupByKey() : Group values by key
# a. Convert grouped values to a list
grouped_rdd = rdd.groupByKey().mapValues(list)
# Output: [('apple', [2, 4, 1]), ('banana', [5, 1]), ('orange', [3])]

# b. Convert grouped values to a set
grouped_rdd = rdd.groupByKey().mapValues(set)
# Output: [('apple', {1, 2, 4}), ('banana', {1, 5}), ('orange', {3})]

# c. Count elements in each group
group_count_rdd = rdd.groupByKey().mapValues(lambda v: len(list(v)))
# Output: [('apple', 3), ('banana', 2), ('orange', 1)]
```

### Advanced Transformations

```py
data = [("a", 1), ("a", 2), ("b", 3), ("a", 4), ("b", 5)]
rdd = sc.parallelize(data)

# 1. aggregateByKey() : Custom aggregation with different local & global combiners
# a. Sum and count for each key
sum_and_count_rdd = rdd.aggregateByKey((0, 0), 
                     lambda acc, value: (acc[0] + value, acc[1] + 1),  # Local combiner
                     lambda acc1, acc2: (acc1[0] + acc2[0], acc1[1] + acc2[1])  # Global combiner
                    )
# Output: [('a', (7, 3)), ('b', (8, 2))]

# b. Calculate min and max for each key
min_max_rdd = rdd.aggregateByKey((float('inf'), float('-inf')),
                                 lambda acc, value: (min(acc[0], value), max(acc[1], value)),
                                 lambda acc1, acc2: (min(acc1[0], acc2[0]), max(acc1[1], acc2[1]))
                                )
# Output: [('a', (1, 4)), ('b', (3, 5))]

# 2. combineByKey() : Initialize, merge locally, and merge globally
# a. Sum and count by key
sum_count_rdd = rdd.combineByKey(lambda value: (value, 1), 
                                 lambda acc, value: (acc[0] + value, acc[1] + 1), 
                                 lambda acc1, acc2: (acc1[0] + acc2[0], acc1[1] + acc2[1]))
# Output: [('a', (7, 3)), ('b', (8, 2))]
```

| Method        | Initialization | Suitable for                      | Avoid When                               | Control Over Aggregation method |
| ------------- | -------------- | ----------------------------------- | ---------------------------------------- |-|
| `reduceByKey` | No custom init | Simple commutative/associative ops   | Complex types/operations               | Least to none |
| `aggregateByKey` | Custom init  | Different combine and merge ops      | Simple aggregations                      | High control on aggregation |
| `combineByKey` | Custom init  | Different init and merge logic        | Basic sum, max, min                     | Full control on aggregation | 

### Common Actions

```py
# Count elements in the RDD
line_count = rdd.count()

# Collect all elements into a list
all_lines = rdd.collect()

# Take first n elements (useful for sampling)
first_3_lines = rdd.take(3)
```

### `collect` v/s `take(n)`
    
| Feature | `take(n)` | `collect()` |
|---|---|---|
| Purpose | Get first n elements | Get all elements |
| Use Case | Sampling or quick checks | When full dataset is needed |
| Memory Usage | Lower, only n elements | High, can crash driver |
| Performance | Faster with large RDDs | Slower if RDD is large |
| Internal Behavior | Stops after collecting n | Gathers all partitions |

### Working with text file in SparkContext

```py
# Assuming textFileRdd is an RDD of lines from a text file
word_counts = (textFileRdd
               .flatMap(lambda line: line.split(" "))
               .map(lambda word: (word, 1))
               .reduceByKey(lambda a, b: a + b))

# Display word counts
for word, count in word_counts.collect():
    print(f"{word}: {count}")
```

## 8. Cache and Persistance in SparkContext

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

## Saving RDD
- Saving as text file
```py
rdd.saveAsTextFile("output/textFile")
```
- Saving as Sequence File
```py
rdd.saveAsTextFile("output/textFile")
```
- Saving as pickle file
```py
rdd.saveAsPickleFile("output/pickleFile")
```
- Saving as Hadoop file
```py
rdd.saveAsHadoopFile("output/hadoopFile", "org.apache.hadoop.mapred.TextOutputFormat")
```
- Saving as Compressed file
```py
rdd.saveAsTextFile("output/compressedFile", "org.apache.hadoop.io.compress.GzipCodec")
```


## 9. SparkSession

- **SparkSession as Entry Point:** SparkSession serves as the primary gateway for utilizing Spark's capabilities.
- **Core Functionality:** Its main uses include:
    - Creating DataFrames.
    - Creating DataSets.
    - Executing SQL queries.
- **Unified Interface:** SparkSession consolidates the functionalities of several older contexts, including:
    - SparkContext
    - SQLContext
    - StreamingContext
    - HiveContext
- **Recommended Usage:**  SparkSession is the preferred method for working with higher-level Spark APIs like:
    - DataFrame API
    - DataSet API
    - SQL API

### Creating DataFrame
1.  Using array

    ```py
    data = [("Alice", 34), ("Bob", 45)]
    df = spark.createDataFrame(data, ["Name", "Age"])
    df.show()
    ```

2. Using RDD (With createDataFrame)

    ```py
    rdd = spark.sparkContext.parallelize([("Alice", 34), ("Bob", 45), ("Cathy", 29)])

    schema = StructType([
        StructField("name", StringType(), True),
        StructField("age", IntegerType(), True)
    ])

    df = spark.createDataFrame(rdd, schema)
    df.show()
    ```

3. Using RDD (With toDF)

    ```py
    rdd = spark.sparkContext.parallelize([("Alice", 34), ("Bob", 45), ("Cathy", 29)])

    df = rdd.toDF(["name", "age"])
    df.show()
    ```

4. Using read json (When rdd data is in dict format)

    ```py
    rdd = spark.sparkContext.parallelize([{"name": "Alice", "age": 34}, {"name": "Bob", "age": 45}, {"name": "Cathy", "age": 29}])

    df = spark.read.json(rdd)
    df.show()
    ```

5. **[Using read](./spark_coding.md#Reading-and-Writing-Data)**


### Creating SparkSession in python script

```py
from pyspark.sql import SparkSession

# Initialize SparkSession
spark = SparkSession.builder \
    .master("local") \
    .appName("MyApp") \
    .config("spark.sql.shuffle.partitions", "50") \
    .getOrCreate()

# Example: Creating a DataFrame
data = [("Alice", 34), ("Bob", 45)]
df = spark.createDataFrame(data, ["Name", "Age"])
df.show()

# Stop the SparkSession
spark.stop()
```

### Common Configurations (.config())


| Parameter | Description | Example |
|---|---|---|
| `spark.sql.shuffle.partitions` | Number of partitions for shuffle operations (default: 200) | 50 |
| `spark.executor.memory` | Memory allocated to each executor | 4g |
| `spark.driver.memory` | Memory allocated to the driver | 2g |
| `spark.executor.cores` | Number of cores per executor | 4 |
| `spark.sql.autoBroadcastJoinThreshold` | Max size for broadcast joins | 10MB |
| `spark.serializer` | Serializer for performance optimization | `org.apache.spark.serializer.KryoSerializer` |

### Reading and Writing Data

1. Reading CSV file

```py
# Read CSV with custom delimiters
df = spark.read.option("delimiter", "#") \
              .option("lineSep", "\n") \
              .csv("path_to_your_file.csv", header=True, inferSchema=True)

# From HDFS
df = spark.read.csv("hdfs://namenode_host:9000/path/to/file.csv", header=True, inferSchema=True)
```

2. Writing DataFrames to files

```py
# Write as Parquet file
df.write.mode("overwrite").parquet("file:///output/dir/path")

# Write as CSV file
df.write.csv("file:///path/to/dir", header=True, mode="overwrite")
df.write.csv("hdfs://namenode_host:9000/path/to/dir", header=True, mode="overwrite")
# Other modes: "append", "ignore", "error"
```

### DataFrame Transformation and Actions
- Transformations

```py
# Filtering rows where Age > 30
df_filtered = df.filter(df["Age"] > 30)
df_filtered = player_df.filter((col("PRICE") < 7) & (col("YEAR\r") > 2016)).show()


# Selecting specific columns
df_selected = df_filtered.select("Name")

# Adding a new column
df_with_column = df.withColumn("AgePlus10", df["Age"] + 10)

# Changing column type
df_with_column = df.withColumn("age", col("age").cast("INT"))

# Sorting the DataFrame
df_sorted = df.orderBy(df["Age"].desc())

# Dropping a column
df_dropped = df.drop("Age")
```

- Actions

```py
df_filtered.show()  # Display data on the console
print(df.count())   # Count rows
print(df.collect()) # Collect all data as a list of rows
print(df.first())   # Return the first row
print(df.take(2))   # Return the first 2 rows as a list
```

### Aggregations

```py
from pyspark.sql.functions import collect_list, sum

data = [("A", 1), ("A", 2), ("A", 3), ("B", 4), ("B", 5), ("C", 6)]
columns = ["key", "value"]
df = spark.createDataFrame(data, columns)

# Group by 'key' and collect values in a list
result = df.groupBy("key").agg(collect_list("value").alias("value_list"))
result.show()

# Group by 'key' and sum values
result = df.groupBy("key").agg(sum("value").alias("total_value"))
result.show()
```

- Output

```
+---+----------+
|key|value_list|
+---+----------+
|  A| [1, 2, 3]|
|  B|    [4, 5]|
|  C|       [6]|
+---+----------+

+---+-----------+
|key|total_value|
+---+-----------+
|  A|          6|
|  B|          9|
|  C|          6|
+---+-----------+
```

### Schema Handling 

1. Autohandling

```py
# CSV
df = spark.read.csv("path/to/file.csv", header=True, inferSchema=True)
df.printSchema()  # Displays the inferred schema

# JSON
df = spark.read.json("path/to/json_data")
df.printSchema()  # Prints the inferred schema, useful for nested data

```

2. Using StructType

```py
from pyspark.sql.types import StructType, StructField, StringType, IntegerType

# Define a schema StructField("Name", Type, Nullable?)
schema = StructType([
    StructField("Name", StringType(), True),
    StructField("Age", IntegerType(), True)
])

# Apply the schema while reading a CSV
df = spark.read.schema(schema).csv("path/to/file.csv", header=True)
df.printSchema()
```

3. Changing Schema of perticular column

```py
from pyspark.sql.functions import col

# Cast columns to specific types
df = df.withColumn("Age", col("Age").cast(IntegerType()))
df.printSchema()
```

4. Renaming column name 

```py
df = df.toDF("NewName1", "NewName2")
```

5. Another way for creating schema

```py
# Example of creating a schema programmatically (e.g., based on column names from a list)
column_names = ["Name", "Age", "Gender"]
data_types = [StringType(), IntegerType(), StringType()]

schema = StructType([StructField(name, dtype, True) for name, dtype in zip(column_names, data_types)])
```

6. Using rows to create df

```py
from pyspark.sql import Row

# Create Row objects without predefined schema
row1 = Row(Name="Alice", Age=34)
row2 = Row(Name="Bob", Age=45)

# Convert Row objects to DataFrame
df = spark.createDataFrame([row1, row2])
df.show()
```

### Temperary Views and SQL Queries

```py
# Create a temporary view from DataFrame
df.createOrReplaceTempView("people")

# Execute SQL queries on the temporary view
spark.sql("SELECT Name, Age FROM people WHERE Age > 30").show()
# Note: Cannot use subqueries in this approach
```

### Reading Text files and Word count example

```py
text_df = spark.read.text("file:///path/to/file")

from pyspark.sql.functions import explode, split

# Word count example
word_counts = (text_df
               .select(explode(split(text_df.value, " ")).alias("word"))
               .groupBy("word")
               .count())

word_counts.show()
```



## 10. Partitions
### SparkContext
- Defining Partitions While Creating an RDD:
    - You can define the number of partitions when creating an RDD by specifying the minPartitions parameter

    ```py
    rdd = sc.textFile("sample.txt", minPartitions=10)
    ```

- Changing the Number of Partitions:
    - Spark provides methods to modify the number of partitions in an RDD:
        - Increasing the number of partitions:
        
            ```py
            rdd = rdd.repartition(5)  # Increase partitions
            ```
            
        - Decreasing the number of partitions (more efficient for downscaling):
        
            ```py
            rdd = rdd.coalesce(2)  # Decrease partitions
            ```

- Adjusting the Number of Cores for a PySpark Application:
    - When running a PySpark application, you can specify the number of cores to use with the spark-submit command:

    ```bash
    spark-submit --master local[2] word_count.py  # 2 cores
    spark-submit --master local[4] word_count.py  # 4 cores
    ```

### SparkSession
- Creating session with specific cores:
    - You can create a SparkSession with a specific number of CPU cores by setting the master parameter:

    ```py
    spark = SparkSession.builder 
        .appName("PartitionAndCoreExample") 
        .master("local[4]")   # Use 4 CPU cores
        .getOrCreate()
    ```

- Setting Default Partitions for RDD and DataFrame Operations:
    - The SparkSession builder allows you to configure default partitioning for RDD operations and DataFrame shuffles:

    ```py
    spark = SparkSession.builder 
        .appName("PartitionAndCoreExample") 
        .config("spark.default.parallelism", "8")   # Default partitions for RDD operations
        .config("spark.sql.shuffle.partitions", "8")   # Default partitions for DataFrame shuffles
        .getOrCreate()
    ```

- Creating a DataFrame with Specific Partitions:
    - When reading large datasets, you can define the number of partitions for a DataFrame using the minPartitions parameter:

    ```py
    df = spark.read.csv("large_dataset.csv", header=True, inferSchema=True, minPartitions=20)
    ```

    - Alternatively, you can repartition the DataFrame after loading the data:

    ```py
    df = spark.read 
        .option("header", "true") 
        .option("inferSchema", "true") 
        .csv("large_dataset.csv") 
        .repartition(20)
    ```

- Saving Data with Specific Partitions:

    ```py
    # When saving a DataFrame, you can specify partitioning by columns:
    df.write.partitionBy('year', 'month').parquet('file:///path/to/dir')
    # You can also apply filters when saving the partitioned data:
    df.write.partitionBy('year', 'month').parquet('file:///path/to/dir').filter("day = monday")
    # Additionally, you can specify a compression format
    df.write.option('compression', 'snappy').parquet('file:///path/to/dir')
    ```

## 11. Narrow and wide transformation

### Shuffling
1. If the data is to be compared accross partitions then we need shuffling
2. Like for a key,value pair array we need to sum of values, grouped by values
3. Then first we will perfrom this operation in a single partitions
4. Then request the data from all the partitions and repartition the data
5. Then perfrom transformation on each partitions again then aggregate the data
- This process of gathering data, grouping and repartitioning (4th step) is called shuffling 

### Narrow Transformation
- When the transformation happens entirely in one transformation
- And no shuffling is required 
- Eg. `map`, `filter`, `flatmap`, `union`, `sample`

### Wide Transformation
- Here for perfroming transformation we need to break it in seperate steps
- Or say shuffling is required at an intermediate step to complete transformation
- Eg. `groupByKey`, `reduceByKey`, `join`, `distinct`, `sortByKey` etc.

## 12. Database with spark
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
spark = SparkSession.builder 
    .appName("MySQL Write") 
    .config("spark.jars", "/home/ubuntu/bigdata/spark/jars/mysql-connector-java-8.0.33.jar") 
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
df.write 
    .mode("append")   # Modes: "append", "overwrite", "ignore", "error"
    .jdbc(url=mysql_url, table="people", properties=mysql_properties)

print("✅ Data written successfully to MySQL")
```

## 13. Working with JSON
- Read json file
- Types
    1. Multiple JSON Records in a Single Line:

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

## 14. Broadcast variable

```py
from pyspark import SparkContext

sc = SparkContext.getOrCreate()

# Broadcast a dictionary
lookup = {"A": 1, "B": 2, "C": 3}
broadcastVar = sc.broadcast(lookup)

# Accessing the value of the broadcast variable
print(broadcastVar.value)  # Output: {'A': 1, 'B': 2, 'C': 3}

# Unpersisting the broadcast variable
broadcastVar.unpersist(blocking=True)

# Checking if the broadcast variable is destroyed
print(broadcastVar.isDestroyed())  # Output: False

# Destroying the broadcast variable
broadcastVar.destroy()

# Verifying the destruction
print(broadcastVar.isDestroyed())  # Output: True
```

## 15. Accumulator

```py
from pyspark import SparkContext

# Initialize SparkContext
sc = SparkContext("local", "Accumulator Example")

# Create an accumulator with initial value 0
acc = sc.accumulator(0)

# Add values to the accumulator
acc.add(5)
acc.add(10)

# Check the current value of the accumulator
print(f"Accumulator Value: {acc.value}")  # Output: 15

# Check if the accumulator has been modified (isSet)
print(f"Accumulator is set: {acc.isSet}")  # Output: True

# Define a function to use in foreach
def add_to_accumulator(x):
    global acc
    acc.add(x)

# Create an RDD to use with the accumulator
rdd = sc.parallelize([1, 2, 3, 4])

# Use accumulator in an action (foreach) with the defined function
rdd.foreach(add_to_accumulator)  # Adds each element to the accumulator

# Print the final value of the accumulator after the operation
print(f"Final Accumulator Value after foreach: {acc.value}")  # Output: 25 (15 + 1 + 2 + 3 + 4)

# Check if the accumulator has been modified after the foreach operation
print(f"Accumulator is set after foreach: {acc.isSet}")  # Output: True

# Reset the accumulator (for custom types, if needed)
# Note: PySpark doesn't have a built-in `reset` for primitive accumulators, but you can manually reset it:
acc = sc.accumulator(0)  # Reset by reinitializing

# Print the reset value
print(f"Accumulator Value after reset: {acc.value}")  # Output: 0

# Stop the SparkContext
sc.stop()
```
