# Spring Boot MVC

## - Front Controller / DispatcherServlet
- Traditionally in servlet and MVC(Without Spring) we used to create web.xml file
  - These files were used to direct traffic to appropriate controller
- Now we have Front Controller that does the same
- Spring Boot MVC handles the Front Controller / DipatcherServlet it self
- In Spring MVC we need a little configuration
- It also takes care of view resolution (Find view, sending data to view, and rendering view)

## - Directories used
- SpringBoot version : `3.4.1`
- `spring-boot-starter-web`
- spring-boot-starter-tomcat
- spring-boot-starter-test
- `tomcat-jasper:10.1.34`

> - ### If tomact-jasper is not used then it donwloads the `.jsp` files
> - Tomcat server handles the conversion of jsp to servlet
> - But Spring, which is used for backend has excluded jasper, which is reponsible for conversion 
> - Hence we need to manually add dependency  

## - Basic home controller
```java
package com.mechsimvault.springbootmvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// Make the class as MVC controller class
@Controller
public class HomeController{
  
  // url-pattern
  @RequestMapping("home")
  public String home(){
    // spring boot takes care of finding index.jsp in `webapp`
    // If it is nested then we need to mention path w.r.t webapp
    // or we can mention the prefix and suffix to seach in application.properties 
    return "index.jsp";
  } 
}
```

```yaml
# application.properties

# These two are used to tell Model or ModelView where to look for 
# Remove comments when using
spring.mvc.view.prefix= /views/ # say jsp files are stored in webapp/views 
spring.mvc.view.suffix= .jsp # what should file end with
```

- ## Requst handling, Request Dispactcher
- Say we will get params from url-params 
- Using servlet we would do following
  ```java
  @RequestMapping("add")
  public String home(HttpServletRequest req, HttpSession session){
    int num1 = Integer.parseInt(req.getParameter("num1"));
    int num2 = Integer.parseInt(req.getParameter("num2"));

    int num3 = num1+num2;
    session.setAttribute("num3", num3);

    return "add";
  } 
  ```
- But with Spring Boot we can use Model and ModelAndView
  ```java
  @RequestMapping("add")
  public ModelAndView home(@RequestParam int num1,@RequestParam int num2){
    // Two syntax to set view 
    ModelAndView mv = new ModelAndView();
    mv.setViewName("add");
    // or
    ModelAndView mv = new ModelAndView("add");

    mv.addObject("num3",num1+num2);
    return mv;
  } 
  ```
  ```java
  @RequestMapping("add")
  public String home(@RequestParam int num1,@RequestParam int num2, Model mv){
    mv.addAttribute("num3",num1+num2);
    return "add";
  } 
  ```
- `Model` v/s `ModelAndView`
  - With model you cannot create object of it and cannot return it as object
  - Cannot set view with `Model`
  - Model is used to set page properties
  - When you want to set views explicity you can use `ModelAndView`

| Aspect               | Model                                  | ModelAndView                              |
|----------------------|----------------------------------------|-------------------------------------------|
| **Purpose**           | Used to add model attributes to be accessed in the view. | Combines both the model (data) and the view (view name). |
| **Return Type**       | Returned as part of the controller method’s return type. | Typically returned by the controller method directly. |
| **Data Handling**     | Data is added to the model as attributes. | Data and view name are set directly on the ModelAndView object. |
| **View Resolution**   | View resolution is done implicitly based on the returned view name. | Explicitly set the view name through `setViewName()`. |
| **Usage**             | More commonly used in controller methods that return a view name as a String. | Used when you need to specify both model data and the view name explicitly. |

- To save a class using `Model` and `ModelAndView`

  ```java
    @RequestMapping("addStudent")
    public String addStudent(@RequestParam int id,@RequestParam String name, Model mv){
      Student student = new Student();
      student.setId(id);
      student.setName(name);
      mv.addAttribute(student);
      return "student";// student.jsp
    }
  ```
  ```java
    @RequestMapping("addStudent")
    public String addStudent(@ModelAttribute Student student, Model mv){
      mv.addAttribute(student);
      return "student";// student.jsp
    }
  ```
  ```java
    @RequestMapping("addStudent")
    public ModelAndView addStudent(@ModelAttribute Student student){
      // Request dispatcher parameter is added to ModelAndView constructor
      ModelAndView mv = new ModelAndView("student");// student.jsp
      mv.addObject("student",student);
      return mv;
    }
  ```