# Tomcat
- Using tomcat 9.0.98 (10 was not good for starters)
  - With `openjdk 11.0.25`
- File structure
  ```
  MyFirstApp/
  └── WEB-INF 
      ├── classes
      │   └── HelloServlet.class 
      └── web.xml 
  ```
# Steps of creating smaple project
### 0. Creating file structure
```
mkdir -p <App-Name>/WEB-INF/classes
```
### 1. Create class for servlet
```java
// HelloServlet.java

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.getWriter().println("<h1>Hello, World! This is my first servlet.</h1>");
    }
}
```
### 2. Compile class

```bash
# -cp : class path : location of external libraries needed for the compilation
# /opt/tomcat9/lib/servlet-api.jar : location of external lib for compilation

# -d where to output .class files
# . create .class file at current location

# HelloServlet.java : file to be compiled
javac -cp /opt/tomcat9/lib/servlet-api.jar -d . HelloServlet.java
```

### 3. move .class file to WEB-INF/classes

```bash
mv HelloServlet.class MyFirstApp/WEB-INF/classes/
```

### 4. Create `deployment descriptor` 

```xml
<!-- web.xml -->

<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/javaee" version="3.0">
    <servlet>
        <servlet-name>HelloServlet</servlet-name>
        <servlet-class>HelloServlet</servlet-class>
        <!-- class name HelloServlet need to be added to WEB-INF/classes -->
    </servlet>
    <servlet-mapping>
        <servlet-name>HelloServlet</servlet-name>
        <url-pattern>/hello</url-pattern>
    </servlet-mapping>
</web-app>

```

### 5. Create .war file
- `.war` files are deployable format for web application

```bash
# -cvf
#   c: create archive
#   f: name of output file

# -C : change the dir before adding files to archive
# . : include all files in current dir
jar -cvf MyFirstApp.war -C MyFirstApp/ .  
```

> ### Now you can see result on `localhost:8080/MyFirstApp/hello`

