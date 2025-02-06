# Hive Implementation

## Starting hive
- After starting hadoop and yarn
- Use `hive --service metastore` to create hive session
  - Without this hive may start but may not work properly
  - If it does not work we may restart metastore
    - `schematool -dbType mysql -initSchema`
    - `hive --service metastore` 
- on another tab use `hive`
> Restart namenode `hdfs namenode -format`
## Context
  - [Basic Command](./hive_coding.md#basic-command)
  - [Creating table](./hive_coding.md#creating-table)
  - [Inserting data](./hive_coding.md#inserting-data)

## Basic command

- Listing CLI commands
  - `hive --help --service cli`
- `-e` : runnig direct script
  - `hive -e "SELECT * FROM my_table LIMIT 10"`
- `-f` : running HQL script
  - `hive -f /path/to/HQL_file.q`
  - Example HQL script

    ```sql
    -- script.q
    CREATE TABLE IF NOT EXISTS my_table (id INT, name STRING);
    LOAD DATA LOCAL INPATH '/path/to/data.txt' INTO TABLE my_table;
    SELECT * FROM my_table;
    ```

- `-i` : If there is an intialization script to run before query use 
  - `hive -i /path/to/initialization_script.q -e "SELECT * FROM my_table"`

- `-S` : If you want to run everything in backgroud use 
  - `hive -i /path/to/initialization_script.q -S -e "SELECT * FROM my_table"`


| Description | Command |  
|-|-|
| Showing database | `show databases` |
| set database | `use <database-name>` |
| Showing Tables | - `show tables from <database-name>` <br> - `show tables`|
| describe table | - `desc <database-name>.<table-name>` <br> - `desc <database-name>.<table-name>` |
| describe extended table | - `desc extended <database-name>.<table-name>` <br> - `desc   extended <table-name>` |

- ### DML commands are same
- ### `Delete` command wont work 

## Creating table
- Basic format

  ```sql
  -- database name : demo_db
  create table demo_db.employee (
    id int,
    username string,
    salary float 
  )
  row format delimited 
  fields terminated by ','
  lines terminated BY ';' -- optional by default '\n'
  stored as textfile; -- optional line by default 'textfile'
  ```

- ### Table properties

```sql
CREATE TABLE employees (id INT,name STRING,salary FLOAT)
STORED AS ORC
TBLPROPERTIES (
    'orc.compress'='ZLIB',
    'transactional'='TRUE',
    'skip.header.line.count'='1'
);
```
| command | description |
|-|-|
| comment		 | Stores a description of the table. |
| EXTERNAL		 | If TRUE, marks the table as an external table. |
| STORED AS		 | Specifies the file format (TEXTFILE, ORC, PARQUET, etc.). |
| transient_lastDdlTime	 | Stores the last modified time of the table in epoch format. |
| skip.header.line.count | Skips a specified number of header lines while reading. |
| skip.footer.line.count | Skips a specified number of footer lines while reading. |
| transactional		 | If TRUE, enables ACID properties. |

  ---

- ### Options
  - ### Row format

    - ROW FORMAT DELIMITED  
      - Used with plain text files where fields and rows are separated by a special character.  
      - Usually used with `FIELDS TERMINATED BY` and `LINES TERMINATED BY`.

    - ROW FORMAT SERDE  
      - Used for files with special formats, like JSON.  
      - Example for Avro format:

        ```sql
        ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe'
        WITH SERDEPROPERTIES ('avro.schema.url'='hdfs://path_to_schema')
        ```

    - ROW FORMAT INPUTFORMAT and OUTPUTFORMAT  
      - Example for ORC format:

        ```sql
        INPUTFORMAT 'org.apache.hadoop.hive.ql.io.orc.OrcInputFormat'
        OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat'
        ```

  - ### Storage format

    | Code | description |
    |-|-|
    | stored as orc | ORC Format (Optimized for performance in Hive) |
    | stored as parquet | Columnar storage |
    | stored as textfile | Columnar storage |
  
    - When we say stored as text file hive tells HDFS to store data 
    - And `hive metadata store` stores the metadata of table
  
  - ### Storage location 
    - Internal storage
      - If nothing is mentioned while creating table then they are stored at `/user/hive/warehouse`
      - and these are hive managed hence are not flexible to share wiht other tools like `pig`
    - External storage
      - Store table externally we need to use `location` keyword
        ```sql
        create external table emplist (Id int, Name string , Salary float)  
        row format delimited  
        fields terminated by ','   
        location '/HiveDirectory';  
        ``` 
    
## Inserting data 

- Inserting data from local
  
  ```sql
  load data local inpath '/path/to/file' into table <db-name>.<table-name>
  ```

- Inserting data from hadoop (remove local keyword)
  
  ```sql
  load data inpath '/path/to/file' into table <db-name>.<table-name>
  ```

- Inserting array and structs
  - Use `array()` to add array
  - Use `named_struct()` to create struct

    ```
    INSERT INTO employee_data VALUES (101, "John", ARRAY("Java", "SQL", "Python"));
    INSERT INTO student_marks VALUES (1, ARRAY(NAMED_STRUCT("subject", "Math", "score", 90), NAMED_STRUCT("subject", "Science", "score", 85)));
    ```

## Exporting data

- Exporting data from hive to local storage

  ```sql
  INSERT INTO LOCAL DIRECTORY '/path/to/local/directory'
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY '\t'
  SELECT * FROM your_table;
  ```
- Exporting data from hive to hdfs

  ```sql
  INSERT OVERWRITE DIRECTORY '/user/hive/warehouse/output_dir'
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY ','
  SELECT * FROM sales;
  ```


## Partitioning and Bucketing

- ### Partitioning 
  - Keys to partition must be defined during table creation 

    ```sql
    CREATE TABLE sales (id INT, amount DOUBLE)
    PARTITIONED BY (year INT, month INT)
    STORED AS ORC;
    ```

- ### Static Partitioning
  - Defining the keys explicitly on which partitioning should be done while loading data   

    ```sql
    --- Method 1 ---
    LOAD DATA INPATH '/data/sales_2024_01.csv' 
    INTO TABLE sales PARTITION (year=2024, month=1);
    --- Method 2 ---
    INSERT INTO sales1 PARTITION (year=2024, month=1) 
    SELECT id, amount, year, month FROM sales;
    --- Method 3 ---
    INSERT INTO TABLE sales PARTITION (year=2025, month=3) 
    VALUES (something, 2025, 3);
    ```

  - Validation wont happen in method 1 and method 2
  - Validation will happen in method 3
    - Following will throw error

    ```sql
    --- Method 3 ---
    INSERT INTO TABLE sales PARTITION (year=2025, month=3) 
    VALUES (something, 2023, 3);
    ```

  - But if tries to retirve data with method 1,2 using where clause it wont retrieve all values
    - As few values recide in different partiton 


- ### Dynamic Partitioning
  - If the keys are not defined then hive takes care of partitioning dynamically
  - before loading the data via dynamic partitioning we must specify
  
    ```sql
    SET hive.exec.dynamic.partition=true;
    SET hive.exec.dynamic.partition.mode=nonstrict;
    ```

  - example of loading data

    ```sql    
    LOAD DATA INPATH '/data/sales_2024_01.csv' 
    INTO TABLE sales PARTITION (year, month);
    ---
    INSERT INTO sales1 PARTITION (year, month)  
    SELECT * FROM sales;
    ```

- Bucketing
  - Dividing data into fixed number of equal sized files based on key/s

  ```sql
  CREATE TABLE sales_bucketed (id INT, amount DOUBLE)
  CLUSTERED BY (id) INTO 10 BUCKETS
  STORED AS ORC;
  ```

  - Before adding data to bucket table use the following

    ```sql
    SET hive.enforce.bucketing = true;
    ```

  - Reason 
    - Hive does not enforce bucketing so to write data according to hash function we need to tell hive to do it 
    
  ```sql
  INSERT INTO orders_bucketed 
  SELECT * FROM orders;
  ```

- ### Viewing data in clusted and partitioned files
  - Using `INPUT__FILE__NAME`, we can see the file in which data is been stored
  ```sql
  SELECT *, INPUT__FILE__NAME FROM <table_name>;
  ```

