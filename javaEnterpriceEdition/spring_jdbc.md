# Spring JDBC

## - Main App code 

```java
package com.mechsimvault.springjdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringjdbcApplication implements CommandLineRunner {

	@Autowired
	private MyDatabaseService myDatabaseService;

	public static void main(String[] args) {
		SpringApplication.run(SpringjdbcApplication.class, args);
	}

	@Override
	public void run(String... args)throws Exception {
		myDatabaseService.runQuery();
	}
}
```

## - `application.properties`
```
spring.application.name=springjdbc

spring.datasource.url=jdbc:mysql://localhost:3306/temp
spring.datasource.username=root
spring.datasource.password=ohm123ohm
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

## - CommandLineRunner
- It is a FunctionalInterface and has abstract method `void run(String... args)` 
  - Functional interfaces are one which has only abstract method
  - Functional interfaces can have multiple static and default methods 
  - These three dots are called `...`->`var args`
  - These allow zero to multiple args to be passes and can be accessed using advance for loop
- It is used to run specific logic after the Spring boot applicaction has started
- Like in our case run query after the spring boot has started 

```java
package com.mechsimvault.springjdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MyDatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void runQuery() {
      String sql = "SELECT * FROM Student";
      try {
          jdbcTemplate.queryForList(sql).forEach(row -> {
              System.out.println(row);
          });
      } catch (Exception e) {
          e.printStackTrace(); // Log the error if something goes wrong
      }
  }
}

```

## - Using MyDataBaseService Without importing in main class
- `MyDataBaseService` is annotated as `@Service` which is just like component
- Hence when `@ComponentScan` runs it captures all the components

## - `JdbcTemplate`
- We use `JdbcTemplate` instead of `JDBC API`
  - This avoids lots of boilerplate code
  - template takes care of transactions 
  - template takes care of error handling 
  - template takes care of creating and closing connection objects 
- ### Basic Methods offered by JDBCTemplate
  1. `public int update(String query)`
      - Used update, insert and delete in DML
      - Returns number of rows affected
  2. `public int update(String query, Object... args)`
      - Used update, insert and delete in DML
      - Returns number of rows affected
      
      ```java
        String sql = "INSERT INTO employees (id, name, department) VALUES (?, ?, ?)";
        int rowsInserted = jdbcTemplate.update(sql, 102, "Alice", "HR");
      ```
  3. `public void execute(String query)`
      - DDL query execution
  4. `public <T> T execute(String sql, PreparedStatementCallback<T> action)`
      - This is used to get more hold on PrepareStatement
      
      ```java
      int count = jdbcTemplate.execute(sql, preparedStatement -> {
          try (ResultSet rs = preparedStatement.executeQuery()) {
              if (rs.next()) {
                  return rs.getInt(1); // Get the count
              }
          }
          return 0;
      });
      ```
  5. `public <T> T query(String sql, ResultSetExtractor<T> rse)`
    - Used to map the extracted resource using `ResultSetExtractor`
    - Like converting query to list of objects 
    
    ```java
    List<String> employeeNames = jdbcTemplate.query(sql, rs -> {
    List<String> names = new ArrayList<>();
    while (rs.next()) {
          names.add(rs.getString("name")); // Fetching names
      }
      return names;
    });
    ```
    ```java
    List<Employee> employees = jdbcTemplate.query(sql, (rs, rowNum) -> {
        Employee emp = new Employee();
        emp.setId(rs.getInt("id"));
        emp.setName(rs.getString("name"));
        emp.setDepartment(rs.getString("department"));
        return emp;
    });
    ```

- Dependencies required 
  - `spring-boot-starter-data-jdbc`
    - Simplifies database interaction
    - JdbcTemplate is part of this
  - `spring-boot-starter-data-jpa`
    - Used for hibernate 
    - Not used now
  - `mysql-connector-j`
    - Used for connection with MySQL
