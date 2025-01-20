# Hibernate Lifecycle / Persistance Lifecycle

## Four Stages of Hibernate Lifecycle
1. Transient State
2. Persistant State
3. Detached State
4. Removed State

<img src="./images/image4.png" width=500>

## 1. Transient State
- When an object of POJO class is instantiated it is not connected to hibernate
- It exist in heap memory, hence changes can be made to it without altering DB
## 2. Persistant State
- When an object is connected to hibernate session it is in persistant state
- Two ways to get object in persistant state
  - Saving object to DB using hibernate session
  - Loading object from DB using hibernate session
## 3. Detached State
- For converting object form Persistant State to detached state, there are two ways
  - Close the session (So changes in object wont affect DB)
  - Clear the cache (So changes in object wont affect DB)
## 4 Removed State
- When an object is deleted from database, so after that any changes made in object wont affect DB




