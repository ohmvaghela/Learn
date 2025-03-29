<h1> Database Connectivity using <code>gorm</code> </h1>

- [Creating DB connection instance](#creating-db-connection-instance)
  - [Components of DB connection function](#components-of-db-connection-function)
- [CRUD with SQL diver](#crud-with-sql-diver)
  - [Raw Query](#raw-query)
- [CRUD operations](#crud-operations)
  - [Using Raw Query](#using-raw-query)
  - [Create Operation](#create-operation)
  - [Read/find](#readfind)
  - [Update Operations](#update-operations)
- [Entity in golang](#entity-in-golang)
  - [First/ Find/ Scan with OrderBy/ Where/ GroupBy/ Limit/ Offset](#first-find-scan-with-orderby-where-groupby-limit-offset)


# Creating DB connection instance

- `grom.DB`

  ```go
  type DB struct {
  	*Config
  	Error        error
  	RowsAffected int64
  	Statement    *Statement
  	// contains filtered or unexported fields
  }
  ```

- Connection instance code

  ```go
  package main

  import (
  	"fmt"
  	"log"
  	"github.com/gin-gonic/gin"
  	"gorm.io/driver/mysql"
  	"gorm.io/gorm"
  )

  // Database instance
  var DB *gorm.DB

  // User struct represents a database model
  type User struct {
  	ID    uint   `gorm:"primaryKey"`
  	Name  string `gorm:"size:100"`
  	Email string `gorm:"unique"`
  }

  // connectDatabase initializes the database connection
  func connectDatabase(user, password, host, port, dbName string) {

  	dsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s?charset=utf8mb4&parseTime=True&loc=Local",
  		user, password, host, port, dbName,
  	)

  	var err error
  	DB, err = gorm.Open(mysql.Open(dsn), &gorm.Config{})
  	if err != nil {
  		log.Fatal("Failed to connect to database:", err)
  	}

  	fmt.Println("Connected to the database!")

  	// Auto-migrate the User model
  	DB.AutoMigrate(&User{})
  }

  func main() {
  	// Connect to database
  	connectDatabase("root","ohm123ohm","127.0.0.1","3306","testDB")

  	// Initialize Gin router
  	r := gin.Default()

  	// Define a simple GET endpoint
  	r.GET("/", func(c *gin.Context) {
  		c.JSON(200, gin.H{"message": "Hello, Gin + MySQL!"})
  	})

  	// Run the server on port 8080
  	r.Run(":8080")
  }
  ```

## Components of DB connection function
- `func Open(dialector Dialector, opts ...Option) (db *DB, err error)`
  - **Dialector** : Interface that defines the methods that a database driver must implement

    ```go
    package gorm
    type Dialector interface {
    	Name() string
    	Initialize(*DB) error
    	Migrator(db *DB) Migrator
    	DataTypeOf(*schema.Field) string
    	DefaultValueOf(*schema.Field) clause.Expression
    	BindVarTo(writer clause.Writer, stmt *Statement, v interface{})
    	QuoteTo(clause.Writer, string)
    	Explain(sql string, vars ...interface{}) string
    }
    ```
  
  - **Options**
    - Generally `gorm.Config` is provided
    - if empty `gorm.Config` is provided like `&gorm.Config{}` then default configs are used
    - Some use cases with custom Configs
      1. Database use snake_case, but we want camelCase

      ```go
      func connectDB() (*gorm.DB, error) {
        dsn := "user:password@tcp(127.0.0.1:3306)/dbname?charset=utf8mb4&parseTime=True&loc=Local"
        customNamingSchema := schema.NamingStrategy{
            TablePrefix:   "t_",   // Table names should start with `t_`
            SingularTable: true,   // Use singular table names, disable pluralization
            NameReplacer:  strings.NewReplacer("UUID", "UUID"), //avoid gorm change UUID to uuid
            Column: func(name string) string {
              return strings.ReplaceAll(name, "CamelCase", "snake_case")
            },
          }
        db, err := gorm.Open(mysql.Open(dsn), &gorm.Config{
          NamingStrategy: customNamingSchema,
        })
        return db, err
      }
      ```

      2.  You want to see the SQL queries that GORM is executing 

      ```go
      func connectDB() (*gorm.DB, error) {
        dsn := "user:password@tcp(127.0.0.1:3306)/dbname?charset=utf8mb4&parseTime=True&loc=Local"

        newLogger := logger.New(
          log.New(os.Stdout, "\r\n", log.LstdFlags), // io writer
          logger.Config{
            SlowThreshold:       time.Second,   // Slow SQL threshold
            LogLevel:    logger.Info, // Log level
            IgnoreRecordNotFoundError: true,    // Ignore ErrRecordNotFound error
            Colorful:    true,   // Disable color
          },
        )
        db, err := gorm.Open(mysql.Open(dsn), &gorm.Config{
          Logger: newLogger,
        })
        return db, err
      }
      ``` 
- `DB.AutoMigrate(&User{})`
  - automatically creates or updates the users table based on the User struct.
- To use manual update/ migration stratergy

  ```go
  DB.Exec("CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY, 
    name VARCHAR(100), 
    email VARCHAR(100) UNIQUE
  )")
  ```

# CRUD with SQL diver
- Base

```go
var DB *gorm.DB

type User struct {
	ID    uint   `gorm:"primaryKey"`
	Name  string `gorm:"size:100"`
	Email string `gorm:"unique"`
}

func connectDatabase(user, password, host, port, dbName string) {...}
```

## Raw Query

```go
func main(){
  connectDatabase("root","ohm123ohm","127.0.0.1","3306","temp")

  // get sql dirver
  sqlDB, err := DB.DB()
  if err != nil { log.Fatal(err) }

  // query
  query := "SELECT name, age FROM users WHERE id = ?"
  row := sqlDB.QueryRow(query, 1) // id of Alice is 1
  err = row.Scan(&name, &age)
  
  if err != nil { /*print error */} 
  else { /*print users */ }

}
```

# CRUD operations

- Base

```go
var DB *gorm.DB

type User struct {
	ID    uint   `gorm:"primaryKey"`
	Name  string `gorm:"size:100"`
	Email string `gorm:"unique"`
}

func connectDatabase(user, password, host, port, dbName string) {...}
```

## Using Raw Query

```go
func main(){
  connectDatabase("root","ohm123ohm","127.0.0.1","3306","temp")
  var users []User

  // db.Raw("SELECT * FROM users") : Returns new db which has results
  // Scan(&users) : Scans rows from DB and adds them to user
  result := DB.Raw("SELECT * FROM users").Scan(&users)

  if result.Error != nil { /*print error */} 
  else { /*print users */ }
}
```

## Create Operation

```go
func main(){
	connectDatabase("root","ohm123ohm","127.0.0.1","3306","temp")
	// 1st method
  var user User = User{1,"ohm", "xyz@gmail.com"}
	result := DB.Create(&user)

  // 2nd method
  result := DB.Create(&User{Name: "Alice", Age: 30})

  // log resutls
  if result.Error != nil { /*print error */} 
  else { /*print users */ }
}
```

## Read/find

```go
func main(){
	connectDatabase("root","ohm123ohm","127.0.0.1","3306","temp")
  
  // get all users
  var users []User
  result := DB.Find(&users)
  // log resutls
  if result.Error != nil { /*print error */} 
  else { /*print users */ }

  // 

}
```

## Update Operations

```go
func main(){
  connectDatabase("root","ohm123ohm","127.0.0.1","3306","temp")
	var user User = User{1,"ohm", "xyz@gmail.com"}
	DB.Create(&user)
}
```

# Entity in golang

```go
type User struct {
	ID    uint   `gorm:"primaryKey"`
	Name  string `gorm:"size:100"`
	Email string `gorm:"unique"`
}
```

## First/ Find/ Scan with OrderBy/ Where/ GroupBy/ Limit/ Offset