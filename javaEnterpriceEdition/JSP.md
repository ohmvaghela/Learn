# JSP (JavaServer Pages)
- Server side tech used to create `dynamic pages`
- Basic example

```html

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>My First JSP</title>
</head>
<body>
    <h1>Welcome to JSP</h1>
    <p>The current date and time is: <%= new java.util.Date() %></p>
</body>
</html>

```

## Types of tags

1. Directive tag `<%@ ... %>`
    - Syntax
      - `<%@ page attribute="value" attribute="value" ... %>`
    - Used to provide instructions like
      - Page level setting
        - `<%@ page language="java" contentType="text/html" %>`
      - Include directives like other `JSP files`
        - `<%@ include file="header.jsp" %>`
      - Import lib and declare custom tag
        - `<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>`
        - To access this core java lib we need to use it with prefix `c`
      - Then there are implicit object which dont need to be defined 
        - List is present below
2. Scriplet tag `<% ... %>`
    - Used to embed java code in JSP
    - These are part of `service()` method 
      - Hence we cannot create classes or declare methods inside this

    ```html
    <body>
      <%
        int num1 = 10;
        int num2 = 20;
        int sum = num1 + num2;
      $>

    </body>
    ```

3. Expressions tag `<%= ... %>`
    - Used to print variables declared in java
    
    ```html
      <body>
        <%
          int num1 = 10;
        $>
        Value of num1 is : <%= num1 %>
      </body>
    ```

4. Description tag `<%! %>`
    - Used to create methods, classes and declare variables
    - these are treated as `instance variables`
    - Shared accross all the request 
      - `shared` accross `all  threads` that are generated for same request
    - Are `not shared` accross application
    - Excellent for resource locking and shared resources

    ```java
    <%! 
        // Declaring a variable
        int counter = 0;

        // Declaring a method
        public String greet(String name) {
            return "Hello, " + name + "!";
        }

        // Declaring a class
        class MyClass {
            public String sayHello() {
                return "Hello from MyClass!";
            }
        }
    %>
    <%
    // Using the declared variable
    counter++;  // Incrementing the counter
    out.println("Counter value: " + counter);  // Output: Counter value: 1

    // Using the declared method
    String message = greet("John");
    out.println(message);  // Output: Hello, John!

    // Using the declared class
    MyClass myClass = new MyClass();
    out.println(myClass.sayHello());  // Output: Hello from MyClass!
    %>
    ```

4. Custom tag
- User-defined tags that encapsulate reusable functionality.

  ```html
  <%
    String[] fruits = {"Apple", "Banana", "Cherry", "Date"};
  %>
  <c:forEach var="fruit" items="${fruits}">
      <p>Fruit: ${fruit}</p>
  </c:forEach>
  ```

## Implicit objects
- These objects are provided by JSP and are not needed to imported which are  

  | Implicit |  Object |	Type	Purpose |
  |-|-|-|
  | request | 	HttpServletRequest |	Contains data from the client (parameters, headers, etc.). |
  | response | 	HttpServletResponse |	Sends data back to the client. |
  | out | 	JspWriter |	Writes output to the client. |
  | session | 	HttpSession |	Tracks user session data. |
  | application | 	ServletContext |	Shared data across the application. |
  | config | 	ServletConfig |	Access to initialization parameters. |
  | pageContext | 	PageContext |	`(Scope is same page by default and it can be changed) `Manages page-level attributes and implicit objects. |
  | page | 	Object |	Refers to the current JSP page. |
  | exception | 	Throwable |	Handles exceptions in error pages. |

## Handling error in JSP
- We dont mention error in normal jsp page
- We have seprate error pages in JSP
- ### How to mention error page

```html
<%@ ... errorPage="error.jsp" %>
```

- ### Error page
  - The error page should know it is error page
  
  ```html
  <!-- In the error page -->
  <%@ ... isErrorPage="true"  %>
  ```
  
  - Now we can use `exception` object 
  - Example

  ```html
  <!-- error.jsp -->
  <%@ page isErrorPage="true" %>
  <html>
  <head>
      <title>Error Page</title>
  </head>
  <body>
      <h2>An error has occurred!</h2>
      <p>Exception Details: <%= exception.getMessage() %></p>
      <p>Stack Trace:</p>
      <pre><%= exception.printStackTrace() %></pre>
  </body>
  </html>
  ```

# JSTL (JSP Standard Template Library)
- Here we use `EL(expression language)` in `JSP`
- Like say we have an attribute named `username`
  - To access it we can use `{username}` in JSP to access it
- Say we want to use java functionality we can do following
  - include library say to do `write.out` we need to it using
  - Directive tag `<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>`
  - Then we can use `out` to print like shown below

  ```html
  <!-- used to print -->
  <c:out value="Hello"/>
  <!-- used to print attribute -->
  <c:out value="${username}"/>
  ```
- Setting and removing variable
  - Set  : `<c:set var="username" value="ohm"> </c:set>`
  - Remove : `<c:remove var="username"></c:set>`

## JavaBeans
- These are Java Classes which follow 
  - Must be serializabe
  - It should have a public no-arg constructor.
  - All properties must be private with public getters and setters
```java
public class Person {
    private String name;
    private int age;

    // No-argument constructor
    public Person() {}

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

```
```html
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>

<html>
<body>

<!-- Use the jsp:useBean tag to create the bean -->
<jsp:useBean id="person" class="com.example.Person" scope="request"/>

<!-- Set properties for the bean -->
<jsp:setProperty name="person" property="name" value="John Doe"/>
<jsp:setProperty name="person" property="age" value="25"/>

<!-- Retrieve the property values from the bean -->
<p>Name: <jsp:getProperty name="person" property="name"/></p>
<p>Age: <jsp:getProperty name="person" property="age"/></p>

<!-- Using JSTL to access bean properties -->
<c:out value="${person.name}" /> <br>
<c:out value="${person.age}" /> <br>

</body>
</html>
```

## JSTL function tag
- Used to perform common operations like `string manipulation`, `number formatting`, `date formatting`, etc...

```html
<!-- Loading function tag using descriptive tag -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
```

- Example usecase

```html
<c:out value="${fn:toUpperCase('hello')}" />
<c:out value="${fn:trim('   Hello World!  ')}" />
<c:out value="${fn:substring('Hello World', 6)}" />
```

