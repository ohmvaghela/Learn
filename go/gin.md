# Gin
- Built on top on `net/http` 
- Faster then `net/http`, handles JSON binding
- Easy-to-use middleware support

<h2> Content table </h2>

- [Gin](#gin)
  - [Basic Gin Server](#basic-gin-server)
  - [Gin Context](#gin-context)
  - [Routers (IRouter, IRoute, Engine, RouterGroup)](#routers-irouter-iroute-engine-routergroup)
  - [Working with JSON](#working-with-json)
  - [Getting path and query params from URL](#getting-path-and-query-params-from-url)
  - [Handling Form data / Multipart Form data](#handling-form-data--multipart-form-data)
    - [Simple Form data(x-www-form-urlencoded)](#simple-form-datax-www-form-urlencoded)
    - [Handle Single file from Multipart form](#handle-single-file-from-multipart-form)
    - [Handle Multiple file from Multipart form](#handle-multiple-file-from-multipart-form)
    - [Sending Single file](#sending-single-file)

## Basic Gin Server

```go
package main

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

func main() {
	// Create a Gin router
	router := gin.Default()

	// Define a simple GET route
	router.GET("/", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"message": "Welcome to Gin!",
		})
	})

	// Start the server on port 8080
	router.Run(":8080")
}
```

> [!NOTE]
> ```go
> gin.H : type H map[string]any
> ```


## Gin Context
- Struct code

```go
package gin
type Context struct {
	Request *http.Request
	Writer  ResponseWriter
	Params Params
	Keys map[string]any
	Errors errorMsgs
	Accepted []string
}
```

- It can be taken as advanced version of `Handler` 
- **Parts of Context**
  - `Params Params` : get URL params
  
    ```go
    r.GET("/users/:userID/:userName", func(c *gin.Context) {
        userID := c.Param("userID")
        userName := c.Param("userName")
    })
    ```
  
  - `Keys map[string]any` : allows data to be shared between middleware

    ```go
    r := gin.Default()
    // {Key:value} pairs can be added to context using Set 
    r.Use(func(c *gin.Context) {
      c.Set("user_role", "admin")
      c.Next()
    })
    r.GET("/admin-panel", func(c *gin.Context) {
      // Data can be fetched using Get
      // it returns value string, exists bool
      role, exists := c.Get("user_role")
    })
    ```
  
  - `Errors errorMsgs` : Error messages can be collected to be processed later, or at the en
    - Example : Say error is caught in middleware
      - But the error is processed in actual request
      - Alternaltely it can also be processed in same or next or prev chain of middleware
    ```go
    r := gin.Default()
    r.Use(func(c *gin.Context) {
      if c.Query("invalid") == "true"{
        // adding error message to Errors      
        c.Error(errors.New("invalid parameter"))
      }
      c.Next()
    })
    r.GET("/check", func(c *gin.Context){
      if len(c.Errors) > 0 {
        // processing errors
        c.JSON(http.StatusBadRequest, gin.H{"errors": c.Errors.Errors()})
        return
      }
      c.JSON(http.StatusOK, gin.H{"message": "ok"})
    })
    ```

  - `Accepted []string` : Used to manage content negotiation
    - Like list of contents to be accepted
    - ```go
      [ "application/json", "application/xml" ]
      ```
  
## Routers (IRouter, IRoute, Engine, RouterGroup)
- `gin.IRouter` is an interface that `gin.Engine` and `gin.RouterGroup` implement
- Creating a new `gin.Engine` instance

  ```go
  var router gin.IRouter = gin.Default()
  router := gin.Default()
  ```

  - Craeting a basic route and starting server
  
    ```go
    router.GET("/", func(c *gin.Context) {
    	c.String(http.StatusOK, "Hello, World!")
    })
    router.Run(":8080")
    ```

- Creating RouterGroup
  - gin.RouterGroup is used to group routes under a common prefix
  
    ```go
    router := gin.Default()
    api := router.Group("/api")
    // adding routes  
  	api.GET("/users", getUsers)
  	api.POST("/users", createUser)
    
    // Generally these groups are kept in code block
    /*
  	{
      api.GET("/users", getUsers)
    	api.POST("/users", createUser)
    }
    */

    router.Run(":8080")
    ```

  - Supporting functions

    ```go
    func getUsers(c *gin.Context) {
      c.JSON(http.StatusOK, users)
    }
    func createUser(c *gin.Context) {
      c.JSON(http.StatusOK, users)
    }
    ```

## Working with JSON
- We use `gin.Context's` function `ShouldBindJSON(v any)` to check if input body can be parsed as required json object
- Or else throw error

  ```go
  if err := c.ShouldBindJSON(&json); err != nil {
    c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
    return
  }
  ```

- Full implementation

  ```go
  router.POST("/create", func(c *gin.Context) {
    var json struct {
      Name string `json:"name"`
      Age  int    `json:"age"`
    }

    // Bind JSON data from the request body to the struct
    if err := c.ShouldBindJSON(&json); err != nil {
      c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
      return
    }

    c.JSON(http.StatusOK, gin.H{
      "status": "Data received",
      "name":   json.Name,
      "age":    json.Age,
    })
  })
  ```

## Getting path and query params from URL
- For getting query params we can use Query
  - But it will return nil if query does not exist
  - So we use DefaultQuery to get default value
```go
router.GET("/search/:id", func(c *gin.Context) {
  // Getting Query Params
  name := c.DefaultQuery("name", "Guest")
  // Getting Path Params
  id := c.Param("id")
  c.JSON(http.StatusOK, gin.H{
    "message": "Search successful",
    "name":    name,
  })
})
```

## Handling Form data / Multipart Form data

### Simple Form data(x-www-form-urlencoded)
- Syntax

  ```go
  name := c.PostForm("name")
  ```

- Full code

  ```go
  router.POST("/submit", func(c *gin.Context) {
      name := c.PostForm("name")
      email := c.PostForm("email")
  })
  ```

### Handle Single file from Multipart form

- Syntax

  ```go
  // FormFile returns : *multipart.FileHeader, error
  file, err := c.FormFile("file")
  ```

- `multipart.FileHeader`
  ```go
  package multipart
  type FileHeader struct {
  	Filename string
  	Header   textproto.MIMEHeader
  	Size     int64
  }
  ```

- Saving file on server
  
  ```go
  dst := "./uploads/" + file.Filename
  c.SaveUploadedFile(file, dst)
  ```

### Handle Multiple file from Multipart form
  - Syntax

  ```go
  // c.MultipartForm() returs : multipart.Form, error
  form, err := c.MultipartForm()

  // form.File["files"] returns : []*multipart.FileHeader
  files := form.File["files"]

  // saving individual file
  for _, file := range files {
    dst := "./uploads/" + file.Filename
    c.SaveUploadedFile(file, dst)
  }
  ```

  - `multipart.Form`
  
  ```go
  package multipart
  type Form struct {
  	Value map[string][]string
  	File  map[string][]*FileHeader
  }
  ```

  - Full code

  ```go
  router.POST("/upload/multiple", func(c *gin.Context) {
    form, err := c.MultipartForm()
    if err != nil {
      c.JSON(http.StatusBadRequest, gin.H{"error": "Failed to parse form"})
      return
    }

    files := form.File["files"] // Extract multiple files

    for _, file := range files {
      dst := "./uploads/" + file.Filename
      c.SaveUploadedFile(file, dst)
    }

    c.JSON(
      http.StatusOK, 
      gin.H{
        "message": "Files uploaded successfully", 
        "file_count": len(files)
      }
    )
  })
  ```

### Sending Single file

- Send file to be rendered
  
```go
router.GET("/download/:filename", func(c *gin.Context) {
    filename := c.Param("filename")
    filepath := "./uploads/" + filename

    // Send the file as a response
    c.File(filepath)
})
```

- Send file as downloadable

```go
router.GET("/download/attachment/:filename", func(c *gin.Context) {
    filename := c.Param("filename")
    filepath := "./uploads/" + filename

    // Send file as a downloadable attachment
    c.FileAttachment(filepath, filename)
})
```

