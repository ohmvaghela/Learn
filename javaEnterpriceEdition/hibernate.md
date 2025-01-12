# Hibernate
- It is ORM(Object Realation Mappong) tool
- Like say we have a object and we want to store it database
  - So instead of writing of SQL, or NoSQL commands we just use the save() method
  - Same is for fetching data
  - As we dont want to write SQL code in java files

## Connecting with database
1. Create instnace of `configuration`
  - `new Configuration()`
  - This stored all the detials for the connectivity of db 
  - But currently it is empty
2. Fill it with details
  - `new Configuration().configure()`
  - Populate it with data
  - If nothing is mentioned in `configure` then takes defualt location and name of `src/main/hibernate.cfg.xml` 
    - Or else we need to mention `xml` location
3. Create a session instance to interact with DB
  - 
  
  ```java
    SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
  ```
  
  - As we have configuration details we need to create a session variable which will interact with DB and perform CRUD operations
  - `ExceptionInInitializerError` is the error thrown if there is problem with initializetion

4. Once we have a session we to start the connection
  - 
  
  ```java
  Session session = sessionFactory.openSession()
  ```

5. Operations in DB are in form of transactions 
  - So we first initiate transaction

    ```java
    Transaction transaction = session.beginTransaction();
    ```

6. Perform changes in DB

  ```java 
    Student student = new Student();
    student.setName("John Doe");
    student.setMarks(85);

    // Save the Student to the database
    session.save(student);
  ```

7. Commit Changes in db

  ```java
    transaction.commit();
  ```

  - Perform rollback if transaction fails

  ```java
    transaction.rollback();
  ```

8. After transaction is complete 
  - Close session and sessionFactory object
  
  ```java
  session.close();
  sessionFactory.close();
  ```

## - Key points to note
- In studentUtil `sessionFactory` is static object 
  - This is because `sessionFactory` is a heavy object and is used for entire session lifetime
  - Hence is initialized once using `static block` and used then onwards  
  - `shutdown()` method should StudentUtil is called once at the end of lifecycle 
  - These is a check in `StudentUtil.shutdown()` to check if the sessionFactory is not closed by any other process
    - If then dont try to close it 
    - This prevent `nullPointerException`


### Combine implementation is given below

```java
// StudentUtil.java
package com.mechsimvault.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class StudentUtil {
    private static SessionFactory sessionFactory;

    // Static initializer block to create the SessionFactory
    static {
        try {
            // Load configuration from hibernate.cfg.xml and build the SessionFactory
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception and throw an error if SessionFactory creation fails
            throw new ExceptionInInitializerError("SessionFactory creation failed: " + ex.getMessage());
        }
    }

    // Method to provide access to the SessionFactory
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // Method to close the SessionFactory
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
```

```java
// HelloServlet.java
package com.mechsimvault;

import com.mechsimvault.model.Student;
import com.mechsimvault.utils.StudentUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/home") // Maps this servlet to the /home URL
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Hibernate logic to save a student to the database
        Session session = StudentUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {
            // Create a new Student
            Student student = new Student();
            student.setName("John Doe");
            student.setMarks(85);

            // Save the Student to the database
            session.save(student);
            transaction.commit();

            // Respond to the client
            resp.setContentType("text/html");
            resp.getWriter().write("<h1>Student saved successfully!</h1>");
        } catch (Exception e) {
            transaction.rollback();
            resp.getWriter().write("<h1>Error saving student: " + e.getMessage() + "</h1>");
            e.printStackTrace();
        } finally {
            session.close(); // Close the session, but not the SessionFactory
        }
    }

    @Override
    public void destroy() {
        // Close the SessionFactory when the servlet is destroyed
        StudentUtil.shutdown();
    }
}
```

## - hibernate configuration file
- It should be named `hibernate.cfg.xml` and should be stored at `src/main/resources`
- Few params
  - `<property name="hibernate.hbm2ddl.auto">create</property>`
    - `create` : It will delete table if it exist and create table and do CRUD operations
    - `update` : If it craete table if does not exist or else use the existing table, and throw error if schema does not match
  - `<property name="hibernate.show_sql">true</property>`
    - If set to true then it will show the sql query in logs
  - `<mapping class="com.mechsimvault.model.Student"/>` 
    - This is used to add enities that needs to be mapped 
    - Another way is the tell the sessionfactory before creating the session 
    - From this 
      
      ```java
      // Load configuration from hibernate.cfg.xml and build the SessionFactory
      sessionFactory = new Configuration().configure().buildSessionFactory();
      ```
    
    - To this 
    
      ```java
      // Load configuration from hibernate.cfg.xml and build the SessionFactory
      Configuration configuration = new Configuration().configure();
      // Add annotated classes programmatically
      configuration.addAnnotatedClass(com.mechsimvault.model.Student.class);
      // Build the SessionFactory
      sessionFactory = configuration.buildSessionFactory();
      ```

### - Example of `hibernate.cgf.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
    "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
    "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <!-- Database connection settings -->
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/temp</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">ohm123ohm</property>

        <!-- JDBC connection pool settings -->
        <property name="hibernate.c3p0.min_size">5</property>
        <property name="hibernate.c3p0.max_size">20</property>

        <!-- Hibernate settings -->
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>
        <property name="hibernate.hbm2ddl.auto">update</property>

        <mapping class="com.mechsimvault.model.Student"/>

    </session-factory>
</hibernate-configuration>
```

## - JPQL (Java Persistance Query Language) / HQL (Hibernate Query Language)
- It is a part of `JPA(Java Persistance API)`
- It is database independent and it interact with relations and enitites
- It creates query which are converted to SQL queries to interact with DB
- Eg. `SELECT e FROM EntityName e WHERE e.propertyName = :value`
- It works on relations like `oneToMany`, `oneToOne` etc.
- It is object oriented
- Here instead of `select * from entity` we use `from entity` when we want to fetch entire table
- Other select statements with where and having clauses and other are same
- For complex queries we should stick to traditional sql queries coz this is abstraction

| **Feature**                | **HQL**                              | **SQL**                             |
|----------------------------|--------------------------------------|-------------------------------------|
| **Query Target**            | Entities (Java objects)              | Tables in the database             |
| **Column/Field Names**      | Property names of Java classes       | Column names in the database       |
| **Joins**                   | Uses object associations (e.g., `inner join e.department`) | Uses table joins (e.g., `INNER JOIN` between tables) |
| **Database Independence**   | More abstract, independent of DBMS   | Tied to a specific database’s SQL dialect |
| **Syntax**                  | Object-oriented, uses Java class/field names | Traditional SQL with table/column names |


## - Creating entity of object
- The class should be a `bean` class
  - i.e. it should have private variables 
  - And public getters and setters to fetch and update the values
- Annotations
  - `@Entity`
    - Used to define this class is an entity
    - `(name="...")` : Used to specify entity name to be used by JPQL
  - `@Table`
    - Used to define the table
    - `(name="...")` : Used to specify table name for mysql
  - `@Id`
    - Used to define primary key
  - `@Column(name="")`
    - Used to define cloumn name for the variable
  - `@Transient`
    - Used to skip the column 
    - Column will be skiped when creating talble
  - 
    <details >

      <summary> code till now </summary>

      ```java
        package com.mechsimvault.model;

        import jakarta.persistence.Entity;
        import jakarta.persistence.Column;
        import jakarta.persistence.Table;
        import jakarta.persistence.Transient;
        import jakarta.persistence.GeneratedValue;
        import jakarta.persistence.GenerationType;
        import jakarta.persistence.Id;
        import com.mechsimvault.model.StudentName;

        @Entity(name="student_entity")
        @Table(name="student_table")
        public class Student {
            @Id
            private String Name;
            @Transient
            private int marks;
            // private StudentName studentName;

            public String getName() {return Name;}
            public int getMarks() {return marks;}
            // public StudentName getStudnetName() {return studentName;}

            public void setName(String Name) {this.Name = Name;}
            public void setMarks(int marks) {this.marks = marks;}
            // public void setStudentName(StudentName studentName) {this.studentName = studentName;}
        }
      ```

    </details>
  
  - `@Embeddable` 
    - Say I have name divided into 3 parts first, middle and last
    - I craete a new class for name but I want to store it in same table
    - Embeddable is used for the same 
    - 
      <details>

        <summary> embeddable code </summary>

      ```java
        package com.mechsimvault.model;

        import jakarta.persistence.Entity;
        import jakarta.persistence.Column;
        import jakarta.persistence.Table;
        import jakarta.persistence.Transient;
        import jakarta.persistence.GeneratedValue;
        import jakarta.persistence.GenerationType;
        import jakarta.persistence.Id;
        import jakarta.persistence.Embeddable;

        @Embeddable 
        public class StudentName {
          private String fname;
          private String mname;
          private String lname;

          public String getFname(){ return fname; }
          public String getMname(){ return mname; }
          public String getLname(){ return lname; }

          public void setFname(String fname){this.fname = fname;}
          public void setMname(String mname){this.mname = mname;}
          public void setLname(String lname){this.lname = lname;}

        }

      ```

      </details>

## - Relations in Hibernate
- Types
  - OneToOne
  - OneToMany
  - ManyToOne
  - ManyToMany
> Dont forget to import the type of relation
- We just need to mention on the top of foriegn key the type of relation 
  ### - OneToOne
  ```java
  import jakarta.persistence.OneToOne;
  public class Student{
    @OneToOne
    private Laptop laptop;
  }
  ```
  
  ### - OneToMany
    - Here new table will be created to map oneToMany relatio

      ```java
      import java.util.ArrayList;
      import java.util.List;
      import jakarta.persistence.OneToMany;
      public class Student{
        @OneToMany
        private List<Laptop> laptop = new ArrayList<Laptop>(); 
      }
      ```

    - If we want to avoid this we need to define a column in Laptop named student
      - And also tell student that mapping is done by 
        - Laptop class's attribute named studnet
     
      ```java
      public class Student{
        @OneToMany(mappedBy="student")
        private List<Laptop> laptop = new ArrayList<Laptop>(); 
      }

      import jakarta.persistence.OneToMany;
      public class Laptop{
        @ManyToOne
        private Student student;
      }
      ```
  
  ### - ManyToMany
    - Say each student and have many laptop access and a laptop can be used by multiple students
    - Here If mapped by is not mentioned then two tables will be created 
      - Student_Laptop
      - Laptop_Student
    - 
    <details>
      <summary> code  </summary>

    ```java

      @Entity
      public class Laptop{
        @Id
        private int laptopId;
        private String laptopName;
        @ManyToMany(fetch=FetchType.EAGER)
        private List<Student> student = new ArrayList<Student>();

        public void setLaptopId(int laptopId){this.laptopId = laptopId;}
        public void setLaptopName(String laptopName){this.laptopName = laptopName;}
        public void setStudent(ArrayList<Student> student){this.student = student;}

        public int getLaptopId(){return laptopId;}
        public String getLaptopName(){return laptopName;}
        public List<Student> getStudent(){return student;}
      }

      @Entity(name="student_entity")
      @Table(name="Student")
      public class Student {
          @Id
          private String Name;
          private int marks;
          private StudentName studentName;
          @ManyToMany(mappedBy="student")
          private List<Laptop> laptop = new ArrayList<Laptop>();

          public String getName() {return Name;}
          public int getMarks() {return marks;}
          public StudentName getStudentName() {return studentName;}
          public List<Laptop> getLaptop() {return laptop;}

          public void setName(String Name) {this.Name = Name;}
          public void setMarks(int marks) {this.marks = marks;}
          public void setStudentName(StudentName studentName) {this.studentName = studentName;}
          public void setLaptop(ArrayList<Laptop> laptop) {this.laptop = laptop;}


      }

    ```

    </details>


## - Fetch Types
- Say A table has foriegn key, so at the time of fetch
  - If one table is fetched then wether other table will be fetched or not is decided by FetchType
  - `fetch=FetchType.EAGER`
    - Fetch all the tables in relation to this table
  - `fetch=FetchType.LAZY`
    - Only fetch this table

## - Caching
- There are two types of cache in hibernate (in ORM geneally)
  - `First level caching` : which is implemented in the ORM itself
  - `Second level caching` : Done by other 3rd party library
- Difference in primary and secondary caching
  - Primary caching is limited to session, once the session is closed the cache is lost
  - Second level caching is not limited to session
  - Second level caching is shared among multiple sessions
  - Hence consistency is difficult maintined in `second level caching`



| Aspect               | First Level Cache (L1)                        | Second Level Cache (L2)                              |
|----------------------|-----------------------------------------------|------------------------------------------------------|
| **Scope**            | Per session                                   | Across sessions (shared by SessionFactory)           |
| **Lifetime**         | Exists only during the session                | Persists until the SessionFactory is closed          |
| **Default**          | Enabled by default                            | Disabled by default (needs configuration)            |
| **Eviction**         | Automatically cleared when session ends       | Can be configured with time-to-live, eviction policies|
| **Cache provider**   | Not configurable                              | Can use external cache providers like EhCache, Infinispan |
| **Visibility**       | Only available within the same session       | Available across multiple sessions                   |
| **Usage**            | Caches entities during a single session      | Caches entities, collections, and queries across multiple sessions |


### - First level cache
- Say In a session we used same query twice, then it will only be executed once

```java
Student get_student1 = (Student) session.get(Student.class, "Doe");
Student get_student2 = (Student) session.get(Student.class, "Doe");
```

- This is log

```
Hibernate: 
    select
        s1_0.Name,
        s1_0.marks,
        s1_0.fname,
        s1_0.lname,
        s1_0.mname 
    from
        Student s1_0 
    where
        s1_0.Name=?
```

- But if we close the session and open it again and run the same query then 
  - They query will run two times 

```java
Session session1 = StudentUtil.getSessionFactory().openSession();
Transaction transaction = session1.beginTransaction();
Student get_student1 = (Student) session1.get(Student.class, "Doe");
transaction.commit();
session1.close(); // Close the session, but not the SessionFactory

Session session2 = StudentUtil.getSessionFactory().openSession();
Transaction transaction = session2.beginTransaction();
Student get_student1 = (Student) session2.get(Student.class, "Doe");
transaction.commit();
session2.close(); // Close the session, but not the SessionFactory
```
- In the above case query will be fired twice
- But if we configura second level cache it wont be fired twice

### - Second level cache
- Stratergies for second level cache `CacheConcurrencyStratergy`
  - NONE : No caching
  - NONSTRICT_READ_WRITE : Read-Write without locking
  - READ_ONLY : Read only cache, no write or update
  - READ_WRITE : Read_write with soft lock
  - TRANSACTIONAL : Read_write with hard lock
  > - Soft lock : Concurrent read, blocks write
  > - Hard lock : blocks read and write during lock

## - JPA (Java Persistance API)
- It was introduced as standard for ORM's
- So if developers had written code in a perticular ORM 
  - And they want to change ORM then the code base will be same
- It is nearly same as hibernate ORM 
- All we need to do is create a persistance ORM in `/resources/META-INF/persistence.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://java.sun.com/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd">
    
    <persistence-unit name="student-pu" transaction-type="RESOURCE_LOCAL">
        <class>com.mechsimvault.model.Student</class>
        <properties>
            <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/mydb"/>
            <property name="javax.persistence.jdbc.user" value="root"/>
            <property name="javax.persistence.jdbc.password" value="password"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

```java
package com.mechsimvault.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.mechsimvault.model.Student;

public class StudentCRUD {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("student-pu");
    private static EntityManager em = emf.createEntityManager();

    // Create
    public static void createStudent() {
        Student student = new Student();
        student.setName("John Doe");
        student.setMarks(85);

        em.getTransaction().begin();
        em.persist(student);  // Save student to database
        em.getTransaction().commit();

        System.out.println("Student created: " + student);
    }

    public static void main(String[] args) {
        createStudent();
    }
    // Read
    public static void readStudent(Long studentId) {
      Student student = em.find(Student.class, studentId);  // Fetch student by ID
      if (student != null) {
          System.out.println("Student found: " + student);
      } else {
          System.out.println("Student not found!");
      }
    }

}

```
