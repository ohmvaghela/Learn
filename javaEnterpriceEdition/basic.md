# Basics

## Container
- It is an object that holds collection of values
- It acts as a wrapper to provide a systemmatic way to store, read and manuplate data
- These also proide functionally to handle `null` value safety

## Examples of Container Objects

### 1. Collections
- List
- ArrayList
- Map
- Set

### 2. Opetionals (`Optional<T>`)
- It represent presence or absence of null value
- Helps in avoiding NullPointerException
```java
// Storing data in Optionals
Optional<String> emptyOptional = Optional.empty();
// When you are sure value is not null then use
Optional<String> nonEmptyOptional = Optional.of("Hello"); 
// If not sure value will be null then use
Optional<String> name = Optional.ofNullable("John");  // Creates a non-empty Optional
// Using it to store return from function
Optional<Student> st =  studentRepo.findById(1); // this returns Optional<Student>

// Checking if data is present
Optional<String> name = Optional.ofNullable("John");
if (name.isPresent()) {
    System.out.println(name.get());
}

// Retreving data
String value = name.get();  // Returns "John"
String value = name.orElse("Default Name");  // Returns "Default Name"
String value = name.orElseThrow(() -> new IllegalArgumentException("Name not found"));
String value = name.orElseGet(() -> "Generated Default Name");

```
