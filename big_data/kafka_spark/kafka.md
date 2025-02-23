# Kafka
- Distributed event streaming platform
- Primarily used for real time data pipeline and streaming applications

![alt text](image.png)

## Core Componenets
- `Producer` : Publish message to kafka topics
- `Consumer` : Subscribe to a topic and processes/consumes the message
- `Broker` : Kafka Server, or the location where the events/messages are stored 
- `Topics` : Category to which messages are sent to 
- `Partition` : A topic is split into partitions, allowing parallel processing
- `Offset` : The unique ID of each message within a partition
- `Consumer` Group : A group of consumers working together to consume messages
- `Record` : Datatype used to store data in kafka. Record consists of
  - `Key`(Optional) : Used to determine which partition record will go to 
    - If not provided then random partition is selected using round-robin
  - `*Value`(Requred) : Actual data in any format
  - `TimeStamp`(Optional) 
  - `Header`(Optional) : Metadata of message
  - `Offset`(Auto-Assigned by kafka) : Unique ID of record in a partition, is incremental for each new record
- `Replication` : Replicas of partitions are good for scaling
  - `Leader Partition` 
    - If there are multiple replicas of partition then zookeeper creates a `leader partition`
    - Only on `leader partition` both read and write operation can be perfromed
  - `partition followers`(partition replicas)
    - Replicas are `partition followers` which support read-only operation
    - `partition followers` update themselves from `Leader partition`
    - all `Partition followers` and the `partition leader` each reside on different broker
### Other details about kafka
- Partitions of a Topics may or may not be stored on same broker
- all `Partition followers` and the `partition leader` each reside on different broker
- Zookeeper's responsibility
  - Broker managerment : Zookeeper maintains list of brokers
  - Handles dynamic addition and removal of brokers
  - Leader Election : Elects new partition leader if a broker goes down 
    - Zookeeper do not create new partition or broker, they just elect
  - Stores metadata of : topics, partitions, broker-partition-mapping and their status.

## Data flow in kafka
- Before producer sends `record` it asks for `topic's` required `partition leader` and on which `broker` that `partition leader` is located
- Then it sends data to the `partition leader` to be consumed
- Then the record on `partition leader` is replicated to `partition followers`
- Next consumer consumer consumes record

