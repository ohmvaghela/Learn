# Spring Config file

- These are xml file used for initial config
- Can be imported in two ways 
  1. Using web.xml when called dispatcher (say my file's name is `spring-jpa-config.xml`)

      ```xml
        <servlet>
            <servlet-name>dispatcher</servlet-name>
            <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
            <init-param>
                <param-name>contextConfigLocation</param-name>
                <param-value>/WEB-INF/spring-jpa-config.xml</param-value>
            </init-param>
            <load-on-startup>1</load-on-startup>
        </servlet>

        <servlet-mapping>
            <servlet-name>dispatcher</servlet-name>
            <url-pattern>/</url-pattern>
        </servlet-mapping>

      ```

  2. Using application context in controller

      ```java
      ApplicationContext context = new ClassPathXmlApplicationContext("beanConfig1.xml","beanConfig2.xml",...);
      ```

## - Using config file to configure Dependency injection for hibernate and JPA

- Thing todo for creating hibernate connection and managing sessions and transactions
  - create a dataSource (Create DB connection)

      ```xml
        <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
          <property name="driverClass" value="com.mysql.cj.jdbc.Driver"/>
          <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/temp"/>
          <property name="user" value="root"/>
          <property name="password" value="ohm123ohm"/>
          
          <property name="minPoolSize" value="5"/>
          <property name="maxPoolSize" value="20"/>
          <property name="idleConnectionTestPeriod" value="3000"/>
          <property name="maxIdleTime" value="600"/>
        </bean>
      ```
    
  - Use that connection to create sesison using sesion factory

      ```xml
      <!-- Hibernate SessionFactory -->
      <bean id="sessionFactory" class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
          <property name="dataSource" ref="dataSource"/>
          <property name="packagesToScan" value="com.mechsimvault.entity"/> <!-- Package where your JPA entities are located -->
          <property name="hibernateProperties">
              <props>
                  <prop key="hibernate.dialect">org.hibernate.dialect.MySQLDialect</prop>
                  <prop key="hibernate.show_sql">true</prop>
                  <prop key="hibernate.format_sql">true</prop>
                  <prop key="hibernate.hbm2ddl.auto">update</prop>
              </props>
          </property>
      </bean>
      ```

  - Create Transaction manager so we dont need to take care of tranasctions

    - We need to provide spring what a `HibernateTransactionManager` object which will take care of tranascations

      ```xml
      <bean id="transactionManager" class="org.springframework.orm.hibernate5.HibernateTransactionManager">
          <property name="sessionFactory" ref="sessionFactory"/>
      </bean>
      ```
    
    - To use annotations like `@Transactional` we need to define it 

      ```xml
      <tx:annotation-driven transaction-manager="transactionManager"/>
      ```

## - Other configurations to enable MVC and Annotations and defining package to scan

  ```xml
  <context:component-scan base-package="com.mechsimvault.controller"/>

  <!-- Enable Annotation-Based Configuration -->
  <context:annotation-config/>

  <!-- Enable Spring MVC -->
  <mvc:annotation-driven/>
  ```




```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:context="http://www.springframework.org/schema/context"
      xmlns:mvc="http://www.springframework.org/schema/mvc"
      xmlns:tx="http://www.springframework.org/schema/tx"
      xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd
                           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">


    <context:component-scan base-package="com.mechsimvault.controller"/>

    <!-- Enable Annotation-Based Configuration -->
    <context:annotation-config/>

    <!-- Enable Spring MVC -->
    <mvc:annotation-driven/>

    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/"/>
        <property name="suffix" value=".jsp"/>
    </bean>


    <!-- DataSource Configuration (C3P0 Connection Pool) -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="com.mysql.cj.jdbc.Driver"/>
        <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/temp"/>
        <property name="user" value="root"/>
        <property name="password" value="ohm123ohm"/>
        
        <property name="minPoolSize" value="5"/>
        <property name="maxPoolSize" value="20"/>
        <property name="idleConnectionTestPeriod" value="3000"/>
        <property name="maxIdleTime" value="600"/>
    </bean>

    <!-- Hibernate SessionFactory -->
    <bean id="sessionFactory" class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="packagesToScan" value="com.mechsimvault.entity"/> <!-- Package where your JPA entities are located -->
        <property name="hibernateProperties">
            <props>
                <prop key="hibernate.dialect">org.hibernate.dialect.MySQLDialect</prop>
                <prop key="hibernate.show_sql">true</prop>
                <prop key="hibernate.format_sql">true</prop>
                <prop key="hibernate.hbm2ddl.auto">update</prop>
            </props>
        </property>
    </bean>


    <bean id="transactionManager" class="org.springframework.orm.hibernate5.HibernateTransactionManager">
        <property name="sessionFactory" ref="sessionFactory"/>
    </bean>

    <tx:annotation-driven transaction-manager="transactionManager"/>
</beans>

```
