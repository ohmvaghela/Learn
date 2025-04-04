# Net/Http
- [Net/Http](#nethttp)
  - [Mux (Multiplexer)](#mux-multiplexer)
- [Handling Incommig Request](#handling-incommig-request)
  - [Handling HTTP request](#handling-http-request)
    - [Using functional implementation of http.Hanlder](#using-functional-implementation-of-httphanlder)
    - [Using a Struct as an HTTP Handler](#using-a-struct-as-an-http-handler)
    - [`http.ListenAndServer`](#httplistenandserver)
      - [Gracefully stopping server](#gracefully-stopping-server)
    - [Custom HTTP server](#custom-http-server)
  - [HTTP path and query params](#http-path-and-query-params)
  - [Setting Headers](#setting-headers)
  - [Gorilla/Mux (github.com/gorilla/mux)](#gorillamux-githubcomgorillamux)
    - [Basic http server](#basic-http-server)
    - [URL Path / Query Params](#url-path--query-params)
    - [Regex in path params](#regex-in-path-params)
    - [HTTP methods \[GET, PUT, POST, DELETE ...\]](#http-methods-get-put-post-delete-)
  - [Sub Routes](#sub-routes)
  - [Adding Middleware](#adding-middleware)
  - [Handling CORS `github.com/gorilla/handlers`](#handling-cors-githubcomgorillahandlers)
    - [Middleware in CORS](#middleware-in-cors)
  - [Working with JSON](#working-with-json)
    - [Converting struct to json](#converting-struct-to-json)
    - [Converting JSON/JSON array to go data types](#converting-jsonjson-array-to-go-data-types)
    - [Converting Request Body to JSON](#converting-request-body-to-json)
    - [Sending JSON response](#sending-json-response)
- [Making a request](#making-a-request)
  - [Defining request](#defining-request)
  - [Making Request](#making-request)
    - [client.Do](#clientdo)
    - [Client.Go / Client.Post / Client.Head](#clientgo--clientpost--clienthead)
    - [Full code](#full-code)
- [http.Transport, http.RoundTripper](#httptransport-httproundtripper)
  - [RoundTripper](#roundtripper)
  - [Transport](#transport)
  - [Example Uses](#example-uses)
    - [1. http.DefaultTransport / Custom http.Transport](#1-httpdefaulttransport--custom-httptransport)
    - [2. Wrapping Multiple Transport](#2-wrapping-multiple-transport)
    - [3. Logging Request and response using RoundTripper](#3-logging-request-and-response-using-roundtripper)


## Mux (Multiplexer)
   device that enables the simultaneous transmission of several messages or signals over one communications channel

# Handling Incommig Request

## Handling HTTP request
- Go provide `Handler` interface to process HTTP request

```go
type Handler interface {
    ServeHTTP(w http.ResponseWriter, r *http.Request)
}
```
  ### Using functional implementation of http.Hanlder
- To handle an HTTP request, we need to implement the `ServeHTTP` method and register it using `http.Handle` function.
  - Syntax
  
  ```go
  // syntax
  func http.Handle(pattern string, handler http.Handler)
  ```

  - Use

  ```go
  // handler function
  func handler(w http.ResponseWriter, r *http.Request){
  	fmt.Fprint(w, "Hello World")
  }
  // accepting request
  http.Handle("/", http.HandlerFunc(handler))
  ```

- `http.HandlerFunc` is a type alias for a function that matches the signature:
  - Since functions alone do not implement `http.Handler`, Go provides `http.HandlerFunc` to wrap functions and make them valid `http.Handler` implementations.
  - `http.HandlerFunc` : Convert `handler` function to `http.Handler` interface

  ```go
  type HandlerFunc func(http.ResponseWriter, *http.Request)
  // following makes HandlerFunc child implementation of Handler interface
  func (f HandlerFunc) ServeHTTP(w http.ResponseWriter, r *http.Request) {
    f(w, r)
  }
  ```

- This allows

  ```go
  http.Handle("/", http.HandlerFunc(handler))
  ```

### Using a Struct as an HTTP Handler
- Instead of a function, we can pass a struct that implements http.Handler.
- Another way is the pass Struct implementation of `http.Handler`, as `http.handle` can accept both `http.handler` function implementation and `http.handler` struct implementation

  ```go
  type myHandlerFunc struct {
  	fn func(http.ResponseWriter, *http.Request)
  }

  func (h myHandlerFunc) ServeHTTP(w http.ResponseWriter, r *http.Request) {
  	h.fn(w, r)
  }


  func Myhandler(w http.ResponseWriter, r *http.Request){
  	fmt.Fprintf(w,"Hello world")
  }

  func main() {
  	handler := myHandlerFunc{fn: Myhandler}
  	http.Handle("/", handler)
  	http.ListenAndServe(":8080", nil)
  }
  ```

- To ease the use of http provides `http.HandleFunc`
  - Syntax
  
  ```go
  func HandleFunc(pattern string, handler func(http.ResponseWriter, *http.Request))
  ```

  - Use
  
  ```go
  func homeHandler(w http.ResponseWriter, r *http.Request) {
  	fmt.Fprintln(w, "Hello from homeHandler!")
  }

  func main() {
  	http.HandleFunc("/", homeHandler) // Directly pass function
  	http.ListenAndServe(":8080", nil)
  }
  ```

> [!NOTE]
>  Internal implementation of HandleFunc is : 
> ```go
> func HandleFunc(pattern string, handler func(http.ResponseWriter, *http.Request)){
>   http.Handle(pattern, http.HanderFunc(handler))
> }
> ```

### `http.ListenAndServer`
-  It starts an HTTP server and listens for incoming requests.
- Syntax

  ```go
  func ListenAndServe(addr string, handler http.Handler) error
  ```

- If `nil` is provided to hander : Go uses http.DefaultServeMux, It's the default router in Go.

#### Gracefully stopping server

```go
err := http.ListenAndServe(":8080", nil)
if err != nil {
	fmt.Println("Server Error:", err)
}
```

> [!CAUTION]
> - `http.HandleFunc` and `http.Handle` register handlers globally
>   - Any modification affects the entire application.
>    ```go
>    http.HandleFunc("/", homeHandler) // Registers globally
>    // Later, another developer might add this:
>    http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
>    	fmt.Fprintf(w, "New Handler")
>    })
>    ```
> - Here path : `/` is overriden without any warning

> [!CAUTION]
> - http.DefaultServeMux does not support multiple routers.
> - Eg say we want
>   - A main router for public pages.
>   - An API router for /api/* endpoints.
>   - An admin router for /admin/* endpoints
> - `http.DefaultServeMux` doesn’t allow multiple routers easily
> - Hence we use  third-party router like `gorilla/mux`.

### Custom HTTP server
- Instead of using `http.ListenAndServe`, we can create a custom HTTP server using `http.Server`.
```go
package main

import (
	"fmt"
	"net/http"
	"time"
)

func handler(w http.ResponseWriter, r *http.Request) {
	time.Sleep(3 * time.Second) // Simulate delay
	fmt.Fprintf(w, "Hello, World from handler!")
}

func handler1(w http.ResponseWriter, r *http.Request) {
	time.Sleep(8 * time.Second) // Simulate delay
	fmt.Fprintf(w, "Hello, World from handler1!")
}

func main() {
	http.HandleFunc("/1", handler)
	http.HandleFunc("/2", handler1)

	server := &http.Server{
	Addr:	 ":8080",
	ReadTimeout:  5 * time.Second,
	WriteTimeout: 7 * time.Second,
	IdleTimeout:  9 * time.Second,
	}

	fmt.Println("Server running on :8080...")
	if err := server.ListenAndServe(); err != nil {
	fmt.Println("Server error:", err)
	}
}
```

> [!CAUTION]
> - Go’s default `http.Server` is single-threaded, handling incoming requests concurrently
> - No loadbalancing


## HTTP path and query params

```go
package main

import (
	"fmt"
	"net/http"
	"strings"
)

func greetHandler(w http.ResponseWriter, r *http.Request) {
	// Extract the URL parameter from the path "/greet/{name}"
	parts := strings.Split(r.URL.Path, "/")
	name := parts[len(parts)-1]

	query := r.URL.Query().Get("q")

	fmt.Fprintf(w, "Hello, %s!, %s", name, query)
}

func main() {
	http.HandleFunc("/greet/", greetHandler) // Handle URLs like /greet/John?q=10
	http.ListenAndServe(":8080", nil)
}
```

## Setting Headers

```go
func createPersonHandler(w http.ResponseWriter, r *http.Request) {
  // Set multiple headers
  w.Header().Set("Content-Type", "application/json")
  w.Header().Set("Cache-Control", "no-cache") // Example: prevent caching
  w.Header().Set("X-Custom-Header", "MyValue") // Example: custom header
  w.Header().Set("Location", "/people") //Example: location header
  w.WriteHeader(http.StatusCreated) // Set status code after setting headers

}
```

## Gorilla/Mux (github.com/gorilla/mux)

### Basic http server

```go
package main

import (
	"fmt"
	"github.com/gorilla/mux"
	"net/http"
)

func Handler1(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Welcome to Page1!")
}
func Handler2(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Welcome to Page2!")
}

func main(){
	var r *mux.Router = mux.NewRouter()
  r.HandleFunc("/1", Handler1)
  r.HandleFunc("/2", Handler2)
  http.ListenAndServe(":8080",r)
}
```

### URL Path / Query Params

```go
func Handler(w http.ResponseWriter, r *http.Request) {
	// Extract the 'name' parameter from the URL
	vars := mux.Vars(r)
	name := vars["name"]
	fmt.Fprintf(w, "Hello, %s!", name)
  
  query := r.URL.Query().Get("q")
	fmt.Fprintf(w, "You searched for: %s", query)
}

/// main
r.HandleFunc("/{name}", Handler)
```

### Regex in path params

```go
func numberHandler(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	number := vars["number"]
	fmt.Fprintf(w, "You entered the number: %s", number)
}

func main() {
	r := mux.NewRouter()
	r.HandleFunc("/number/{number:[0-9]+}", numberHandler)
	http.ListenAndServe(":8080", r)
}
```

### HTTP methods [GET, PUT, POST, DELETE ...]

```go
func commonHandler(w http.ResponseWriter, r *http.Request) {
  fmt.Fprintf(w, "Handling request with method: %s\n", r.Method)
  // Your common logic here
}
func deleteHandler(w http.ResponseWriter, r *http.Request) {
  fmt.Fprintf(w, "Handling request with method: %s\n", r.Method)
  // Your common logic here
}

func main() {
  r := mux.NewRouter()

  r.HandleFunc("/common", commonHandler).Methods("GET", "POST", "PUT") // Same handler for GET, POST, PUT.
  r.HandleFunc("/delete", deleteHandler).Methods("DELETE") // different endpoint, same handler.

  fmt.Println("Server running on :8080...")
  http.ListenAndServe(":8080", r)
}
```

## Sub Routes

- Sub route 1

```go
package api1

import (
	"fmt"
	"github.com/gorilla/mux"
	"net/http"
)

func api1Handler1(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "This is API1 - Endpoint 1")
}

func api1Handler2(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "This is API1 - Endpoint 2")
}

func RegisterRoutes(r *mux.Router) {
	r.HandleFunc("/1", api1Handler1).Methods("GET")
	r.HandleFunc("/2", api1Handler2).Methods("GET")
}
```

- Main route

```go
package main

import (
  "fmt" "github.com/gorilla/mux" "log" "net/http" "go-mux-app/api1"
)
func main(){
  r := mux.NewRouter()
  api_Router_1 := r.PathPrefix("/api1").Subrouter()
  api1.RegisterRoutes(api_Router_1)
  http.ListenAndServe(":8080", r)
}
```

## Adding Middleware
- Middlewares and Handles

```go
package main

import (
  "fmt"
  "net/http"
  "github.com/gorilla/mux"
)

func middleware1(next http.Handler) http.Handler {
  return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
    fmt.Println("Middleware 1: Before handler")
    next.ServeHTTP(w, r)
    fmt.Println("Middleware 1: After handler")
  })
}

func middleware2(next http.Handler) http.Handler {
  return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
    fmt.Println("Middleware 2: Before handler")
    next.ServeHTTP(w, r)
    fmt.Println("Middleware 2: After handler")
  })
}

func middleware3(next http.Handler) http.Handler {
  return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
    fmt.Println("Middleware 3: Before Handler")
    next.ServeHTTP(w, r)
    fmt.Println("Middleware 3: After Handler")
  })
}

func handler(w http.ResponseWriter, r *http.Request) {
  fmt.Fprintln(w, "Hello from handler!")
}
```

- Main function

```go
func main() {
  r := mux.NewRouter()

  // Apply middleware chain to /protected route.
  protected := r.PathPrefix("/protected").Subrouter()
  protected.Use(middleware1, middleware2, middleware3)
  protected.HandleFunc("", handler) // any path under /protected

  // Apply a different middleware to /other route.
  other := r.PathPrefix("/other").Subrouter()
  other.Use(middleware1)
  other.HandleFunc("", handler) // any path under /other

  // No middleware for /public route.
  r.HandleFunc("/public", handler)

  fmt.Println("Server running on :8080...")
  http.ListenAndServe(":8080", r)
}
```

## Handling CORS `github.com/gorilla/handlers`
- `github.com/gorilla/handlers` provides the handlers.CORS middleware to handle CORS easily

```go
package main

import (
	"fmt"
	"github.com/gorilla/handlers"
	"github.com/gorilla/mux"
	"net/http"
)

func homeHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Welcome to the Home Page!")
}

func main() {
	r := mux.NewRouter()
	r.HandleFunc("/", homeHandler)

	// Enable CORS
	corsMiddleware := handlers.CORS(
		handlers.AllowedOrigins([]string{"*"}), // Allow all origins
		handlers.AllowedMethods([]string{"GET", "POST", "PUT", "DELETE"}),
		handlers.AllowedHeaders([]string{"Content-Type"}),
	)

	http.ListenAndServe(":8080", corsMiddleware(r))
}
```

### Middleware in CORS
- Order of middleware exection
  - corsMiddleware
  - contentTypeMiddleware
  - authMiddleware
  - cookieMiddleware
  
  ```go
  func main() {
  	r := mux.NewRouter()
  	r.HandleFunc("/", homeHandler)

  	// CORS settings
  	corsMiddleware := handlers.CORS(
  		handlers.AllowedOrigins([]string{"*"}), // Allow all origins
  		handlers.AllowedMethods([]string{"GET", "POST", "PUT", "DELETE"}),
  		handlers.AllowedHeaders([]string{"Content-Type", "Authorization"}),
  	)

  	// Apply middlewares
  	securedRouter := corsMiddleware(
      contentTypeMiddleware(
        authMiddleware(
          cookieMiddleware(r))))

  	http.ListenAndServe(":8080", securedRouter)
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
	
  
  // Respond with the received data in JSON format
	w.Header().Set("Content-Type", "application/json")
  // If response code is not set in headers it is assumed as 200
	w.WriteHeader(http.StatusCreated)

  // Data to be sent
	person := Person{ Name: "ohm", Age: 12, Addresses: "address"}

  // 1st approah
  jsonResponse,  err  := json.Marshal(person)
	w.Write(jsonResponse)

  // 2nd approach : More efficient
  err := json.NewEncoder(w).Encode(jsonResponse)
}
```

# Making a request

- To make a request we can use `http.Client` as shown

  ```go
  client := &http.Client{Timeout : 10*time.Second}
  ```

## Defining request
  
  - Syntax
  
  ```go
  // With context
  func http.NewRequestWithContext(ctx context.Context, method string, url string, body io.Reader) (*http.Request, error)

  // Without context
  func http.NewRequest(method string, url string, body io.Reader) (*http.Request, error)
  ```

  - Use
  
  ```go
  context := context.Background()
  // Request with context
  req,err := http.NewRequestWithContext(context, http.MethodGet, "/url", nil)
  req,err := http.NewRequest(http.MethodGet, "/url", nil)
  ```

  - Request with IO reader

  ```go
  // Parsing Map
  type url.Values map[string][]string
  data := url.Values{
      "key1": {"value1"},
      "key2": {"value2"},
  }
  // returns string
  encodedData := data.Encode()

  req, err := http.NewRequestWithContext(
    context,
    http.MethodPost, 
    "https://example.com/submit", 
    strings.NewReader(encodedData)
  )

  // Passing JSON data
  type Payload struct {
    Name  string `json:"name"`
    Value int    `json:"value"`
  }

  payload := Payload{Name: "example", Value: 123}
  payloadBytes, err := json.Marshal(payload)
  if err != nil {
      return nil, err
  }

  req, err := http.NewRequestWithContext(
    context,
    http.MethodPost, 
    "https://example.com/api", 
    bytes.NewBuffer(payloadBytes)
  )
  ```

- Setting Headers
  
  ```go
  req.Header.Set("Content-Type", "application/json")
  ```

## Making Request

### client.Do
- Used when we have defined `*http.Request`

```go
// Syntax
func (c *http.Client) Do(req *http.Request) (*http.Response, error)
// use
response, err := client.Do(req)
```

### Client.Go / Client.Post / Client.Head

```go
// Syntax
func Get(url string) (resp *http.Response, err error)
func Post(url string, contentType string, body io.Reader) (resp *http.Response, err error)
func Head(url string) (resp *http.Response, err error)
func PostForm(url string, data url.Values) (resp *http.Response, err error)
type url.Values map[string][]string


// use
response, err := client.Get("/url")
response, err := client.Post("/url", "application/json", nil)
response, err := client.Head("/url")
response, err := client.PostForm("/url", map[string][]string{
  "key1" : ["value10","value11"],
  "key2" : ["value20","value21"]
})
```

- We need to close the `IO.readCloser`, if there is huge chunk of data then after stream is read then the stream needs to be closed 

```go
resposne.Body.Close()
```

### Full code

```go
func main() {
  mycontext := context.Background() // Create a background context.
  client := &http.Client{Timeout: 10 * time.Second} // Create HTTP client with 10-second timeout.

  req, err := http.NewRequestWithContext(
    mycontext, 
    http.MethodGet, 
    "https://example.com", 
    nil
  ) // Create GET request with context.
  if err != nil {return}

  response, err := client.Do(req) // Send the HTTP request.
  if err != nil {return}

  defer response.Body.Close() // Close the response body when the function returns.

  body, err := io.ReadAll(response.Body) // Read the response body.
  if err != nil {return}

  fmt.Println(string(body)) // Print the response body.
}
```

# http.Transport, http.RoundTripper 

## RoundTripper
- RoundTrip executes a **single** HTTP transaction
- And returns response
- It does not handle high level protocals like redirects, authentication, or cookies.
- Higher-level logic should be handled by http.Client, not RoundTripper
  
  ```go
  type RoundTripper interface {
    // returns error==nil if response is recieved
    RoundTrip(*Request) (*Response, error)
  }
  ```

> [!IMPORTANT]
> - RoundTrip should not attempt to interpret the response
> - RoundTrip should not modify the request
>   - Except closing the Request's Body
>   - Adding Headers

> [!CAUTION]
> <h3> A RoundTripper must be safe for concurrent use by multiple goroutines <h3>

## Transport
- `http.Transport` is a default implementation of `http.RoundTripper` 
- Generally when creating custom `Transport` or custom implementaion `RoundTripper` we couple them with defaultTransport or Base Transport using Decorator Pattern

  ```go
  // Basic implementation of RoundTripper
  type Transport struct {
      Proxy func(*Request) (*url.URL, error)
      DialContext func(ctx context.Context, network, addr string) (net.Conn, error)
      TLSClientConfig *tls.Config
      DisableKeepAlives bool
      MaxIdleConns int
      MaxIdleConnsPerHost int
      ResponseHeaderTimeout time.Duration
  }
  
  // Default Implementation of Transport
  var DefaultTransport RoundTripper = &Transport{
  	Proxy: ProxyFromEnvironment,
  	DialContext: defaultTransportDialContext(&net.Dialer{
  		Timeout:   30 * time.Second,
  		KeepAlive: 30 * time.Second,
  	}),
  	ForceAttemptHTTP2:     true,
  	MaxIdleConns:          100,
  	IdleConnTimeout:       90 * time.Second,
  	TLSHandshakeTimeout:   10 * time.Second,
  	ExpectContinueTimeout: 1 * time.Second,
  }
  ```

## Example Uses

### 1. http.DefaultTransport / Custom http.Transport 

```go
func createNewRequestAndPrintBody(client *http.Client){
    req, _ := http.NewRequest("GET", "https://httpbin.org/get", nil)
    resp, err := client.Do(req)
    if err != nil {return}
    
    // Print Body
    body, _ := ioutil.ReadAll(resp.Body)
    fmt.Println(string(body))
}

func main() {
    // 1. Create a custom Transport with timeout and TLS settings
    transport := &http.Transport{
        // Ignore TLS verification
        TLSClientConfig: &tls.Config{InsecureSkipVerify: true}, 
        MaxIdleConns:    10,
        IdleConnTimeout: 30 * time.Second,
    }
    // Rest functionalities of http.Transport are ignored

    // 2. Using DefaultTransport
    transport := http.DefaultTransport

    // Creating http Client 
    client := &http.Client{Transport: transport}

    // Creating and sending request and recieving response 
    createNewRequestAndPrintBody(client)
}
```

### 2. Wrapping Multiple Transport

- Custom RoundTrippers

```go
type CustomRoundTripper1 struct {
  baseTransport http.RoundTripper
}

func (c *CustomRoundTripper1) RoundTrip(req *http.Request) (*http.Response, error){
  // User Logic
  // Forwarding request to underlying transporter
  response, err := c.baseTransport.RoundTrip(req)
  // Handling error
  // Returning response 
  return response, err
}
type CustomRoundTripper2 struct {
  baseTransport http.RoundTripper
}

func (c *CustomRoundTripper2) RoundTrip(req *http.Request) (*http.Response, error){
  // User Logic
  // Forwarding request to underlying transporter
  response, err := c.baseTransport.RoundTrip(req)
  // Handling error
  // Returning response 
  return response, err
}
```

- Main function 

  ```go
  func main() {
      client := &http.Client{
        Transport: &CustomRoundTripper1{
          Transport: &CustomRoundTripper2{
            Transport: http.DefaultTransport, // Use the default transport
          },
        },
      }

    // Creating and sending request and recieving response 
    createNewRequestAndPrintBody(client)
  }
  ```
### 3. Logging Request and response using RoundTripper

- LoggingRoundTripper

  ```go
  type loggingRoundTripper struct {
  	nextRoundTripper http.RoundTripper
  	logger           io.Writer
  }
  ```

- RoundTrip implementation

  ```go
  func (lrt *loggingRoundTripper) RoundTrip(req *http.Request) (*http.Response, error) {
    // Prints request before sending
  	fmt.Fprintf(lrt.logger, "[Request] %s %s\n", req.Method, req.URL.String())

  	// Perform the request using the next RoundTripper in the chain
  	resp, err := lrt.nextRoundTripper.RoundTrip(req)
  	if err != nil {
  		fmt.Fprintf(lrt.logger, "[Error] Request failed: %v\n", err)
  		return nil, err
  	}

  	// Read response body for logging
  	readBody(resp, lrt.logger, req.Method)

  	return resp, nil
  }
  ```

- Body reader

  ```go
  func readBody(resp *http.Response, logger io.Writer, httpMethod string) {
  	var bodyBuf bytes.Buffer
  	_, err := io.Copy(&bodyBuf, resp.Body)
  	if err != nil {return}

  	// Restore the response body so that it can be read again
  	resp.Body = io.NopCloser(bytes.NewReader(bodyBuf.Bytes()))

  	// Log the response details
  	fmt.Fprintf(logger, "[Response] %s %d %s \n", httpMethod, resp.StatusCode, http.StatusText(resp.StatusCode))
  	fmt.Fprintf(logger, "[Response Body] %s\n", bodyBuf.String())
  }
  ```

- Main funciton

  ```go
  func main() {
  	// Create a logging transport that logs only to stdout
  	loggingTransport := &loggingRoundTripper{
  		nextRoundTripper: http.DefaultTransport,
  		logger:           os.Stdout,
  	}

  	// Create an HTTP client with the logging transport
  	client := &http.Client{
  		Transport: loggingTransport,
  	}

    // Create request
    req, _ := http.NewRequest("GET", "https://httpbin.org/get", nil)
  	// Send request 
    // transport will log request and send request to url
    // Then will recieve response and print body and return resposne
    resp, err := client.Do(req)

    // Error handling and closing body
    if err != nil {return}
  	defer resp.Body.Close()
  }
  ```

- Output

  ```yaml
  [Request] GET https://httpbin.org/get
  [Response] GET 200 OK 
  [Response Body] {
    "args": {}, 
    "headers": {
      "Accept-Encoding": "gzip", 
      "Host": "httpbin.org", 
      "User-Agent": "Go-http-client/2.0", 
      "X-Amzn-Trace-Id": "Root=1-67ecfbac-7e8cabae34096e7a218b3cde"
    }, 
    "origin": "49.36.88.42", 
    "url": "https://httpbin.org/get"
  }
  ```

> [!NOTE]
> - Uses of RoundTripper
>   1. Custom Logging & Monitoring
>   2. Custom Authentication (Adding Headers)
>   3. Retry Mechanism for Unstable Networks (Limiting Client retries)
>   4. Caching Responses (Reducing API Calls)

