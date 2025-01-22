# Spring JPA

## - Dependencies
- hibernate-core 6.1.7
- spring-orm 6.1.16
- spring-tx 6.1.16
- mysql-connector-j 8.0.33
- c3p0 0.9.5.5
- spring-webmvc 6.1.16
- spring 5.0.0



## - Configuring
- Before first run
  - servlet-config.xml
    - add x55mlns:tx
    - add to xsi:schemaLocation : 2
  - creating transactions driver
    - tx:annotation transaction-manager="myTransactionManager"
  - To create this object we need its bean(class) to be loaded
    - <bean id="myTransactionManager" ...>
        <property name="session-factory" ref="session-factory" >
  - We need to create sessionFactory bean(class)
    - <bean id="session-factory" class="...">
    - properties
      - dataSource : DB connection info
      - packageToScan : Where are all entities (model classes)
      - hibernateProperties
        - hibernate.dialect : value depends on database 
        - hibernate.show_sql : true/false
  - We are left to create myDataSoruce object
    - <bean id="myDataSource" class="..." destroy-method="close">
    - properties
      - driverClass
      - jdbcUrl
      - user
      - password
      - ?minPoolSize
      - ?maxPoolSize
      - ?max5IdleTime