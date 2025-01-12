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