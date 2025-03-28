# Net/Http
- [Net/Http](#nethttp)
  - [Mux (Multiplexer)](#mux-multiplexer)
  - [Handling HTTP request](#handling-http-request)
    - [Using functional implementation of http.Hanlder](#using-functional-implementation-of-httphanlder)
    - [Using a Struct as an HTTP Handler](#using-a-struct-as-an-http-handler)
    - [`http.ListenAndServer`](#httplistenandserver)
      - [Gracefully stopping server](#gracefully-stopping-server)
    - [Custom HTTP server](#custom-http-server)
  - [HTTP path and query params](#http-path-and-query-params)
  - [Gorilla/Mux (github.com/gorilla/mux)](#gorillamux-githubcomgorillamux)
    - [Basic http server](#basic-http-server)
    - [URL Path / Query Params](#url-path--query-params)
    - [Regex in path params](#regex-in-path-params)
    - [HTTP methods \[GET, PUT, POST, DELETE ...\]](#http-methods-get-put-post-delete-)
  - [Sub Routes](#sub-routes)
  - [Adding Middleware](#adding-middleware)
  - [Handling CORS `github.com/gorilla/handlers`](#handling-cors-githubcomgorillahandlers)
    - [Middleware in CORS](#middleware-in-cors)


## Mux (Multiplexer)
   device that enables the simultaneous transmission of several messages or signals over one communications channel

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