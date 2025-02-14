# Junit5 
- It consist of 3 main parts
> # IMP
> - Always remeber to import the assert and other conditions that you are using like this
> ```java
>   import org.junit.jupiter.api.Test;
>   import static org.junit.jupiter.api.Assertions.assertTrue;
>   import static org.junit.jupiter.api.Assertions. //any thing else ;
> ```

### 1. Junit Platform
- Provides test execution and discovery infra. (idk why discovery)
- Allows Junit5 to run on different environemt and integrate with different tools and framework
- Supports mutiple test engines like junit jupiter, junit vintage, etc.
### 2. Junit Jupiter
- It is programming model and extension model
- It tell how test should be written and how they are executed
### 3. Junit Vintage
- Allows Junit4 and Junit3 to run test on Junit5 platform

## Annotations
- Test
  - In the test class we need to mention annotation `@Test` on top of each method which will be used for testing
- BeforeAll, Afterall
  - Ran once before/after all the tests
```java
@BeforeAll
static void initAll() {
    System.out.println("Before all tests");
}
@AfterAll
static void tearDownAll() {
    System.out.println("After all tests");
}

@Test
void test1() {
    System.out.println("Test 1 executed");
}

@Test
void test2() {
    System.out.println("Test 2 executed");
}

```
- Output
```
Before all tests
Test 1 executed
Test 2 executed
After all tests
```
- BeforeEach/AfterEach 
  - Ran once before/after each test


## Test Methods
- Test methods must `not` be `abstract` and `private`
  - Private test methods wont be detected in java
- Test methods can be `default` 
- The return type of test methods must be `void`
## Assertions 
1. assertEquals
- It checks
  - Primitive Types (e.g., int, double, char, etc.):
  - it wont work on arrays as it will compare the address of array
- Forms
  - `<T>` : byte, char, int, long, object, string
    - assertEquals(`<T>` expected, `<T>` actual)
    - assertEquals(`<T>` expected, `<T>` actual, String message) # processed even if the test passes
    - assertEquals(`<T>` expected, `<T>` actual, Supplier<String> messageSupplier) # not processed it test passes
  - `<T>` : double, float
    - All the above and also
    - assertEquals(`<T>` expected, `<T>` actual, `<T>` delta)
    - assertEquals(`<T>` expected, `<T>` actual, `<T>` delta, String message)
    - assertEquals(`<T>` expected, `<T>` actual, `<T>` delta, Supplier<String> message supplier)
- Uses
```java
class AppTest{

  
  @Test
  public void test1(){
    int numerator = 10;
    int denominator = 2;
    
    // When
    int result = numerator/denominator;

    int expected = 5;
    // Without message supplier
    assertEquals(expected, result, "The division result should be 5"); 
    // With message supplier
    assertEquals(expected, result, () -> "The division result should be 5");
  }

  @Test
  public void test2(){
    double numerator = 10.0;
    double denominator = 3.0;
    
    // When
    double result = numerator/denominator;

    double expected = 3.3333;
    double delta = 0.0001;    
    // Without message supplier
    assertEquals(expected, result, delta, "The division result should be approximately 3.3333"); 
    // With message supplier
    assertEquals(expected, result, delta, () -> "The division result should be approximately 3.3333");
  }
}

```
2. assertArrayEquals
  - `<T>` : byte array, char array, int array, long array, object array, string array
    - assertEquals(`<T>` expected, `<T>` actual)
    - assertEquals(`<T>` expected, `<T>` actual, String message) # processed even if the test passes
    - assertEquals(`<T>` expected, `<T>` actual, Supplier<String> messageSupplier) # not processed it test passes  
  ```java
    @Test
    public void test2(){
        int[] expected = {1, 2, 3};
        int[] actual = {1, 2, 3};
            
    	assertArrayEquals(expected, actual);
    }
  ```
3. assertFalse
- It can directly accept boolean or can take boolean supplier
- Same goes for message supplier
```java
assertFalse(boolean condition,                String message)
assertFalse(BooleanSupplier booleanSupplier,  String message)
assertFalse(boolean condition,                Supplier<String> messageSupplier)
assertFalse(BooleanSupplier booleanSupplier,  Supplier<String> messageSupplier)
// BooleanSupplier example

import java.util.function.BooleanSupplier;
BooleanSupplier condition = () -> 5 > 3; // Lazy evaluation
assertFalse(condition, "5 should be greater than 3");


```
- Other asserts
  - assertTrue()
  - assertNull()
  - assertNotNull()
  - assertSame()
  - assertNotSame()
  - assertAll()
  - assertIterableEquals()
  - assertArrayEquals()
  - assertLinesMatch()
  - assertThrows()
  - assertDoesNotThrow()
  - assertTimeout()
  - assertTimeoutPreemptively()
  - assertTimeoutPreemptively(Duration, Executable)

## Assumptions
- Skip if the test fails
- assumingThat(boolean assumption, Executable executable)
```java
    assumingThat(1 > 0 && 1 > 0, () -> {
        assertEquals(2, 12, "Sum should be 12");
    });
```
- Fails the test (Ideally it should not )
```java
assumeTrue(/*boolean*/, String message);
assumeTrue(/*boolean*/, Supplier<String> messageSupplier);
assumeTrue(/*boolean supplier*/, String message);
assumeTrue(/*boolean supplier*/, Supplier<String> messageSupplier);

assumeFalse(/*boolean*/, String message);
assumeFalse(/*boolean*/, Supplier<String> messageSupplier);
assumeFalse(/*boolean supplier*/, String message);
assumeFalse(/*boolean supplier*/, Supplier<String> messageSupplier);
```

## Exception assertions
- Check if exception exist if it should or not if should not 

```java

assertThrows(Class<T> expectedType, Executable executable)
assertThrows(Class<T> expectedType, Executable executable, String message)
assertThrows(Class<T> expectedType, Executable executable, Supplier<String> messageSupplier)

assertDoesNotThrow(Executable executable)
assertDoesNotThrow(Executable executable, String message)
assertDoesNotThrow(Executable executable, Supplier<String> messageSupplier)
assertDoesNotThrow(ThrowingSupplier<T> supplier)

```

```java
@Test
public void testException() {
  MyClass myClass = new MyClass();
  MyException exception = assertThrows(MyException.class, () -> {
    throw new MyException("This is Custom exception message");
  });
  assertEquals("This is Custom exception message", exception.getMessage());
}

@Test
void testIllegalArgumentException() {
  assertThrows(IllegalArgumentException.class, () -> {
    Integer.parseInt("invalid"); // Invalid input
  });
}

```

