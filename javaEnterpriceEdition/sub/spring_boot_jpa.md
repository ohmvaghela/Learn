# Spring Boot JPA

## Dependencies
- MYSQL driver
- Spring Data JPA

## Application.Properties

  ```
  spring.application.name=springbootjpa

  spring.mvc.view.suffix=.jsp
  # spring.mvc.view.prefix=

  spring.datasource.url=jdbc:mysql://localhost:3306/temp

  spring.jpa.show-sql=true

  spring.datasource.username=root
  spring.datasource.password=ohm123ohm

  spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
  ```

## JpaRepository
- It is an interface that provides CRUD operations with needing any sql queries
- For using it we just need to extend it and provide entitiy class and primary key as shown

  ```java
  package com.mechsimvault.springbootjpa;

  import com.mechsimvault.springbootjpa.Student;
  import org.springframework.data.jpa.repository.JpaRepository;

  public interface  StudentRepo extends JpaRepository<Student, Integer>{
    
  }
  ```

- If primary key is multiple attributes then we have to create a new `@Embeddable` class
- Say Student table has 
  - two attributes as PK : (id, schoolcode),  
  - Other attri(name, age)
- Then we will create one `@Embeddable` class with id and school code

  ```java
  @Embeddable
  public class StudentId implements Serializable {
      private Integer id;
      private String schoolCode;
  }
  ```
- And then create `Student` entity with StudentId embedded

  ```java
  @Entity
  public class Student {
      @EmbeddedId
      private StudentId studentId;  // Composite primary key
      private String name;
      private int age;
  }
  ```

- Now the StudentRepo will look like 

  ```java
    public interface StudentRepo extends JpaRepository<Student, StudentId> {}
  ```

## Using JpaRepositroy to get data
- We can get the StudentRepo without importing it by using DI  `@Autowired` 

  ```java
  @Autowired
  StudentRepo studentRepo;
  ```

- It provided with some basic CRUD operations like
  - `save(S entity)` – to save an entity.
  - `findById(ID id)` – to find an entity by its ID.
  - `findAll()` – to get all entities.
  - `deleteById(ID id)` – to delete an entity by its ID. 

- `findAll()` returns list
- But `findbyId()` returns `Optioanl<T>`

```java
package com.mechsimvault.springbootjpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController{
  
	@Autowired
	StudentRepo studentRepo;

  @RequestMapping("/students")
  public String students(Model m){

    List<Student> st = studentRepo.findAll();
    
    m.addAttribute("students", st);

    return "students";
  }
  
  @PostMapping("/students")
  @ResponseBody
  public List<Student> students() {
    return studentRepo.findAll(); // Return the list of students as JSON
  }

  @PostMapping("/student")
  @ResponseBody
  public Student student(){
    Optional<Student> st =  studentRepo.findById(1);
    
    return st.orElse(null);
  }

}
```

## Creating custom methods
- We can craete custom abstract methods without implementing it 
- It has a naming convention which if followed then works right
  - like if we want to find by an attribute then we can use 
    - `findBy<attribute-name>(<Attribute-type> Attribute)`
    - `findBy<atr1>And<atr2>(<atr-type-1> Atr1,<atr-type-2> Atr2)`

      ```java
      public interface StudentRepo extends JpaRepository<Student, Integer> {
          // Automatically creates a query to find students by their name
          List<Student> findByName(String name);

          // Automatically creates a query to find students by their ID and name
          Student findByIdAndName(Integer id, String name);
      }
      ```

## Using custom query 
- Note the query is JPQL(Java Persistence Query Language) and not SQL

  ```java
  public interface StudentRepo extends JpaRepository<Student, Integer> {

      @Query("SELECT s FROM Student s WHERE s.name = ?1 AND s.age = ?2")
      List<Student> findByCustomQuery(String name, int age);
  
  }
  ```

- If we want to take input as params

  ```java
  @Query("SELECT s FROM Student s WHERE s.id = :id")
  Student getStudentByCustomId(@Param("id") int id);
  ```

- If we want to run native SQL query we need to mention 

  ```java
  public interface StudentRepo extends JpaRepository<Student, Integer> {

    @Query(value="Select * from Student where id=?1", nativeQuery=true)
    Student getNativeStudent(int id);
  
  }
  ```


