<h1> Database Connectivity using <code>gorm</code> </h1>

- [Creating DB connection instance](#creating-db-connection-instance)
  - [Components of DB connection function](#components-of-db-connection-function)
- [CRUD with SQL diver](#crud-with-sql-diver)
  - [Raw Query](#raw-query)
- [CRUD operations](#crud-operations)
  - [Using Raw Query](#using-raw-query)
  - [Create Operation](#create-operation)
  - [Update Operations](#update-operations)
- [Entity in golang](#entity-in-golang)
- [SQL queries](#sql-queries)
  - [Grom Model](#grom-model)
  - [Selecting table to be used](#selecting-table-to-be-used)
    - [Model](#model)
    - [Table](#table)
  - [Find](#find)
  - [Take](#take)
  - [First](#first)
  - [Scan](#scan)
  - [Select](#select)
  - [Query Modifiers](#query-modifiers)
    - [OrderBy](#orderby)
    - [Where](#where)
    - [GroupBy + Having](#groupby--having)
    - [Limit and Offset](#limit-and-offset)
    - [Count](#count)
    - [Distinct](#distinct)
    - [Attrs](#attrs)
    - [FirstOrCreate](#firstorcreate)
  - [Query Modifier usage mapping](#query-modifier-usage-mapping)


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
            // Table names should start with `t_`
            TablePrefix:   "t_",   
            // Use singular table names, disable pluralization
            SingularTable: true,   
            //avoid gorm change UUID to uuid
            NameReplacer:  strings.NewReplacer("UUID", "UUID"), 
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
          // io writer
          log.New(os.Stdout, "\r\n", log.LstdFlags), 
          logger.Config{
            // Slow SQL threshold
            SlowThreshold:       time.Second,   
            // Log level
            LogLevel:    logger.Info, 
            // Ignore ErrRecordNotFound error
            IgnoreRecordNotFoundError: true,    
            // Disable color
            Colorful:    true,   
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

# SQL queries
- For fetching data following can be use
  - First : Order record by PK and fetch first
  - Take : fetch first record
  - Find : Fetch all records matching condition
    - Only works with **Model** or **slice of model**
    - Does not work with custom structs or queries
  - Scan : Fetch all records matching condition
    - Works with **Model** or **slice of model**
    - Works with raw queries and custom struct mappings
- These generally fill the interface or struct provided

## Grom Model
- In GORM, a model is just a Go struct that represents a table in the database. 
- GORM automatically maps the struct fields to table columns based on their names and tags.

  ```go
  type User struct {
      ID    uint   `gorm:"primaryKey"`
      Name  string `gorm:"size:100"`
      Email string `gorm:"unique"`
  }
  ```

## Selecting table to be used

### Model
- Takes model struct as input

  ```go
  func (db *DB) Model(value interface{}) (tx *DB)
  db.Model(&User{})
  ```

### Table
- Takes string as input

  ```go
  func (db *DB) Table(name string, args ...interface{}) (tx *DB)
  db.Table("users")
  ```

## Find 
- Syntax
  
  ```go
  func (db *DB) Find(dest interface{}, conds ...interface{}) (tx *DB)
  ```

- Use
  
  ```go
  var users []User
  err := db.Find(&users).Error
  err := db.Find(&users, "age > ?", 25).Error // SELECT * FROM users WHERE age > 25;

  // If you are sure only one entry will be returned
  var user User
  err := db.Find(&user).Error
  err := db.Find(&user, "age > ?", 25).Error // SELECT * FROM users WHERE age > 25;
  ```

- Other options for condition

  ```go
  // direct PK/id
  err := db.Find(&users, 1).Error // SELECT * FROM users WHERE id = 1;

  // Condition : SELECT * FROM USERS WHERE Name = "John"
  err := db.Find(&users, "name = ?", "John").Error  

  // Struct :  SELECT * FROM USERS WHERE Name = "John"
  err := db.Find(&users, User{Name: "John"}).Error

  // Map : SELECT * FROM USERS WHERE Name = "John"
  err := db.Find(&users, map[string]interface{}{"name": "John"}).Error
  ```

## Take
- Syntax

  ```go
  func (db *DB) Take(dest interface{}, conds ...interface{}) (tx *DB)
  ```

- Use

  ```go
  var user User
  err := db.Take(&user).Error // SELECT * FROM users LIMIT 1;
  ```

## First

- Syntax

  ```go
  func (db *DB) Take(dest interface{}, conds ...interface{}) (tx *DB)
  ```

- Use

  ```go
  var user User
  err := db.First(&user).Error // SELECT * FROM users ORDER BY id LIMIT 1;
  ```

## Scan 
- Syntax

  ```go
  func (db *DB) Scan(dest interface{}) (tx *DB)
  ```

- Use

  ```go
  type Result struct {
    Name string
    Age  int
  }
  var result Result

  // query : SELECT name, age from users where id = 1
  err := db.Table("users").Select("name, age").Where("id = ?", 1).Scan(&result).Error
  ```

> [!NOTE]
> - Mostly used for Map query result into custom struct

> [!CAUTION]
> - `Result` is not grom model so we cannot use `Find(&result)` over here

## Select
- Use to filter columns

```go
func (db *DB) Select(query interface{}, args ...interface{}) (tx *DB)
db.Table("users").Select("name, age").Where("id = ?", 1).Scan(&result)
```


## Query Modifiers

### OrderBy
- Syntax

  ```go
  func (db *DB) Order(value interface{}) (tx *DB)
  ```  

- Use

  ```go
  db.Order("age desc").Find(&users)
  ```

### Where
- Syntax
  
  ```go
  func (db *DB) Where(query interface{}, args ...interface{}) (tx *DB)
  func (db *DB) Order(value interface{}) (tx *DB)
  ```

- Use

  ```go
  // 1
  db.Where("name = ?", "John").Find(&users)
  // 2
  db.Where([]map[string]interface{}{
    {"name": "John", "age": 23},
    {"name": "Ram", "age": 24},
  }).Find(&users)
  ```

### GroupBy + Having
- Syntax
  
  ```go
  func (db *DB) Having(query interface{}, args ...interface{}) (tx *DB)

  ```

- Use

  ```go
  db.Model(&User{}).Select("age, count(*) as total").Group("age").Having("count(*) > ?", 5).Find(&results)
  ```

### Limit and Offset
- Syntax
  

- Use

  ```go
  db.Limit(10).Offset(20).Find(&users)
  ```

### Count
- Syntax
  
  ```go
  func (db *DB) Limit(limit int) (tx *DB)
  func (db *DB) Offset(offset int) (tx *DB)
  ```

- Use

  ```go
  var count int64
  db.Model(&User{}).Where("age > ?", 25).Count(&count)
  ```

### Distinct
- Syntax

  ```go  
  func (db *DB) Distinct(args ...interface{}) (tx *DB)
  ```

- Use

  ```go
  db.Distinct("age").Find(&users)
  ```

### Attrs
- If value of column mentioned in null then it is replaced with default value

- Syntax
  
  ```go
  func (db *DB) Attrs(attrs ...interface{}) (tx *DB)
  ```

- Use

  ```go
  db.Where(User{Name: "John"}).Attrs(User{Age: 30}).FirstOrCreate(&user)
  ```

### FirstOrCreate
- Checks if a user with Name = "John" exists.
  - If yes, it returns that user (without modifying anything).
  - If no, it creates a new user with Name: "John", ignoring "Ohm".

  ```go
  db.Where(User{Name: "John"}).Attrs(User{Name: "Ohm"}).FirstOrCreate(&user)

  ```

## Query Modifier usage mapping

| Function | 	Use Case | Works With |
|-|-|-|
| Model | Used when working with a GORM model	| `.Where(),` `.Count(),` `.Find(),` `.Select()` |
| Table | Used when working with raw tables instead of GORM models | `.Scan(),` `.Select(),` `.Where()` |
| Find | Fetch multiple rows | `.Where(),` `.Order(),` `.Group(),` `.Limit()` |
| First | Fetch the first row (ordered by primary key) | `.Where(),` `.Order()` |
| Count | Count records | `.Where(),` `.Group(),` `.Having()` |
| Distinct | Fetch unique values | `.Select(),` `.Find()` |
| Attrs | Set default values if the record doesn't exist | `.FirstOrCreate()` |
