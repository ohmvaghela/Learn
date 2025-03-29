# Go Basic Syntax

- [Go Basic Syntax](#go-basic-syntax)
  - [Varialbe decelaration](#varialbe-decelaration)
  - [Function](#function)
  - [For loop](#for-loop)
  - [If Statements](#if-statements)
  - [Slice](#slice)
    - [Initializing Slice](#initializing-slice)
    - [Operations on Slice](#operations-on-slice)
  - [Map](#map)
    - [Initializing Map](#initializing-map)
    - [Operations on Map](#operations-on-map)
  - [Struct](#struct)
    - [Nested Struct](#nested-struct)
  - [Interface](#interface)
    - [Functional Interface](#functional-interface)
    - [Empty Interface](#empty-interface)
    - [Interface inheritence](#interface-inheritence)
  - [Custom Types in Go](#custom-types-in-go)
  - [GoRoutines](#goroutines)
    - [Make a function goRoutine](#make-a-function-goroutine)
    - [WaitGroup](#waitgroup)
    - [Locks in go](#locks-in-go)
    - [Channels](#channels)
      - [bi-directional and directional channel](#bi-directional-and-directional-channel)
      - [To close channel](#to-close-channel)
      - [Accessing the buffer](#accessing-the-buffer)
    - [Worker Pool](#worker-pool)
  - [Error Handling](#error-handling)
    - [Panic and Recover](#panic-and-recover)
  - [IO Reader/Writer](#io-readerwriter)
    - [IO Reader](#io-reader)
    - [IO Writer](#io-writer)
  - [Working with JSON](#working-with-json)
    - [Converting struct to json](#converting-struct-to-json)
    - [Converting JSON/JSON array to go data types](#converting-jsonjson-array-to-go-data-types)
    - [Converting Request Body to JSON](#converting-request-body-to-json)
    - [Sending JSON response](#sending-json-response)
  - [Misc](#misc)
    - [Reflect](#reflect)

## Varialbe decelaration
```go
// 1. Using var keyword
var name = "ohm"            // Type inferred as string
var name string = "ohm"     // Explicit type declaration
var name string             // Declaration without initialization (default: "")

// Multiple variable declaration
var (
    x int    = 10
    y string = "test"
    z bool   = true
)

// 2. Constants
const name = "ohm"          // Type inferred as string
const name string = "ohm"   // Explicit type declaration

// Multiple constant declaration
const (
    pi    float64 = 3.14
    max   int     = 100
)

// 3. Short variable declaration (only within functions)
name := "ohm"              // Type inferred, shorter syntax

// Pointers
var a int = 3              // Regular integer variable
var b *int = &a            // Pointer to integer (stores address of a)
var c = myFunc(&a)         // Function taking pointer as parameter

// Additional useful syntax:
// Zero value initialization
var count int // count = 0
var count float64 // count = 0.0
var count string // count = ""
var count bool // count = false

// Multiple assignment
var i, j int = 1, 2
```

## Function
```go
// Basic function with no parameters or return
func myFunc() {}

// Function with multiple return types
func myFunc() (int, string) {}

// Function with single return type
func myFunc() int {}

// Function with grouped parameters
  // a,b,c : int | d,e,f string
func myFunc(a, b, c int, d, e, f string) {}

// Variadic function (variable number of parameters)
func myFunc(numbers ...int) {}

// Function with pointer parameters
  // a,b,c must be Integer pointer
func myFunc(a, b, c *int, d, e, f string) {}

// Function taking another function as parameter
func myFunc(a func(int, int) int) int {
    return a(1, 2)
}
func subFunc(a, b int) int {
    return a + b
}
var result int = myFunc(subFunc)

// Function returning another function
func myFunc() func(int, int) int {
    return func(x, y int) int {
        return x + y
    }
}

// Lambda function (anonymous function) example
func example() {
    a := func(x, y int) int {
        return x + y
    }
    fmt.Println(a(1, 2)) // Prints 3
}
```

## For loop
```go
// Infinite loop
for {}

// Traditional for loop
for i := 0; i < 10; i++ {}

// Iterating through a slice/array/map with range
// Range returns index and value
for index, value := range myList {}
for _, value := range myList {}
for index, _ := range myList {}

// Single condition loop (like while)
for condition {}
```

## If Statements
```go
// 1. Basic if-else structure
if condition1 {} 
else if condition2 {} 
else {}

// 2. If with initialization
// ✅ Only one initialization allowed
if variable := expression; condition {}
// ❌ Only expression is allowed
if variable := expression;variable := expression; condition {}
```

## Slice
### Initializing Slice

```go
// slice
// 1. Basic
sliceVariable := []Type{}
// Example
sliceVariable := []int{}

// 2. Pre defined array
sliceVariable := []int{1,2,3,4}

// 3. From array
sliceVariable := [5]int{1,2,3,4}
sliceVariable := arr[1:4]

// 4. Using Make
sliceVariable := make([]Type, length, capacity)
// Example
sliceVariable := make([]string, 2, 5)

// 5. slice using pointer
sliceVariable := new([]Type)
*sliceVariable := make([]Type, length, capacity)
// Example
sliceVariable := new([]int)
*sliceVariable = make([]int, 2, 5)

```

### Operations on Slice

```go
// Basic operations on slice
// 1. Add
sliceVariable = append(sliceVariable, 1)
sliceVariable = append(sliceVariable, 1,2,3)
sliceVariable = append(sliceVariable, s2...)
// Slice are abstraction of Array
// After adding elements 
  // Size of underlying array exceed
    // New slice is created and elements are copied 
  // Size of underlying array dont exceed
    // No new slice is created

// 2. Remove element at index
sliceVariable = append(sliceVariable[:index], sliceVariable[index+1:]...)
// 3. Length and Capacity of underlying array
fmt.Println("Length:", len(slice))  // Number of elements
fmt.Println("Capacity:", cap(slice)) // Underlying array capacity
// 4. Create copy
newSlice := make([]int, len(slice))
copy(newSlice, slice)
// when using primites new elements are created
// But when reference like pointer, structs are used same reference are used  
```

## Map
### Initializing Map

```go
// map
// 1. Basic
mapVariable := map[KeyType]ValueType{}
mapVariable := map[string]int{}
mapVariable := map[string]int{"one": 1, "two": 2}

// 1. Using Make
mapVariable := make(map[KeyType]ValueType)
// Example
myMap := make(map[int]int)

// 2. Using Pointer
mapVariable := new(map[KeyType]ValueType)
*mapVariable = make(map[KeyType]ValueType) 
// Example
mp := new(map[string]int)
mp = make(map[string]int)
```

### Operations on Map

```go
// Basic operations on map
// 1. Add element
mp["one"] = 1
// 2. Remove element
delete(mp,"one")
// 3. Iterating on map
for key, value := range myMap {}
// 4. Length of map
fmt.Println("Length:", len(mp)) 
```

## Struct
```go
// basic syntax
type Rectangle struct {
  width int
  height int
}
// 1
r := Rectangle{width: 14, height: 23}
// 2
r := Rectangle{14,23}
// 3 With pointer
r_pointer := &Rectangle{14,23}
// 4 With pointer
r_pointer := new(Rectangle)
r_pointer.width = 14
r_pointer.height = 23

// Accessing it
fmt.Println(r.height)

// struct methods
func (r Rectangle) Area() int {
  return r.height * r.width
}
fmt.Println(r.Area())
```
- **Go takes create of refrencing and de-referencing**
```go

r := Rectangle{14,23}
fmt.Println(r) // {14,23}

updateFn := func (r *Rectangle){
  // below two are equivalent
  r.height = 2*r.height
  (*r).height = 2*r.height
}
fmt.Println(r) // {14,92}
```
### Nested Struct
- Two ways of using nested struct
1. Nested struct **cannot** be accessed directly
      ```go
      type Address struct {City,State string}
      type Person struct {
          Name    string
          Age     int
          address Address
      }
      func main() {
       p := Person{
        Name: "Alice",
        Age:  25,
        Address: Address{
         City:  "New York",
         State: "NY",
        },
       }
       fmt.Println(p.Name, "lives in", p.address.City, p.address.State)
      }
      ```

2. Nested struct **can** be accessed directly
      ```go
      type Address struct {City,State string}
      type Person struct {
          Name    string
          Age     int
          Address // Anonymous (embedded)
      }
      func main() {
       p := Person{
        Name: "Alice",
        Age:  25,
        Address: Address{
         City:  "New York",
         State: "NY",
        },
       }
       fmt.Println(p.Name, "lives in", p.City, p.State)
      }
      ```

## Interface
### Functional Interface
- For interface of functions
  - If a function implements a method of interface
  - All the methods in interface must be defined by function
  - Say shape interface has Area, Perimeter as methods
    - Now rectangle must implement both Area and Perimeter
    - Or else rectangle should not implement any
```go
// Basic syntax
type Shape interface {
	Area() int
	Perimeter() int
}
type rectangle struct{
	height, width int
}

func (r rectangle) Area() int {
	return r.height * r.width
}
func (r rectangle) Perimeter() int {
	return r.height * r.width
}
// Now we can pass rectangle as shape
func printArea(s Shape){
	fmt.Println(s.Area())
	fmt.Println(reflect.TypeOf(s))
}
```

### Empty Interface
```go
// empty iterface can have any value
func printValue(value interface{}) {
  value, ok := i.(int) // Try to convert to int
  if ok {
      fmt.Println("Integer value:", value)
  } else {
      fmt.Println("Not an integer")
  }
}
printValue(42) // Integer value : 42
printValue("Hello, Go!") // Not an integer
printValue(3.14) // Not an integer

// switch with empty interface
func printValue(value interface{}){
  switch v := i.(type) {
  case int:
      fmt.Println("Integer:", v)
  case string:
      fmt.Println("String:", v)
  case float64:
      fmt.Println("Float:", v)
  default:
      fmt.Println("Unknown type")
  }
} 
identifyType(42)
identifyType("GoLang")
identifyType(3.14)
```

### Interface inheritence
- function implementing Pet can directly access Speak
  - And must implement both interface 
```go
type Animal interface {
    Speak() string
}
// Child interface embedding Animal
type Pet interface {
    Animal    // Embedding Animal interface
    Play() string
}
```

## Custom Types in Go
- Go allows you to create custom types, which can enhance code readability and type safety. Here's a breakdown of the examples:

**1. Custom Type Based on a Built-in Type (String)**

- You can create custom types based on existing types like `string`, `int`, etc.
- This allows you to add methods specific to your custom type.

```go
type extendedString string

func (e extendedString) endsWithZero() bool {
  length := len(e)
  if length == 0 { //Handle empty string case.
    return false;
  }
  return e[length-1] == '0'
}

func main() {
  var name extendedString = "ohm0"
  fmt.Println("Does name end with zero? :", name.endsWithZero()) //Corrected Println usage.

  var emptyName extendedString = ""
  fmt.Println("Does emptyName end with zero?", emptyName.endsWithZero())
}
```

**2. Custom Function Type**
- You can define custom function types, which can be useful for working with functions as first-class citizens.
- This allows you to create specialized function signatures and add methods to function types

```go
type mySumFunctionDefinition func(a, b, c, d int) int

func (f mySumFunctionDefinition) mySumSubFunction1(a,b int) int {
  return f(a,b,1,1)
}


func main() {
  // Direct use
  var mySumFunction1 mySumFunctionDefinition = func(a, b, c, d int) int {
    return a + b + c + d
  }

  result1 := mySumFunction1(1, 2, 3, 4)
  fmt.Println("Result 1:", result1)

  // Creating a method for the function type.

  result2 := mySumFunction1.mySumSubFunction1(1, 3)
  fmt.Println("Result 2:", result2)
}
```

## GoRoutines

### Make a function goRoutine
```go
func sayHello(){fmt.Println("Hellow")}
func main(){go sayHello()}
```
- With this will exit main without waiting result to finish
- So to wait we use `WaitGroup`

### WaitGroup
- It has 
  - Add : Increment for number of coroutines to wait for
  - Done : make one coroutine as complete
  - Wait : Wait for all coroutines to complete
```go
func sayHello(wg *sync.WaitGroup, i int){
  defer wg.Done()// marks one coroutine to wait for
  fmt.Println(i)
}

func main(){
  var wg sync.WaitGroup

  for i := 0;i< 5;i++ {
    wg.Add(1)// adds one coroutine to wait for
    go sayHello(&wg,i)
  }
  wg.Wait() // wait till all coroutines are done
}
```

### Locks in go 
```go
func increment(wg *sync.WaitGroup) {
	defer wg.Done()
	mutex.Lock()
	counter++
	mutex.Unlock()
}
```

### Channels
- For concurrent message passing, instead of locking
```go
// creating go channel that works with int
ch := make(chan int)
// creating go channel buffer of size 5
ch := make(chan int, 5)

// Sending data into buffer
ch<-3

// reciving data from buffer
var a int = <-ch 
a := <-ch 
fmt.Print(<-ch)
```

- To handle multiple channel

```go
ch1 := make(chan string)
ch2 := make(chan string)

go func() {ch1 <- "Message from ch1"}()
go func() {ch2 <- "Message from ch2"}()

select {
  case msg1 := <-ch1: fmt.Println("Received:", msg1)
  case msg2 := <-ch2: fmt.Println("Received:", msg2)
}
```
#### bi-directional and directional channel
- Go channel are bi-directional but it can be made directional 
- Say a function that only recives data in reciver channel and send data in sender channel

```go
// We can't send data to receiver in the function
// We can only accept (receive) data from receiver in the function
// We can't receive data from sender in the function
// We can only send data to sender in the function
func worker(receiver <-chan int, sender chan<- int) {}
```
#### To close channel 
```go
ch := make(chan int)
close(ch)
```

#### Accessing the buffer
```go
jobs := make(chan int, 5)
for job := range jobs{fmt.Println(job)}
for i:=0;i<10;i++{jobs<-i}
```


### Worker Pool
- Parallel processing of jobs
- Say we create multiple works to process a channel buffer

```go
func worker(jobs <-chan int, results chan<- int, wg *sync.WaitGroup) {}
func main(){
  for i := 1; i <= numWorkers; i++ {
      wg.Add(1)
      go worker(i, jobs, results, &wg)
    }
}
for j := 1; j <= numJobs; j++ {
  jobs <- j
}
```
> ![NOTE]
> - Even though multiple workers are creating the channel pool is shared
> - But here the jobs can be processed parallely
> - Else jobs will be processed one after another if only one worker is there 
> - If job buffer is full then the loop will till buffer is full 
> ```go
> for j := 1; j <= 4*numJobs; j++ {
>   jobs <- j
> }
> ```

## Error Handling
- Error is an built in interface
```go
type error interface {
    Error() string
}
```
- Using error
```go
func divide(a,b int)(int, error){
  if b==0:
    return 0, error.New("Division by zero")
  else:
    return a/b, nil
}
func main() {
    result, err := divide(10, 0)
    if err != nil {
        fmt.Println("Error:", err)
    } else {
        fmt.Println("Result:", result)
    }
}
```
- Creating custom error
```go
type Divide struct{
  Divisor, Divident int
  Message String
}
func (d *Divide) Error() string{
  return fmt.Sprintf(
    "cannot divide %d by %d: %s", 
    e.Dividend, 
    e.Divisor, 
    e.Message,
  )
}

func divider(a,b int)(int, error){
  if b==0:
    return 0, &Divide(a,b,"Divider Error")
  else:
    return a/b, nil
}
```
### Panic and Recover
- For catching server errors we use Panic and Recover
  - `panic` stops execution and begins unwinding the stack.
  - `recover` catches the panic if it's called inside a deferred function.

```go
package main
import "fmt"

func riskyFunction() {
    panic("Something went wrong!")
}

func main() {
    defer func() {
        if r := recover(); r != nil {
            fmt.Println("Recovered from:", r)
        }
    }()
    riskyFunction()
    fmt.Println("This won't be reached due to panic.")
}
```

## IO Reader/Writer

### IO Reader
- Used to read stream of data
- Interface :
  
  ```go
  package io
  // Takes slice as input
  // Return stream size n
  type Reader interface{
    Read(p []byte) (n int, e error)
  }
  ```

> [!NOTE]
> - Before processing error, n bytes must be processed
> - io.EOF error marks end of file

- **Uses**
  - `*os.File` : Used to read data from file
  - `string.Reader` : Create reader to read string stream
  - `http.Request.body`

- **Implementation**

```go
// Creating IO.reader from file
reader, err := os.Open("file1.txt") // reader : *os.file implements *IO.reader

// Creating IO.reader from string
reader := strings.NewReader("Hello World") // reader : *strings.Reader implements *IO.reader
```

- Working with `io.Reader`

```go
func printStream(r io.Reader){
  // this will hold stream data in byte slice
  buffer := make([]byte, 1024)

  for{
    // n is the size of buffer
    // buffer is updated with the data
    n, err := r.Read(buffer)
    // iterating from 0 to n as the entire buffer may not be used
    for _, val := range buffer[:n]{
      fmt.print(string(value))
    }
    // error can be io.EOF marking end of stream
    if err == io.EOF{
      return
    }
    // error occured due to something else
    if err != io.EOF && err != nil{
      return
    }
  }
}

// using printStream
reader, err := os.Open("file1.txt") // reader : *os.file implements *IO.reader
reader := strings.NewReader("Hello World") // reader : *strings.Reader implements *IO.reader
printStream(reader)
```

- Stream reading

```go
// say following is the stream
str := strings.NewReader(
  `12345
6789`
)

// say the buffer size in the printStream function is 3
// So buffer in each iteration will look like
  // 1: 1 2 3
  // 2: 4 5 \n
  // 3: 6 7 8
  // 4: 9
  // 5: io.EOF
```

### IO Writer

- Used to write a stream of data.
- Interface:

  ```go
  package io

  // Takes slice as input
  // Returns number of bytes written n and error
  type Writer interface {
    Write(p []byte) (n int, err error)
  }
  ```

> [!NOTE]
> - Before processing error, n bytes must be processed.
> - Errors indicate issues during the write operation.

- **Uses**
  - `*os.File`: Used to write data to a file.
  - `bytes.Buffer`: Used to write data to an in-memory buffer.
  - `http.ResponseWriter`: Used to write HTTP response data.

- **Implementation**

```go
func main(){
	f, err := os.Create("Output.txt")
	if err != nil {
		panic(err)
	}
	defer f.Close()

	n, err := writeStream("Hello Word", f)
	if err != nil {
		panic(err)
	}
	fmt.Println("Done : ", string(n))
}

func writeStream(s string, f io.Writer )(int, error){
	n, err := f.Write([]byte(s))

	if err != nil {
		return 0,err
	}
	return n,nil
}
```

## Working with JSON
  - Type in Go is stores as array of byte(**uint 8**)
    - `[]byte`
### Converting struct to json

  ```go
  // if json name is not mentioned then data will take name of corrosponding variable
  type Person struct {
  	Name string `json:"name"`
  	Age int `json:"age"`
  	Addresses string `json:"address"`
  }
  type Person1 struct {
  	Name string
  	Age int
  	Addresses string
  }
  func main() {
  	person := Person{ Name: "ohm", Age: 12, Addresses: "address"}
    person1 := Person1{ Name: "ohm", Age: 12, Addresses: "address"}
  	
  	jsonData,  err  := json.Marshal(person)
  	jsonData1, err1 := json.Marshal(person1)
  	fmt.Println(string(jsonData)) // {"name":"ohm","age":12,"address":"address"}
  	fmt.Println(string(jsonData1)) // {"Name":"ohm","Age":12,"Address":"address"}
  }
  ```

### Converting JSON/JSON array to go data types
- Base structs
  ```go
  type Person struct {
  	Name    string `json:"name"`
  	Age     int    `json:"age"`
  	Address string `json:"address"`
  }
  type PersonDelay struct {
  	Name json.RawMessage `json:"name"`
  	Age  json.RawMessage `json:"age"`
  	City string          `json:"city"`
  }

  ```

- Implementations

  ```go
  // JSON to Slice
	jsonData := `[1, 2, 3, 4, 5]`
  var intSlice []int
  err := json.Unmarshal([]byte(jsonData), &intSlice)

  //////////////
  // JSON to map
    // With defined value type
  jsonDataMap := `{"name": "Alice", "ageString": "30", "city": "New York"}`
  var personMap map[string]string // JSON object to map
  json.Unmarshal([]byte(jsonDataMap), &personMap)
    // With undefined value type
  jsonDataMap := `{"name": "Alice", "age": 30, "city": "New York"}`
  var personMap map[string]interface{} // JSON object to map
  json.Unmarshal([]byte(jsonDataMap), &personMap)

  //////////////
  // JSON to Map with delayed parsing
	jsonData := `{"name": "Alice", "age": 30, "city": "New York"}`
	var rawMap map[string]json.RawMessage // Store raw JSON values
	err := json.Unmarshal([]byte(jsonData), &rawMap)
  
  // Delayed parsing
  var name string
	json.Unmarshal(rawMap["name"], &name)
	var age int
	json.Unmarshal(rawMap["age"], &age)

  //////////////
  // JSON to Struct
	jsonData := `{"name":"Jane Doe","age":25,"address":"4321 Oak Avenue"}`
  jsonArray := `[{"name":"John Doe","age":30,"address":"1234 Elm Street"},
	{"name":"Jane Doe","age":25,"address":"4321 Oak Avenue"}]`
	
  // Create a Person variable to hold the unmarshalled data
	var person Person
  var people []Person
	
  // Unmarshal the JSON into the Person struct
	err := json.Unmarshal([]byte(jsonData), &person)
  err1 := json.Unmarshal([]byte(jsonArray), &people)

  //////////////
  // JSON to struct with delayed parsing
	jsonData := `{"name": "Alice", "age": 30, "city": "New York"}`
	var person PersonDelay
	err := json.Unmarshal([]byte(jsonData), &person)

  // Delayed Parsing
	var name string
	json.Unmarshal(person.Name, &name)
	var age int
	json.Unmarshal(person.Age, &age)
  ```

### Converting Request Body to JSON
- Request body is `io.ReadCloser`

  ```go
  package io
  type ReadCloser interface {
  	Reader
  	Closer
  }
  ```

- Converting Request body to JSON

  ```go
  func createPersonHandler(w http.ResponseWriter, r *http.Request) {
	var person Person

  // json.NewDecoder(r.Body) : Returns JSON.Decoder
    // it takes io.Reader as input
  // Decode(v any)
    // Processes the stream and updates the input
  err := json.NewDecoder(r.Body).Decode(&person)  
  }
  ```

### Sending JSON response

```go
func createPersonHandler(w http.ResponseWriter, r *http.Request) {
	
	person := Person{ Name: "ohm", Age: 12, Addresses: "address"}
	jsonData,  err  := json.Marshal(person)

	// Respond with the received data in JSON format
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)

  // If response code is not set in headers it is assumed as 200
	w.Write(jsonResponse)
}
```


## Misc

### Reflect
| Code                                 | Description                                                                                                                                                                                                                            |
|--------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `reflect.TypeOf(value)`              | Returns the `reflect.Type` of the given `value`. This `reflect.Type` represents the type of the value at runtime and provides methods to inspect its properties.                                                                       |
| `t.Name()`                           | Returns the name of the type `t` as a string. For struct types, this returns the struct's name.                                                                                                                                     |
| `t.PkgPath()`                        | Returns the package path of the type `t` as a string. For built-in types, it returns an empty string.                                                                                                                            |
| `t.Kind()`                           | Returns the `reflect.Kind` of the type `t`. `reflect.Kind` represents the specific kind of type, such as `reflect.Struct`, `reflect.Int`, `reflect.String`, etc. This is useful for determining the underlying type category. |
| `t.NumField()`                       | Returns the number of struct fields (if `t` is a struct). Panics if `t` is not a struct.                                                                                                                                            |
| `t.Field(i)`                         | Returns the `reflect.StructField` at index `i` (if `t` is a struct). Panics if `t` is not a struct or if `i` is out of range.                                                                                                         |
| `t.FieldByName(name)`                | Returns the `reflect.StructField` with the given `name` and a boolean indicating whether the field was found (if `t` is a struct).                                                                                                   |
| `reflect.ValueOf(value)`             | Returns a `reflect.Value` representing the value of `value`. `reflect.Value` provides methods for examining and manipulating the value.                                                                                             |
| `v.Type()`                           | Returns the `reflect.Type` of the value `v`.                                                                                                                                                                                         |
| `v.Kind()`                           | Returns the `reflect.Kind` of the value `v`.                                                                                                                                                                                         |
| `v.Interface()`                      | Returns the value `v` as an `interface{}`. This allows you to convert the `reflect.Value` back to its original type.                                                                                                                 |
| `v.Field(i)`                         | Returns the `reflect.Value` of the struct field at index `i` (if `v` is a struct). Panics if `v` is not a struct.                                                                                                                     |
| `v.FieldByName(name)`                | Returns the `reflect.Value` of the struct field with the given `name` (if `v` is a struct).                                                                                                                                           |
| `v.Set(x)`                           | Sets the value of `v` to `x`. `v` must be addressable and settable. Panics if `v` is not settable or if `x`'s type is not assignable to `v`'s type.                                                                              |
| `v.Elem()`                           | Returns the value that the interface `v` contains or that the pointer `v` points to. Panics if `v`'s `Kind` is not `reflect.Interface` or `reflect.Ptr`.                                                                           |
| `reflect.Indirect(v)`                | Returns the value that the interface `v` contains or that the pointer `v` points to, like `v.Elem()`, but handles `nil` pointers gracefully by returning the zero `reflect.Value` of the pointed-to type. |
| `reflect.Zero(t)`                    | Returns the zero `reflect.Value` for the type `t`.                                                                                                                                                                                     |
| `reflect.New(t)`                     | Returns a `reflect.Value` representing a pointer to a new zero value for the type `t`.                                                                                                                                              |
| `reflect.MakeSlice(t, len, cap)`      | Creates a new slice `reflect.Value` with the given type `t`, length `len`, and capacity `cap`.                                                                                                                                         |
| `reflect.MakeMap(t)`                 | Creates a new map `reflect.Value` with the given type `t`.                                                                                                                                                                            |