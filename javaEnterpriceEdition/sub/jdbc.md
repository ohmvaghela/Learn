## JDBC (Java Database Connectivity)

## 7 Steps in JDBC
- Import packages
- Register Driver
- Establish Connection 
- Create Query
- Execute Query
- Get results
- Close Connection

## Database driver
- Library that is implementation of `java.sql.Driver` interface
- Translate high level java code to database specific protocal
## Driver manager
- Intermediate btwn application and database driver
- Keeps track of all the database drivers loaded during runtime
- Role
  - Responsible for registeration of driver
  - Provides a connection with `getConnection` method
## JDBC url format
```java
Format: "jdbc:<db_type>://<host>:<port>/<database_name>"
Example: "jdbc:mysql://localhost:3306/temp"
```
- Code sequence
  1. Load database driver
      - `Class.forName("com.mysql.cj.jdbc.Driver");` (optional for newer versions).
  2. Establish connection
      - `DriverManager.getConnection(url, username, password);`
  3. Execute SQL Queries
      - Use a `Statement` or `PreparedStatement` object to execute queries.
  4. Process Results
      - Retrieve data using `ResultSet`
  5. Close Resources
## Working code
```java
package mechsimvault;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        String url="jdbc:mysql://localhost:3306/temp";
        String u_name = "root";
        String password = "ohm123ohm";

        try (            
            Connection con = DriverManager.getConnection(url, u_name, password);
        ) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from students");
            while(rs.next()){
                String sname = rs.getString("sname"); 
                int marks = rs.getInt("marks");
                System.out.println(sname+" - "+marks);
            }

        }catch(SQLException e){
            // System.out.println(e);
            e.printStackTrace();

        }
    }
}
```

## DML
### Read
- For this we use executeQuery
- This returns a set as an output which can be iterated using `next` method
  ```java
  String query = "select * from students";
  Statement st = con.createStatement();
  ResultSet rs = st.executeQuery(query);
  ```
### Update
- For updating data we use executeUpdate 
- and it will result in int which will be how many rows are affected
  ```java
  String query = "insert into students values ('"+name+"',"+marks+")";
  Statement st = con.createStatement();
  int rs = st.executeUpdate(query);
  ```
### PrepareStatement
- Alternate method used for updating table
  ```java
  String query = "insert into students value (?,?)";
  PrepareStatement st = con.prepareStatement(query);
  
  String name = "ohm";
  int marks = 89;
  
  st.setString(name);
  st.setInt(marks);
  
  int rs = st.executeUpdate(query);
  ```
### PrepareStatment v/s CreateStatement
- CreateStatement is used for simple query 
  - PrepareStatement is used for complex query
- CreateStatement is vulnerable to SQL injections
  - Preparestatement ensures safety from SQL injection
- PrepareStatement is used when there is a need for repeated use of same statement

| Feature             | Statement                            | PreparedStatement                  |
|---------------------|--------------------------------------|-------------------------------------|
| **Query Type**      | Static queries                      | Dynamic/Parameterized queries      |
| **Parameter Support**| No                                  | Yes                                |
| **Security**         | Vulnerable to SQL injection         | Secure against SQL injection       |
| **Performance**      | Slower for repeated queries         | Faster for repeated queries        |
| **Ease of Use**      | Simpler for one-off, static queries | More complex for dynamic queries   |
| **Precompilation**   | Not precompiled                     | Precompiled for better performance |
