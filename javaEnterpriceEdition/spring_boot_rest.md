# Spring Boot Rest

## `@RestController` v/s `@Controller`

| Controller | RestControlelr | 
|-|-|
| Specialization of `@Component` annotation | Specialization of `@Controller` annotation |
| Generally Used for handling JSP, HTML | Used for handling RESTful API| 
| Does not convert response automatically to HTTP response | Convert resposne to HTTP resposen automatically |

- When We use `@Controller` we need to use `@RequestBody` before every method to convert them to HTTP response
- `@RestController` automatically convert reponse to HTTP response 
- Like for the following response will try to find a JSP or HTML file named `"{\"message\": \"hello\"}"`

  ```java
  package com.mechsimvault.springbootrest;

  import org.springframework.stereotype.Controller;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.ResponseBody;


  @Controller
  public class HomeController {
    
    @RequestMapping(value="/")
    public String base(){
      return "{\"message\": \"hello\"}";
    }
  }
  ```

- But if we use `@RequestBody` annotation it will convert it to HTTP resposne 

    ```java
    @RequestMapping(value="/")
    @RequestBody
    public String base(){}
    ```

- But if we use @RestController We dont need to mention `@RequestBody` annotation everytime

  ```java
  @RestController
  public class HomeController {
    
    @RequestMapping(value="/")
    public String base(){
      return "{\"message\": \"hello\"}";
    }
  }
  ```

## Response type : JSON / XML
- By default `RestController` will send JSON, XML both 
- But we want to configure what to send and what to accept we can use `Produce`, `Consume`
> ### For using XML we need add dependency called `Jackson dataformat XML`
### - Produce : Sending resposne in desired format
  - JSON
    - `@GetMapping(value = "/", produces = "application/json")`
    - `@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)`
  - XML
    - `@GetMapping(value = "/", produces = "application/xml")`
    - `@GetMapping(value = "/", produces = MediaType.APPLICATION_XML_VALUE)`
  - Using both     
    - `@GetMapping(value = "/", produces = {"application/xml", "application/xml"})`
    - `@GetMapping(value = "/", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})`
### - Consume : accepting resposne in desired format
  - JSON
    - `@GetMapping(value = "/", consumes = "application/json")`
    - `@GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)`
  - XML
    - `@GetMapping(value = "/", consumes = "application/xml")`
    - `@GetMapping(value = "/", consumes = MediaType.APPLICATION_XML_VALUE)`
  - Using both     
    - `@GetMapping(value = "/", consumes = {"application/xml", "application/xml"})`
    - `@GetMapping(value = "/", consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})`

### - Taking variables from requestbody

- When the name of vairable/object in body is same as name of variable/object here

```java
public User updateUser( @RequestBody User user) {}
```

- When it is different
```java
public User updateUser( @RequestBody("SomeUser") User user) {}
```
