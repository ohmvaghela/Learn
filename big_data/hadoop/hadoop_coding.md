# Hadoop in Ubuntu

- Finding base location of hadoop : $HADOOP_HOME

## Configuration files in hadoop
- Location of following files : `$HADOOP_HOME/etc/hadoop/`
- `hadoop-env.sh`
  - Sets environment variables for hadoop
- `core-site.xml`
  - Configure fundamentals like file system type and default hdfs location
  - key configs :
    - fs.defaultFS : defines file system URI(Unique Resource Identifier)
      - default it is `file://` : local file system
      - We have used hdfs : `hdfs://namenode:9000`
    - hadoop.tmp.dir : dir to store temperory files
      - Files like intermediate data processing files
      - Metadata and local caching
      - logs and system generated temp files
      - default value :  /tmp/hadoop-${user.name}
    - io.file.buffer.size : buffer size for read-write operations
- `hdfs-site.xml`
  - hdfs settings like replication-factor, NameNode/DataNode properties
  - key configs:
    - dfs.replication : replicas of each block (3 default)
    - dfs.namenode.name.dir : location of NameNode metadata will be stored
    - dfs.datanode.name.dir : dir where DataNode will store block data
    - dfs.permissions : controls hdfs permissions checks
      - default it is true : normal unix like permissions
      - false : allow all users to access
- `mapred-site.xml`
  - configure mapreduce execution framework
  - key configs
    - mapreduce.framework.name : execution mode (like yarn)
    - mapreduce.jobtracker.address : location of job tracker
    - mapreduce.job.reduces : number of reducers per task
- `yarn-site.xml`
  - yarn.resourcemanager.address : resource manager address like port 8050
  - yarn.nodemanager.resource.memory-mb : memory for NodeManager container

## Starting hadoop and yarn 
- `start-dfs.sh`, `start-yarn.sh`
- Listing java processes related to hadoop that are running
  - use jps (Java Process Status) to list all the deamons running
  - so generally these six deamons will be running and their PIDs will be mentioned
    - 33716 DataNode
    - 34644 Jps
    - 33958 SecondaryNameNode
    - 33500 NameNode
    - 23740 ResourceManager
    - 23919 NodeManager
   
## To check the NameNode, DataNode, Resource Manager
- NameNode : [`http://localhost:9870`](http://localhost:9870)
- Secoundary NameNode : [`http://localhost:9868`](http://localhost:9868)
- DataNode : [`http://localhost:9864`](http://localhost:9864)
- ApplicationMaster : [`http://localhost:8088`](http://localhost:8088)
- NodeManager :  [`http://<nodemanager-host>:8042`](http://<nodemanager-host>:8042)
- MapReduce JobHistory : [`http://localhost:19888`](http://localhost:19888)
- HDFS balancer : [`http://localhost:50070/balancer`](http://localhost:50070/balancer)


## Interacting with hadoop file system
- use `hdfs dfs` as base and enter command
- Like
  - ls -> `hdfs dfs -ls /path/to/dir`
  - rm -> `hdfs dfs -rm /path/to/dir`
  - mkdir -> `hdfs dfs -mkdir /path/to/dir/new_dir`
  - mkdir -> `hdfs dfs -mkdir -p /path/to/dir/new_dir`
  - copy file -> `hdfs dfs -put <source> <destenation>`
- misc
  - say you want to see last 10 lines of a file
    - `hdfs dfs -cat /file/path.txt | tail -n 10`

## Permissions in HDFS
- HDFS uses normal permission model like that of Unix ( POSIX(Portable Operating System Interface) )
- But for each user and each node we may need different permissions hence `ACL (Access Control List)` was introduced
- get permission for a file/dir
  - `hdfs dfs -getfacl <file/dir path>`
- set permission for a file/dir
  - `hdfs dfs -setfacl -m user:ohm:r-x <file/dir path>`
- remove all permissions
  - `hdfs dfs -setfacl -b <file/dir path>`
- set to default permission
  - `hdfs dfs -setfacl -k <file/dir path>`
 
## Safe mode and corrupt files
- When the NameNode starts it starts in safe-mode
- In this mode only read operations are allowed and write operations are not allowed
- When certain percentage of DataNode return with info hdfs exits safe-node
- To check the status of NameNode
  - `hdfs dfsadmin -safemode get`
- change status
  - `hdfs dfsadmin -safemode enter`
  - `hdfs dfsadmin -safemode leave`
- To check health of a file or block we can use fsck (file System Check)
  - `hdfs fsck /path/to/dir -files -blocks -locations`
- To find corrupt files
  - `hdfs fsck /path/to/dir -list-corruptfileblocks`

## Getting config details
- IP of NameNode 
  - `hdfs getconf -namenodes` -> `localhost`
- IP address with port 
  - `hdfs getconf -confKey fs.defaultFS` -> `localhost:9000`
- IP address of other nodes

|||
|-|-|
|  [-namenodes] | gets list of namenodes in the cluster. |
|  [-secondaryNameNodes] | gets list of secondary namenodes in the cluster. |
|  [-backupNodes] | gets list of backup nodes in the cluster. |
|  [-journalNodes] | gets list of journal nodes in the cluster. |
|  [-includeFile] | gets the include file path that defines the datanodes  that can join the cluster.|
|  [-excludeFile] | gets the exclude file path that defines the datanodes  that need to decommissioned.|
|  [-nnRpcAddresses] | gets the namenode rpc addresses |
|  [-confKey [key]] | gets a specific key from the configuration |

- To get report of blocks, datanode and number of datanodes
  - `hdfs dfsadmin -report`
  - Alternately check port 9870 for datanodes


## YARN config files (yarn-site.xml)

- NodeManger reports heart beat at this port

  ```xml
  <property>
    <name>yarn.resourcemanager.resource-tracker.address</name>
    <value>localhost:8025</value>
  </property>
  ```

- Main communication endpoint for job submission from client, ApplicationManager, container, node, nodemanager ...

  ```xml
  <property>
    <name>yarn.resourcemanager.address</name>
    <value>localhost:8050</value>
  </property>
  ```

- Call to schedular for allocating resource

  ```xml
  <property>
      <name>yarn.resourcemanager.scheduler.address</name>
      <value>localhost:8035</value>
  </property>
  ```

- First configures the shuffle service and second instructs nodemanager to use it

  ```xml
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
  </property>
  <property>
      <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
      <value>org.apache.hadoop.mapred.ShuffleHandler</value>
  </property>
  ```

## YARN Request Flow

- Client Submits a Job to ResourceManager  
  - Client sends a request to **`yarn.resourcemanager.address`** (`8050`).  
  - RM validates the request and assigns an **ApplicationMaster (AM) container** on a NodeManager.  

- ResourceManager Uses the Scheduler for Resource Allocation  
  - RM **calls** `yarn.resourcemanager.scheduler.address` (`8035`) to determine how much resource should be allocated.  
  - **Scheduler does NOT allocate resources directly**—it only decides based on policies (FIFO, Fair, Capacity).  

- Scheduler Passes Instructions to ResourceTrackerService  
  - Scheduler **informs** ResourceTrackerService (`8025`) about which NodeManager should allocate resources.  
  - **ResourceTrackerService is responsible for updating NodeManagers**.  

- NodeManagers Report to ResourceTrackerService  
  - NodeManagers **continuously send heartbeats** to RM at `yarn.resourcemanager.resource-tracker.address` (`8025`) to report available resources.  
  - RM selects an appropriate NodeManager for allocation.  

- ApplicationMaster Negotiates Resources with RM  
  - Once the first container is allocated, AM starts running on a NodeManager.  
  - AM **requests more resources** from RM by contacting `yarn.resourcemanager.address` (`8050`).  

- Scheduler Decides Further Allocations  
  - Scheduler **again** processes the request and instructs the ResourceTrackerService (`8025`) to allocate additional resources.  

- Containers Are Launched, Start Reporting to NM & AM  
  - NodeManagers start containers, which:  
    - **Report health to their respective NodeManager**.  
    - **Send progress updates to their respective ApplicationMaster**.  

- ApplicationMaster Reports Completion to RM  
  - Once the job is finished, AM **informs ResourceManager at `yarn.resourcemanager.address` (`8050`)**.  
  - RM **notifies the client** about the job status.  

## YARN application management commands
| Action | Command |
|-|-|
| To monitor using UI                   | [http://localhost:8088/cluster](http://localhost:8088/cluster)           |
| List applications running             | `yarn application -list`                                                |
| List applications (all states)        | - `yarn application -list -appStates ALL`<br>- `yarn application -list -appStates RUNNING`<br>- `yarn application -list -appStates FINISHED`<br>- `yarn application -list -appStates FAILED` |
| List nodes                            | `yarn node -list`                                                       |
| Get status of an application          | `yarn application status <application-id>`                              |
| Kill an application                   | `yarn application kill <application-id>`                                |
| Get logs of an application            | `yarn logs -applicationId <application-id>`                             |

## Different Schedualars in yarn
- There are three types of schedular
  - FIFO (default)
  - Capacity schedular : different queues can have different capacity
  - Fair Schedular : priority bases, dynamic allocation
- ### FIFO (Default Schedular)

    ```xml 
    <property>
        <name>yarn.resourcemanager.scheduler.class</name>
        <value>org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler</value>
    </property>
    ``` 
  ---
- ### Capacity Schedular
  - Queue1 gets 60% share of resource 
  - Queue2 gets 40% share of resource 
  - Change `yarn.scheduler.capacity.root.queues` in capacity-schedular.xml

    ```xml
    <property>
        <name>yarn.scheduler.capacity.root.queues</name>
        <value>queue1,queue2</value>
    </property>
    ```

  - Add following

    ```xml
    <property>
        <name>yarn.scheduler.capacity.root.queue1.capacity</name>
        <value>60</value>
    </property>
    <property>
        <name>yarn.scheduler.capacity.root.queue2.capacity</name>
        <value>40</value>
    </property>

    <queue name="root">
        <capacity>100</capacity> 
        <weight>1</weight> 
        <queue name="queue1">
            <capacity>60</capacity> 
            <maximum-capacity>70</maximum-capacity> 
            <minResources>4GB,4vcores</minResources> 
        </queue>
        <queue name="queue2">
            <capacity>40</capacity> 
            <maximum-capacity>50</maximum-capacity>
            <minResources>2GB,2vcores</minResources>
        </queue>
    </queue>
    ``` 

## Running Job

- We example job which is present at `$HADOOP_HOME/share/hadoop/mapreduce/hadoop...exmaple...jar`
- Example 1 : counting value pi
  - ` yarn jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop...exmaple...jar pi 4 1000`
  - 4 is number of mappers
  - 1000 sample size
- Word count
  - say a file named input.xyz exist at /someplace/
  - `yarn jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop...exmaple...jar wordcount /someplace/input.xyz /someplace/output_dir`
  - To check the job output
    - `hdfs dfs -cat /someplace/output_dir/part-r-00000`
- Running job with custom parameters with are different from default params
  - Changing number of reducers
    
    ```
    hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.4.jar \
      wordcount \
      -D mapreduce.job.reduces=3 \
      /user/hadoop/input /user/hadoop/output
    ```

  - Other config properties

  | Propery | Description | Default |
  |-|-|-|
  | `mapreduce.job.name=<name>`  | Set job name | `job_xxx` | 
  | `mapreduce.job.priority=<LOW/NORMAL/HIGH>`  | set job priority  | NORMAL  |
  | `mapreduce.output.basename=<name>`  | change the output name  | `part`  |
  | `mapreduce.job.reduces=<num>`  | number of reducers  | autocalculated  |
  | `mapreduce.map.memory.mb=<MB>`  | memory per mapper container | 1GB  |
  | `mapreduce.reduce.memory.mb=<MB>`  | memory per reducer container | 1GB  |
  | `mapreduce.map.vcore=<number>`  | CPU per mapper | 1  |
  | `mapreduce.reduce.vcore=<number>`  | CPU per reducer | 1  |
  | `dfs.blocksize=<bytes>`  | HDFS block size | 128 MB  |
  | `dfs.replications=<number>`  | number of replicas  | 3  |
  | `mapreduce.output.fileoutputformat.compress=<true/false>`  | compress the output file  | false  |
  | `mapreduce.job.queuename=<queue>`  | submit job to perticular queue  | default  |
