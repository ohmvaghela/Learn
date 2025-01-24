# Basics
- We can implement multiple classes

  ```java
  ✅ class child implement parent1, parent2 {}
  ```

- We cannot extened multiple classes

  ```java
  ✅ class child extends parnet1{}
  ❌ class child extends parent1, parent2{}
  ```

- We can simuntaniously extend one class and implement one or more interfaces

  ```java
  ✅ class child extends parent1 implements parent2, parent3{}
  ```

# Multi Threading
- There are two ways of performing multi threading in java
  1. Extending Thead class  
  2. Implementing Runnable interface

    ```java
    import java.lang.Thread;
    import java.lang.Runnable;
    
    class CustomThread extends Thread{
      @Override
      public void run(){
        System.out.println("Thread "+ Thread.currentThread().getName());
        System.out.println("Thread "+ Thread.currentThread().getId());
      }
    }
    
    class CustomRunnable implements Runnable{
      @Override
      public void run(){
        System.out.println("Runnable "+ Thread.currentThread().getName());
        System.out.println("Runnable "+ Thread.currentThread().getId());
      }
    }
    
    class MainClass {
      public static void main(String[] args){
        CustomThread ct = new CustomThread();
        ct.start();
    
        CustomRunnable cr = new CustomRunnable();
        Thread myThread = new Thread(cr);
        myThread.start();
      }
    }
    ```

- To sync a resource we can use synchronized block

  ```java
  synchronized (Resource){}
  ```

## Thread v/s runnable

| Thread | Runnable |
|-|-|
| We cannot extend another class | We can extend another class |
| Thread contains `start` method | We need to create a Thread object and pass runnable as params, then we can use Thread object's start method |
| Inherits all methods from thread | Only Uses run method from Thread |

## Passing `run` method of runnable directly to thread
- We can use lambda function to directly call runnable method

```java
Thread t1 = new Thread(()->{
  for(int i=0;i<10000;i++){
    // business logic
  }
})
// another equivalent way
Thread t2 = new  Thread(new Runnable(){
  @Override
  public void run(){
  // business logic
  }
})
```

## Thread syncronization 
- To implement lock on a resource such only one thread access it a time
- We just need to add `syncronized` keyword before function
- This prevents race condition
  <details>
    <summary><h2>Code</h2></summary>
    
    ```java
    import java.lang.Thread;
    import java.lang.Runnable;
    
    class Incre{
      private int val = 0;
      public synchronized void incre(){
        val++;
      }
      public int get(){
        return val;
      }
    }
    
    class Main{
    
      public static void main(String[] args){
        Incre inc = new Incre();
        Thread t1 = new Thread(()->{
          for(int i = 0;i<10000;i++){
            inc.incre();
          }
        });
    
       Thread t2 = new Thread(()->{
         for(int i = 0;i<10000;i++){
           inc.incre();
         }
       });
    
       t1.start();
       t2.start();
    
       try{
         t1.join();
         t2.join();
       }catch(Exception e){
         e.printStackTrace();
       }
       System.out.println(inc.get());
      }
    }
    ```
  </details>

## Callables v/s runnables
- Runnable is a functional interface and the function run returns only void
- And we can also initiate Runnable using lambda function
  
  ```java
  @FunctionalInterface
  public interface Runnable {
      void run();
  }
  ---
  // It returns void 
  Runnable rn = () ->{}
  ```

- Callable have a return type, but it will run synchronously
- Hence to achieve multi-threading we need to use `callable<T>` with `ThreadPool`
- Callable functions are creating using lambda interface

```java
Callable<String> task = () -> {
    Thread.sleep(500); // Simulate some work
    return "Task executed without a thread pool";
};
```

- We use callable when we want a return type as runnable will return void and cant handle exception on its own

## ExecutorService (Thread Pool: For Managing Threads)
- Provides a high-level interface for managing thread execution, replacing manual thread management.
- Key Methods:
  - `submit()`: Submits a `Runnable` or `Callable` task for execution, returning a `Future` that represents the result or handles exceptions.
  - `invokeAll()`: Submits a collection of tasks for execution and returns a list of `Future` objects. It blocks until all tasks complete.
  - `shutdown()`: Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted.
  - `shutdownNow()`: Attempts to stop executing tasks and returns a list of the tasks waiting to be executed.
- Creating Thread Pools: (These are all static methods under `Executors` class and returns `ExecutorService` object)
  - `newFixedThreadPool(int nThreads)`: Creates a thread pool with a fixed number of threads. Additional tasks wait in the queue until a thread becomes available.
  - `newCachedThreadPool()`: Creates a thread pool that can grow as needed, reusing previously created threads when possible.
  - `newSingleThreadExecutor()`: Creates a single thread executor to run tasks sequentially.
  - `newScheduledThreadPool(int corePoolSize)`: Creates a pool of threads for scheduling tasks periodically or after a delay.

    <details>
      <summary> <h3> working code </h3>  </summary>
      
      ```java
      import java.util.concurrent.*;
      
      public class ExecutorServiceExample {
          public static void main(String[] args) {
              // Creating a thread pool with 3 threads
              ExecutorService executorService = Executors.newFixedThreadPool(3);
      
              // Task 1: Using Runnable (doesn't return a result)
              Runnable task1 = () -> {
                  try {
                      System.out.println("Task 1 - " + Thread.currentThread().getName());
                      Thread.sleep(1000);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              };
      
              // Task 2: Using Callable (returns a result)
              Callable<String> task2 = () -> {
                  Thread.sleep(1000);
                  return "Task 2 - " + Thread.currentThread().getName();
              };
      
              // Submit task1 and task2
              executorService.submit(task1);
              Future<String> future = executorService.submit(task2);
      
              try {
                  // Get the result of task2
                  System.out.println(future.get());  // Will block until the task is complete
              } catch (InterruptedException | ExecutionException e) {
                  e.printStackTrace();
              }
      
              // Shutdown the executor
              executorService.shutdown();
          }
      }
      ```
      
    </details>

    <details>
      <summary> <h3> syntax </h3> </summary>

      ```java
      import java.util.concurrent.*;
      
      public class ThreadPoolExample {
          public static void main(String[] args) {
              // Fixed Thread Pool
              ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
      
              // Cached Thread Pool
              ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
      
              // Single Thread Executor
              ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
      
              // Scheduled Thread Pool
              ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);
      
              // Example task
              Runnable task = () -> System.out.println("Task executed by " + Thread.currentThread().getName());
      
              // Submitting tasks
              fixedThreadPool.submit(task);
              cachedThreadPool.submit(task);
              singleThreadExecutor.submit(task);
      
              // Scheduling a task with delay
              scheduledThreadPool.schedule(task, 2, TimeUnit.SECONDS);
      
              // Shutdown executors
              fixedThreadPool.shutdown();
              cachedThreadPool.shutdown();
              singleThreadExecutor.shutdown();
              scheduledThreadPool.shutdown();
          }
      }
      ```

    </details>

## Locks in Java
- `ReentrantLock` object works as lock
- It has 4 common methods
  1. lock()
  2. unlock()
  3. tryLock()
  4. tryLock(timeout, TimeUnit)
- Creating lock

  ```java
  final ReentrantLock myLock = new ReentrantLock();
  ```

- These are unfair locks and resource are allocated randomly
- If we want to create locks and allocate resource in the order of request we can use fair lock
- For it we just need to pass a `true` boolean to ReentrantLock

  ```java
  final ReentrantLock myLock = new ReentrantLock(true);
  ```

### `lock()` v/s `tryLock()` 
- Lock will wait on the line till it gets resource and does not return anything
- While tryLock will attempt to lock resource and if it fails to lock resource then it continues execution
- tryLock returns boolean that wether they were able to lock the resource or not
- tryLock another syntax

  ```java
  MyLock.tryLock(2, TimeUnit.SECONDS));// waits for lock for 2 sec 
  ```

## ReadWrite lock
- These locks allow multiple read and single write
```java
// Declaring lock
ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
lock.readLock().lock(); // Acquire the read lock
lock.readLock().unlock(); // release the read lock
lock.writeLock().lock(); // Acquire the write lock
lock.writeLock().unlock(); // Release the write lock
```

