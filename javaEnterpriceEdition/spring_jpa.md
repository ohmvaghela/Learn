# Spring JPA

## - Dependencies
- Hibernate-core
- Spring-orm
- Spring-tx (Spring Transaction)
- mysql-connector-java (mysql-connector-j)
- c3p0

## - Configuring
- Before first run
  - servlet-config.xml
    - add xmlns:tx
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
      - ?maxIdleTime