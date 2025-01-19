# Spring MVC

## - Dependencies
- spring-webmvc 6.1.16 : Provides spring mvc framework 
- jakarta.servlet-api 5.0.0 : Defines servlet-related APIs

## - `web.xml`
- In spring-boot, request-dispatcher or dispatcherServlet used to taken care by spring-boot itself we need to define it here
- Dispatcher Servlet

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app
    version="5.0"
    xmlns="http://jakarta.ee/xml/ns/jakartaee"
    xmlns:xml="http://www.w3.org/XML/1998/namespace"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://jakarta.ee/xml/ns/jakartaee http://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd">
    <display-name>Archetype Created Web Application</display-name>

    <!-- Dispatcher Servlet -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>
          org.springframework.web.servlet.DispatcherServlet
        </servlet-class>
        <!-- location of config file for servlet Dispactcher -->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/spring-mvc-config.xml</param-value>
        </init-param>
        <!-- Priority of servlet -->
        <!-- 1 : Load servlet bofore other -->
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    <!-- The default file that should be displayed -->
    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>
</web-app>
```

- Configuration file `spring-mvc-config.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <!-- Enable Annotation-Based Configuration -->
    <!-- Eg. @Controller, @Autowired, -->
    <context:annotation-config/>

    <!-- Component Scan for annotations like : @Controller, @Service-->
    <!-- Where to look for specific classes -->
    <context:component-scan base-package="com.mechsimvault.controller"/>

    <!-- Enable Spring MVC -->
    <!-- Eg. @RequestMapping, @RequestParam, and @ModelAttribute, Model, ModelAndView etc. -->
    <mvc:annotation-driven/>

    <!-- View Resolver : Where to look -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/view/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
</beans>

```
