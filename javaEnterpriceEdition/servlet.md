# Servlet
> ## - What is servlet? (Servlet is an interface that must be implemented for creating any Servlet.)
>   - More refined answer??

## Working of servlet 
- When a client makes a call to server, server passes request to servlet container
- Each servlet container finds mapping to servlet and passes the request
- Then servlet lifecycle management starts
  - Container checks if servlet is already loaded (initialized)
    - Load servlet if not loaded
  - Container create two objects 
    - HttpServletRequest, HttpServletResponse
  - Container invokes `Service` method by passing `HttpServletRequest`, `HttpServletResponse` 
  - Response is generated and sent back to client
  - Servlet cleanup (Optional)
- For each client request, container spawns new thread to handle request

## Deployment descriptor (web.xml)
- Used for mapping servlet to url pattern
```xml
<!-- servlet names are matched-->
<web-app>
  <servlet>
    <!-- the name in servlet must match with servlet-mapping -->
    <servlet-name>servlet_name</servlet-name>
    <servlet-class>class_name</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>servlet_name</servlet-name>
    <url-pattern>/url</url-pattern>
  </servlet-mapping>
</web-app>  
```

## Writing servlet code
- We will be implementing `HttpServlet` class
- Url of the current class can be done in two ways one is defined above using web.xml
- Other way can be using `annotations` like shown below

```java
@WebServlet("/add")
public class Add extends HttpServlet {
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
...
  }
}
```

- ### Accessing parametes
  - for the params `url/add?num1=1&num2=3` we use

```java
// conversion is request to int if arthmathic operations are reuqired
String param1 = req.getParameter("num1");
String param2 = req.getParameter("num2");
```

- ### Exceptions to catch
  - `ServletException`

- ### Handling request from client
  - `service()` method handles all the request
  - It is entry point for requests
  - It automatically determines the request type
  - it takes servlet request and response as input

    ```java
      protected void service(HttpServletRequest req, HttpServletResponse res){}
    ```
  - If we want to manually handle each request we can override `goGet()`, `doPost()`, `doPut()`, `doDelete()`
  - Basic code

```java
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/add")
public class Add extends HttpServlet {
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        int num1 = Integer.parseInt(req.getParameter("num1"));
        int num2 = Integer.parseInt(req.getParameter("num2"));
        int result = num1 + num2;

        System.out.println(result);
        out.print("Result is: " + result);
    }
}
```
  - Example of REST operatiosn
```java
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {}
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {}
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {}
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {}
```
