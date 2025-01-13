# Big Data

- ## Data Warehosue
- A structured non-volatile single Source of data Truth
  - Structuring all the best data in one place
  - Used `data reporting` and `data analytics`
- Data Warehosue is 
  - Subject Oriented : Data stored is around a subject like credit card detail  
  - Integrated : Standards are maintained for data mapping etc..
  - Time Varient : Can also contain historial data like 5-10 years old
  - Non Volatile : Data only flows in, it is not updated or deleted
  - Summarized : Data comming is processed
> - ### A data warehouse is not strictly read-only, but it’s optimized for read-heavy workloads rather than frequent writes or updates.



- ## ETL (Extraction Tranformation Load)
- `Extraction` : The data is extracted from the databases
- `Transformation` : Unwanted data is brushed off, and tables are transfored to be stored in datawarehouse
  - It invloves steps like Aggregation, Normalization
- `Load` : Refined data is transfered to data warehouse

- ## OLTP (Online Transaction Processing system)
  - And data generated is stored in `OLTP databases`
  - These are serving customers on daile basis
  - Hence are mission critical system
  - They must be fast and are up all the time
- ## OLAP (Online Analytical Processing System)
  - Then the data is processed and stored in database called `OLAP database`
  - They are in house, and are not needed to serve customers
  - They are used for data analytics

- ## Schema used in data warehousing
- Tables
  - Fact Table
    - Stores metrics, measurements or facts
    - These are connected to dimension table
    - These store the primary key for dimension table
  - Dimension Table
    - These are non-denormalized tables
    - The Primary key of dimension table are stored in fact table as foriegn key
1. Star Schema
   - One of the most straight-forward and simple design
   - Dimension tables are not connected to each other directly
   - There is a single fact table and it is connected to multiple Dimension tables
   - Star schema creates denormalized dimension tables
2. SnowFlake Schema
   - Same as Star schema but dimension tables can have sub tables of its own
   - Hence these schema can have highly denormalized tables
   - Single fact table exist
3. Galaxy Schema
  - It can have multiple fact table
  - can reduces redundancy to near zero redundancy as a result of normalization
  - A dimension table can have multiple connected fact table

| **Aspect**             | **Star Schema**                                                                 | **Snowflake Schema**                                                                                       | **Galaxy Schema**                                                                                         |
|-------------------------|----------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| **Elements**           | Single Fact Table connected to multiple dimension tables with no sub-dimension tables | Single Fact Table connects to multiple dimension tables that connects to multiple sub-dimension tables       | Multiple Fact Tables connect to multiple dimension tables that connect to multiple sub-dimension tables   |
| **Normalization**      | Denormalized                                                                    | Normalized                                                                                                 | Normalized                                                                                                |
| **Number of Dimensions** | Multiple dimension tables map to a single Fact Table                            | Multiple dimension tables map to multiple dimension tables                                                | Multiple dimension tables map to multiple Fact Tables                                                    |
| **Data Redundancy**    | High                                                                            | Low                                                                                                       | Low                                                                                                       |
| **Performance**        | Fewer foreign keys resulting in increased performance                            | Decreased performance compared to Star Schema due to higher number of foreign keys                         | Decreased performance compared to Star and Snowflake. Used for complex data aggregation.                  |
| **Complexity**         | Simple, designed to be easy to understand                                       | More complicated compared to Star Schema—can be more challenging to understand                             | Most complicated to understand. Reserved for highly complex data structures                               |
| **Storage Usage**      | Higher disk space due to data redundancy                                        | Lower disk space due to limited data redundancy                                                           | Low disk space usage compared to the level of sophistication due to the limited data redundancy           |
| **Design Limitations** | One Fact Table only, no sub-dimensions                                          | One Fact Table only, multiple sub-dimensions are permitted                                                | Multiple Fact Tables permitted, only first-level dimensions are permitted                                 |



- ## Using TALEND
- Demo 1
  1. Creating job
  2. Adding TRowGenerator, TLogGenerator
  3. Open TRowGenerator
  4. Add columns
  5. Exit and run
- Demo 2
  1. 

# TO LEARN
- Data warehouse basic concept
- OLAP Online analytics processing
- ETL Extraction Transforamtion Load
- Volative and Non-Volatile data in data warehose
- Is datawarehouse read-only?
- OLTP v/s OLAP v/s Datawarehousing (In depth)
- Types of model in ppt
  - Star schema
  - Snow flake schema
  - Galaxy schema
- DW approch 
  - Kimball v/s Inmon
- DW schema
- Fact tables
- Artifact - Transaction w.r.t artifact
- Dimensions in DataWarehousing
- Data warehousing object 
  - Degenerate Dimension
  - Junk Dimension
  - Confirmed dimension
  - Hirerchies
- Data extraction w.r.t ETL
- Delta extraction w.r.t ETL
- ETL representation
- ETL the bigger prcture
- ETL v/s ELT
