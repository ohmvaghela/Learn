# Aspect Oriented Porgramming

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
  - Compile Time Weaving : Weaving at code compilation
  - Load Time Weaving : Weaving when packages are loaded to JVM
  - Runtime weaving : Aspects are woven dynamically at runtime. 
    - Spring AOP uses runtime weaving by creating proxies.


- Cross Cutting Concerns
- Aspect
- Join point
- Advice
- PointCut
- Introduction
- Target Object
- AOP proxy
- Weaving
- Types of advice
  - Before advice
  - After returning advice
  - After throwing advice
  - After (finally) advice
  - around advice