# Aspect Oriented Programming

## Cross Cutting Concerns
- Functions that are required accross multiple modules of application 
- But these are not part of main business logic
- Eg. Logging, Security, Transaction

## What is AOP
- Its a programming paradigm that seperates cross cutting concerns from main business logic

## Aspect
- Module that encapsulates behaviours affecting/required by multiple classes
- Its like a class with cross-cutting functionality like Logging class.
- These are implemented with regular classes in Spring
- `@Aspect` annostation is used

## Join Point
- It a specific point during execution of program  
- It represents a method execution, constructor call
- Spring AOP offers a few join points which are :
  - Point just before exection of method starts
  - Point just after exection of method ends
  - Point just after method throws error

## Advice 
- Action performed by `Aspect` at a perticular `join point`
- Types of advice 
  - `Before Advice`: Runs before the join point.
  - `After Returning Advice`: Runs after the method completes successfully.
  - `After Throwing Advice`: Runs if the method throws an exception.
  - `After (Finally) Advice`: Always runs after the method, whether it succeeds or throws an exception.
  - `Around Advice`: Runs before and after the join point, giving full control.

## PointCut
- Defines where in the applciation the `advice` should be applied
- It uses expression to match methods and other `join point`
  - `execution(* com.example.service..*(..))`
  - `execution(* com.example.service.*.get*(..))`
  - `execution(* com.example.service.*.*(String, int))`
  - ### Reuable Pointcuts
    - These are called maker methods
    - These can be used to access methods in the class         
    ```java
    @Pointcut("execution(* com.example.service.UserService.*(..))")
    public void userServiceMethods() {}
    ```

## Introduction
- Allows to add new method or field to code without modifying the code
- Spring AOP uses `Introduction Advice` to achieve this

## Traget Object
- The object on which `Advice` is to be applied
- This can only be applied on methods of spring beans

## AOP proxy
- Wrapper around target object that intercepts method calls to apply advice

## Weaving
- Process of linking `aspects` with `target objects` at specified `join points`
- Types of Weaving
  - Compile Time Weavin g : Weaving at code compilation
  - Load Time Weaving : Weaving when packages are loaded to JVM
  - Runtime weaving : Aspects are woven dynamically at runtime. 
    - Spring AOP uses runtime weaving by creating proxies.

## Capturing function return
- In the following example the `result` is the variable which will store the output of function
  ```java
  @AfterReturning(pointcut = "userServiceMethods()", returning = "result")
  public void logAfterReturning(JoinPoint joinPoint, Object result) {
      System.out.println("Method executed: " + joinPoint.getSignature().getName());
      System.out.println("Return value: " + result);
  }
  ```

## using `JoinPoint` Geting target object info

```java
 // Join point all the details of target object and proxies
 public void logAfterReturning(JoinPoint joinPoint, Object result) {
  // returns the target object's method name advice is applied upon
  String method_name = joinPoint.getSignature().getName(); 

  // Get class name
  String class_name = joinPoint.getTarget().getClass().getName()

  // Returns the args provided to target object's method
  Object[] args = joinPoint.getArgs();

  // Returns the proxy object
  Object proxy = joinPoint.getThis();
 }
```


## Usecase
- Say a class UserService has two methods

  ```java
  package com.example.service;
  @Service
  public class UserService {
      public String getUserById(int id) { /* Prints something*/}
      public void updateUser(int id, String name) { /* Prints something*/}
  }
  ```

- Now defining the aspect

    ```java
    package com.example.aspect;

    import org.aspectj.lang.JoinPoint;
    import org.aspectj.lang.ProceedingJoinPoint;
    import org.aspectj.lang.annotation.*;
    import org.springframework.stereotype.Component;


    /*
    * The entire class is an Aspect which provides logs 
    * And there are multiple join points 
    */
    @Aspect
    @Component
    public class LoggingAspect {

        // PointCut: Matches all methods in UserService
        // This is a "Maker Method" and declares reusable PointCuts
        // Anything inside wont be executed
        @Pointcut("execution(* com.example.service.UserService.*(..))")
        public void userServiceMethods() {}

        // Before Advice: Runs before the method execution
        @Before("userServiceMethods()")
        public void logBefore(JoinPoint joinPoint) {
            System.out.println("Before method: " + joinPoint.getSignature().getName());
            System.out.println("Arguments: " + joinPoint.getArgs().length);
        }

        // After Returning Advice: Runs after the method executes successfully
        @AfterReturning(pointcut = "userServiceMethods()", returning = "result")
        public void logAfterReturning(JoinPoint joinPoint, Object result) {
            System.out.println("After method: " + joinPoint.getSignature().getName());
            System.out.println("Returned value: " + result);
        }

        // Around Advice: Logs execution time
        @Around("userServiceMethods()")
        public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed(); // Execute the target method
            long executionTime = System.currentTimeMillis() - start;
            System.out.println("Execution time: " + executionTime + "ms");
            return result;
        }
    }
    ```