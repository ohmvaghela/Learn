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

