# GoLang
- [GoLang](#golang)
  - [Basic Commands](#basic-commands)
  - [Basic files](#basic-files)
    - [`go.mod`](#gomod)
    - [`go.sum`](#gosum)
  - [Build and Run App](#build-and-run-app)
  - [Workspace in go](#workspace-in-go)
  - [To find](#to-find)


## Basic Commands

| command | use | 
|-|-|
| `go mod init <domain>/<project-name>` | Initialize project  | 
| `go get <dependency-link>` | Initialize project  |

> [!IMPORTANT] 
> ## Main Package
> - for the package which will be running it needs to be named as `main.go`
> ```go
> package main
> func main(){}
> ```

> [!TIP] 
> ## Functions
> - functions name starting with LowerCase char
>   - Cannot be accessed outside same package 
> - functions name starting with Upper char
>   - Can be accessed outside same package 

## Basic files 
### `go.mod`
- Defines the module properties and dependencies.
- Contains a require list that specifies the dependencies used in the project.
- The indirect label means that the dependency was not explicitly added by the user but was pulled in as a transitive dependency
    
    ```go
    require (
        github.com/gorilla/mux v1.8.1 // indirect
        github.com/inconshreveable/mousetrap v1.1.0 // indirect
        github.com/spf13/cobra v1.9.1 // indirect
        github.com/spf13/pflag v1.0.6 // indirect
    )
    ```

- If a dependency is added manually or updated, run the following command to verify and fix issues:
    - If Go detects inconsistencies, it will force a re-download of the correct version.

    ```
    go mod verify
    ```

- To remove unused dependencies and clean up go.mod and go.sum, run:

    ```
    go mod tidy
    ```

- Update dependency

    ```
    go get -u github.com/gorilla/mux
    ```

- List all dependencies

    ```
    go list -m all
    ```

### `go.sum`
- Acts as a dependency verification file.
- Contains cryptographic checksums to ensure that dependencies have not been tampered with.
- Each dependency has two checksum entries:
    - One for the package itself.
    - One for its go.mod file.

    ```
    github.com/gorilla/mux v1.8.1 h1:TuBL49tXwgrFYWhqrNgrUNEY92u81SPhu7sTdzQEiWY=
    github.com/gorilla/mux v1.8.1/go.mod h1:AKf9I4AEqPTmMytcMc0KkNouC66V3BtZ4qD5fmWSiMQ=
    ```

- Some dependencies have an entry with `/go.mod` at the end:
    - The first entry (without `/go.mod`) verifies the package contents.
    - The `/go.mod` entry verifies the module's go.mod file to detect changes in its dependencies.

- Some dependencies do not have a `/go.mod` entry because:
    - They do not have their own go.mod file (common in older Go modules).
    - They are indirect dependencies that were pulled in but never directly used


## Build and Run App
- Go to main folder and run

```
go build -o <app-name>
go build -o myApp
```

- To run app

```
/path/to/myApp
./myApp
```

## Workspace in go
- Say lib1 is main package and lib2, lib3 has other function then structure will look like 

```
go-workspace/
├── go.work
├── lib1
│   ├── go.mod
│   └── main.go
├── lib2
│   ├── go.mod
│   └── main.go
└── lib3
    ├── go.mod
    └── main.go
```

- When first time creating go.work use

```
go work init <pkg1-path> <pkg2-path>
go work init ./lib1 ./lib2
```

- To add other modules 

```
go work use <pkg1-path> <pkg2-path>
go work use ./lib1 ./lib2
```

## To find

```go
var myList = []int{}
// Why this way of appending, 
myList = append(myList, 10, 11, 12)
```

- Strings
  - How to split string, find substr, how to access char in string

