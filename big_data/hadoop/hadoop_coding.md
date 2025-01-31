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
      - default it is file:// : local file system
      - We have used hdfs : hdfs://namenode:9000
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
- NameNode : http://localhost:9870
- DataNode : http://localhost:9864
- ApplicationMaster : http://localhost:8088

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

