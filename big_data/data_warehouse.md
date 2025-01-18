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

## - Artifact / Objects
- Structural representation of data
- When a raw data is transfored after ETL it is called artifact
- Types of artifacts
  - Fact table (data table)
  - Dimension table (loopup table)
  - Aggregate table
  - Mini dimension table : Subset of dimension table
  - Helper table
  <hr>
- Some objects
  - Degenerate Dimension
    - These do not have a dimension table of its own
      - But these exist as attribute of fact table
      - As adding a new dimension table wont make sense
    - Examples include invoice numbers, order IDs, or transaction IDs
  - Junk data 
    - The data for whome having a specific dimension does not make sense
    - And also are low cardinaly
    - So are collected in a single table
    - Example : IsNewCustomer, IsPromotionalOrder, PaymentMethod
  - Confirmed Dimension
    - Dimension that is shared accross multiple fact table
  - Bridge table
    - Table used to manage manyToMany realation between fact and dimension table
  - Factless fact table
    - A table that does not contain any mesurable metric / Fact
    - but is used to record events or relationships.
- Hirerchy in data warehouse
  - Balanced
    - Depth of branches is same
    - Eg. Country->State->City->Locality
  - Unbalanced
    - Depth of branched may be different
    - There may be gaps in table
  
    | Organization | Level	| Employee Name	Reports To | 
    |-|-|-|
    | CEO	| Alice |	- |
    | VP	| Bob	| Alice |
    | Manager	| Charlie	| Bob |
    | Employee | David |	Charlie |

  - Ragged
    - When certial levels are skipped and there is gap in between

    | Country |	State |	City |
    | - | - | - |
    | USA |	California | Los Angeles | 
    | USA | - |	Washington DC |
    

- ## Schema used in data warehousing
- Tables
  - Fact Table
    - Stores metrics, measurements or facts
    - These are connected to dimension table
    - These store the primary key for dimension table
  - Dimension Table
    - These are non-denormalized tables
    - The Primary key of dimension table are stored in fact table as foriegn key
  - Some types of facts
    - Addtive facts
      - Facts that can be aggrigated accross all the dimension
      - Like sales, revenue
    - Semi Additive facts
      - Facts that can be aggrigated accross some dimensions
      - Account balance : Accross all dates is useless, it is taken for a specific time
      - Inventory level : cant be summed for time and for summed for all regions
    - Non Additive facts
      - Facts that can not be aggrigated
      - Like profit margin, Customer statisfaction score
       
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

- ## Data warehouse design technique
- Data mart
  - Subset of data warehouse
  - focus on specific business area, function or department like HR, marketing, finance etc. 
- Two types
  - Inmon(Top Down)
    - Data is collected at a place and then `data marts` are created  
  - Kimball (Bottom's up)
    - Here first `data marts` are created and then the data are clubed together
    - The data marts are connected via shared attributes and hence create a `dimentional data warehouse`

  | Inmon |Kimball|
  |-|-|
  | Enterprice foucs | Business focus |
  | More time consuming to craete | Simple to create |
  | Long delivery time | Less delivery time |
  | Simple maintainance | Complex maintainance |
  | More normarlized | More De-Normalized |
  | Slow query as it require more joins | 

- Slowly changing dimensions (SCD)
  - Dimension that go change under time
  - Like address of a person it may or may not change
  - Three ways to handle SCD
    - Type 1 : Replace the data 
    - Type 2 : Keep both data
    - Type 3 : Add a new column to keep track of data change
