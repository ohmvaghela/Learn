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

### Combine implementation is given below

```java
package com.mechsimvault.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class StudentUtil {
    private static SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
```

```java
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
            session.close();
            StudentUtil.shutdown();
        }
    }
}
```
