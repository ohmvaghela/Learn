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
    <servlet-name>servlet_name</servlet-name>
    <servlet-class>class_name</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>servlet_name</servlet-name>
    <url-pattern>url</url-pattern>
  </servlet-mapping>
</web-app>  
```



