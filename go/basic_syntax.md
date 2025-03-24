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
  - [GoRoutines](#goroutines)
    - [Make a function goRoutine](#make-a-function-goroutine)
    - [WaitGroup](#waitgroup)
    - [Locks in go](#locks-in-go)
    - [Channels](#channels)
  - [To learn](#to-learn)

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

- Go channel are bi-directional but it can be made directional 
- Say a function that only recives data in reciver channel and send data in sender channel

```go
// We can't send data to receiver in the function
// We can only accept (receive) data from receiver in the function
// We can't receive data from sender in the function
// We can only send data to sender in the function
func worker(receiver <-chan int, sender chan<- int) {}
```

## To learn
- make()
- reflect
  - TypeOf
  - Kind
- go func()
- Select
- Using set, map, slice, stack, queue, unordered_set, unordered_map
- String 
  - Substring
  - and more string function
- Sync 
  - Pool, 
  - New: func() interface
- Switch 
  - type