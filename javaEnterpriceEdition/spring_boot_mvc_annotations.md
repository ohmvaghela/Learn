# Spring Boot MVC Annoattaions

## - `@Controller`
- Specialization of `@Componenet` annoataiton
- Used to mark class an MVC controller

- Key Annoatations used under `@Controller` class
  - [Request Handling](./spring_boot_mvc_annotations.md#request-handling)
  - [Request Parameters and path variables](./spring_boot_mvc_annotations.md#request-parameters-and-path-variables)
  - [Request Body and Response Body](./spring_boot_mvc_annotations.md#request-body-and-response-body)
  - [Model and Views ](./spring_boot_mvc_annotations.md#model-and-views)
  
---
- ### Request Handling
- `@RequestMapping(value='/home', method=RequestMethod.GET)`
- `@GetMapping("/example")`
- `@PutMapping("/example")`
- `@PostMapping("/example")`
- `@DeleteMapping("/example")`
---
- ### Request Parameters and path variables
- `@RequestParam("name")`
  - If the name of param is same as input then it can be ignored
    - `public String someMethod(@RequestParam String name)`
  - If the name of param is different then we need to mention it
    - `public String someMethod(@RequestParam("name") String name1)`
- `@PathVariable`
  - The path that comes from request
    ```java
      @RequestMapping("/user/{id}")
      public String getUser(@PathVariable int id) {
          return "User ID: " + id;
      }
    ```
- ### ? `@RequestHeader`
- ### ? `@CookieVaule`
---
- ### Request Body and Response Body
- `@RequestBody`
  - Used to params from body
  ```java
  @PostMapping("/create")
  public String create(@RequestBody User user) {}
  ```
- `@ResponseBody`
  - If the response is to be send in body of response
    ```java
    @GetMapping("/api/user")
    @ResponseBody
    public User getUser() {
        return new User("John", 30);
    }
    ```
- `@RestController`
  - Class Annotaiton, when the class will be explicitly handling RESTful API
---
- ### Model and Views 
- `@ModelAttribute`
  - Used to bind request parameter with object
    ```java
    @RequestMapping("addStudent")
    public String addStudent(@RequestParam int id,@RequestParam String name, Model mv){
      Student student = new Student();
      student.setId(id);
      student.setName(name);
      mv.addAttribute(student);
      return "student";
    }
    ```
    ```java
    @RequestMapping("addStudent")
    public String addStudent(@ModelAttribute Student student, Model mv){
      mv.addAttribute(student);
      return "student";
    }
    ```
- `@RequestAttribute`
  - Used to get pre-existing attribute, these may be set by filters
      ```java
      @RequestMapping("/request")
      public String request(@RequestAttribute("key") String value) {
          return "Value: " + value;
      }
      ```
---
- ### Exception Handling
  - ### ? `@ExceptionHandler`
  - ### ? `@ControllerAdvice`
---
- ### Miscellaneous
  - ### ? `@CrossOrigin`
  - ### ? `@ResponseStatus`
---
>- Generally these objects that we get using annotations like `@RequestBody`, `@PathVariable` are in `Jackson` format 
  - we can convert to `Map<String,Object>` format to use them with ease
>- So If we want to decouple we can do it manually like this
>  ```java
>  public String getCity(@RequestBody Map<String, Object> requestBody) {
>    // Extracting the nested value from the Map
>    Map<String, Object> user = (Map<String, Object>) requestBody.get("user");
>    Map<String, Object> address = (Map<String, Object>) user.get("address");
>    String city = (String) address.get("city");
>  }
>  ```
