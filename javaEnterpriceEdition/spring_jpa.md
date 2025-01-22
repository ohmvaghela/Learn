# Spring JPA

## - Dependencies
- hibernate-core 6.1.7
- spring-orm 6.1.16
- spring-tx 6.1.16
- mysql-connector-j 8.0.33
- c3p0 0.9.5.5
- spring-webmvc 6.1.16
- spring 5.0.0

## - Creating DAO
- Once the config.xml setup is complete, we can create a DAO to interact with the database.
  - SessionFactory Injection:
    - Since we have created a SessionFactory bean in the config.xml file, we can use `@Autowired` dependency injection to inject the `SessionFactory` into our DAO.
  - Transaction Management:
    - We have also created a transactionManager bean in the config.xml file. This allows us to manage transactions seamlessly using Spring. To handle transactions automatically, we annotate the DAO methods with `@Transactional`, so Spring can manage the transaction lifecycle.

    ```java
    import org.hibernate.Session;
    import org.hibernate.SessionFactory;
    import org.hibernate.query.Query;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.transaction.annotation.Transactional;
    import java.util.List;
    import com.mechsimvault.Student;

    @Component
    public class StudentDAO {

        @Autowired
        private SessionFactory sessionFactory;

        @Transactional // Ensures Spring manages the transaction for this method
        public List<Student> getStudents() {
            // Get the current Hibernate session
            Session session = sessionFactory.getCurrentSession();

            // Create a query to fetch all students
            Query<Student> query = session.createQuery("from Student", Student.class);

            // Execute the query and return the results
            return query.getResultList();
        }
    }
    ```

- Injecting DAO in Controller:
  - As we have annotated the DAO class with `@Component`, Spring will manage it as a bean. Using `@Autowired`, we can inject the DAO into the controller.

    ```java
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.RequestMapping;
    import java.util.List;
    import com.mechsimvault.Student;
    import com.mechsimvault.StudentDAO;

    @Controller
    public class HomeController {

        @Autowired
        private StudentDAO studentDAO;

        @RequestMapping("/")
        public String getStudents(Model model) {
            // Fetch students from the DAO
            List<Student> students = studentDAO.getStudents();

            // Add students to the model
            model.addAttribute("students", students);

            // Return the view name
            return "index";
        }
    }  
    ```


