# Spring Annotations
## - Stereotype Annotations
  - Used to manage Spring-managed beans
  - `@Controller`
    - Marks Controller class of Spring MVC
  - `@RestController`
    - `@Controller` + `@ResponseBody`
    - Used for app with REST services and returns JSON and XML 
  - `@Repository`
    - Marks class as DAO(Data Access Object)
    - `?` It is also eligible for exception translation (converts database-related exceptions into Spring's DataAccessException).
  - `@Configuration`
    - Indicates that the class contains Spring bean definitions.
    - Used to replace XML config file
## - Dependency Injection Annotations
  - `@Autowired`
  - `@Qualifier`
    - Used with `@Autowired` to define which bean to be injected 
      ```java
      @Autowired
      @Qualifier("specificBean")
      private MyService myService;
      ```
  - `@Primary`
  - `@Value()`
    - Used as field injection for primitives mostly 
## - Scope Annotations
```java
@Scope("prototype")
@Component
public class MyBean {
    // Prototype bean
}
```