# Types of database
1. Relational database
    -  Classic database with tables and relations between entites of table
    - Most mature database
    - Easily represntable
    - Difficult when horizontally scaling
2. Hierarchial Database
    - Tree like sturcture of statbase
    - Each parent entity has single or multiple childs
    - Example 
3. Network database
    - Similar to Hierarchial database
    - But can have multiple parents
4. NoSQL database
    - Non relational database
    - Flexible schema
    - Types
        1. Key value pair
            - Data is stored in key value pairs
            - Key can be anything like string, int, object, or anyother data structure
        2. Document
            - Each document has a unique key 
            - Each doc may have nested docs with unique key
            - JSON format
            - Higly sclable
        3. Column based
            - Instead of row based it is column based
            - Each row has unique id and each row have columns which may differ (i.e. flexible schema)
            - Easily scalable
        4. Graph based
            - Each entity has id and is connected to other entity in graph based structure 
5. Object Oriented database
    - We have objects similar to classes in CPP
    - And we use methods to fetch data
    - Difficult to secure
    - Can handle complex data structure

# Horizontal v/s Verical Scaling

## Horizontal Scaling
- Increasing the instances of database or 