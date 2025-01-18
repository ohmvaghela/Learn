# Spring

## Setting Up and Running a Spring Boot Application
- Use the `Spring Initializr` to generate a Spring Boot project (`Ctrl+Shift+P`).
- To run the project:
  1. Navigate to the project directory.
  2. Build and run it using the following commands:
     ```sh
     mvn clean package
     mvn spring-boot:run
     ```
  > **Note**: Stop any running instance of Tomcat to avoid port conflicts.
  ```sh
  /opt/tomcat9/bin/shutdown.sh

- Changing the port:
  - Modify `/src/main/resources/application.properties`:
    ```properties
    server.port=8081
    ```

> **Remember** to create all files in `java/com/.../spring/`.

---

## Entry Point in a Spring Boot Application
### Key Terminologies
- Spring IoC (Inversion of Control) Container:
  - Injects dependencies into objects.
  - Manages object creation and lifecycle.
  - Includes ApplicationContext and BeanFactory.
  - <img src="./images/image2.png" width=300/>
- Beans:
  - POJO (Plain Old Java Object) with getters and setters.
  - Managed and configured by the Spring IoC Container.
- BeanFactory:
  - Lightweight implementation of the Spring IoC Container.
  - Parent interface of ApplicationContext.
- ApplicationContext:
  - Subinterface of BeanFactory.
  - Suitable for enterprise-level applications.
  - Holds all beans and manages their lifecycle.
- Dependency Injection:
  - Objects define their own dependencies, and the container resolves them.


<img src="./images/image3.png" width=600>

```java
package com.mechsimvault.spring;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.mechsimvault.spring.model.Student;
@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(Application.class,args);
		Student st = context.getBean(Student.class);
		st.compile();
	}
}
```

## Key Annotations

### - `@SpringBootApplication`:

- Marks the main class of the Spring Boot application.  
- Combines the following annotations:
  - `@SpringBootConfiguration`: Indicates the class is a source of bean definitions.
  - `@EnableAutoConfiguration`: Enables auto-configuration for dependencies like `spring-boot-starter-web`.
  - `@ComponentScan`: Scans the package and its subpackages for Spring-managed components.

### - Customizing Component Scan:

- Use `@ComponentScan(basePackages = {"com.mechsimvault.spring", "com.mechsimvault"})` if beans are located outside the default package structure.

### - `@Component`
  - Lets `IoC container` know that this is `bean object`
  - And hence will be managed by `IoC Container`    

    ```java

      package com.mechsimvault.spring.model;

      import org.springframework.stereotype.Component;

      @Component
      public class Student{
        
        public void compile(){
          System.out.println("Student");
        }
      }
    ```


## - Managing Beans and Dependency Injection
  > ### Here Spring auto detects components, DI and classes
- ### `@Component`
  - Marks a class as a Spring-managed bean.

    ```java
    package com.mechsimvault.spring.model;

    import org.springframework.stereotype.Component;

    @Component
    public class Student {
        public void compile() {
            System.out.println("Student");
        }
    }

    ```

- ### `@Autowired` 
  - Injects dependencies into fields, constructors, or setters.
  - But when using field injection on variable we use `@Value("abc")` annotation
    ```java
    // Field injection
    public class Student {
        @Autowired
        private AnyClass anyClass;
        
        @Value(23)
        private int marks;
        
        @Value("Not Found")
        private String name;
    }
    ```
    ```java
    // Setter injection
    public class School { 
      private final Student student;
      @Autowired
      public void setStudent(@Value("${Student.name}") Student student) {  
          this.student = student;
      }
    }
    ```
    ```java
    // Constructor injection
    public class School{
      @Autowired
      public School(Student student) {  
          this.student = student;
      }
    }
    ```
  - To avoid dependency or make dependency opyional
    ```java
    public class Student{
      @Autowired(required = false)
      private String name;// optional to provide
    } 
    ```

## Self managing Appliction Context using `ClassPathXmlAppliationContext`

- If we dont want to use annottions we can define an xml file for the same
- The `applicationContext.xml` file takes care of all defining classes, component, DI  
  
  ```java
  ApplicationContext context = new ClassPathXmlApplicationContext("beanConfig1.xml","beanConfig2.xml",...);
  ```

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">
  
      <bean id="student" class="com.mechsimvault.spring.model.Student"></bean>
  
  </beans>
  ```

- Singleton and Prototype scope
  - The bean config file use `scope="singleton"` by default 
    - This means object is created regardless the object is called or not
    - And only one copy is created and used then after 
    - Hence can craete problem like for the code 

    ```java
		Student st = context.getBean(Student.class);
		st.setMarks(10);
		System.out.println("initial st0 marks : "+ st.getMarks());

		Student st1 = context.getBean(Student.class);
		st1.setMarks(15);
		System.out.println("initial st1 marks : "+ st1.getMarks());
		System.out.println("Final st0 marks : "+ st.getMarks());
    ```

    - Output is

    ```java
    // with scope singleton
    /*     
      <bean 
        id="student" 
        class="com.mechsimvault.spring.model.Student" 
        scope="singleton">
      </bean>
    */

    Student constructor
    initial st0 marks : 10
    initial st1 marks : 15
    Final st0 marks : 15

    // with scope protoype
    /*     
      <bean 
        id="student" 
        class="com.mechsimvault.spring.model.Student" 
        scope="prototype">
      </bean>
    */

    Student constructor
    initial st0 marks : 10
    Student constructor
    initial st1 marks : 15
    Final st0 marks : 10
    ```

- Property tag in bean script
  - Used for multiple purpose
  - used with `value` to fill primitives
  - Used with `ref` to fill object 
  - Can be drectly used with using `p:`
  
    ```xml
    <bean id="course" class="com.mechsimvault.model.Course">
        <property name="courseName" value="Math 101"/>
    </bean>

    <bean id="student" class="com.mechsimvault.model.Student">
        <property name="course" ref="course"/>
    </bean>

    <bean 
      id="student" 
      class="com.mechsimvault.model.Student" 
      p:name="John Doe" 
      p:cource-ref="course"
    />
    ```

  - Autowire : Fill the value of bean is available

    ```xml
      <bean id="student" class="com.mechsimvault.model.Student" autowired="default">
      </bean>

      <bean id="course" class="com.mechsimvault.model.Course"></bean>
    ```
  
  - Here it will fill value of course
  - Say there are two cousre then it will give error so we have to set `autowired` type

    ```xml
      <bean 
        id="student" 
        class="com.mechsimvault.model.Student" 
        autowired="byName" 
        >
        <!--autowired can also be byValue : So will select data type instead of name -->
        <!--when used by name so will match with the name of variable or object in class -->
      </bean>

      <bean id="math_course" class="com.mechsimvault.model.MathCourse"></bean>
      <bean id="sci_course" class="com.mechsimvault.model.SciCourse"></bean>

    ``` 
  - Constructor injection 

    ```xml
      <bean id="student" class="com.mechsimvault.spring.model.Student" scope="prototype">
          <constructor-arg name="name" value="ohm"/>
          <constructor-arg name="marks" value="10"/>
      </bean>

    ```


---
---
---

## Creating model and returing
- Say we have creating a POJO(Plain Old Java Object)
- Student is POJO with 
  - name : stiring
  - id : int
- It has getters and setters
> ### - It must have empty constructor
- Creting controller for it 

  ```java
  package com.mechsimvault.spring;

  import com.mechsimvault.spring.model.Student;
  import org.springframework.http.ResponseEntity;
  import org.springframework.web.bind.annotation.*;

  @RestController
  @RequestMapping("/api/students")
  public class StudentController {

      // @GetMapping("/dummy")
      @RequestMapping(value = "/dummy", method = RequestMethod.GET)
      public Student getDummyStudent() {
          // Creating a dummy Student object
          Student dummyStudent = new Student(1, "John Doe");
          return dummyStudent; // Return the object as JSON
      }
  }

  ```

- Annotations
  - `@RestController`
    - Combines functionalities of `@Controller` and `@ResponseBody`
    - Used for handing HTTP requests
    - Response object will automatically converted to HTTP response
  - `@RequestMapping('/url')`
    - URL mapping
  - `@GetMapping`, `@PutMapping`, `@PostMapping`, `@DeleteMapping`
    - Mapping perticular request to a method
    - Same can be done by 
      - `@RequestMapping(value = "/dummy", method = RequestMethod.GET)`




# - ?

- xml configuration file "spring.xml" with bean tag and attribute class, id, scope(singleton and prototype)
- ClassPathXmlApplicaionContext
- Spring container
- Why beans are called singleton beans coz they are created once
- Setter injection
  - poperty tag in bean tag
  - reference attribute
  - autowite
    -  multiple properties : primary 
- Constructor injection