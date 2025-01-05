## JAR files
.jar is a package format
It is a collection of many Java class files and associated metadata and resources (text, images etc.)
This is like a package in other languages which we import
We need a tool to manage these, we cant manually download and place these JAR files in correct folder every time
Hence tools like Maven are used for the same
## Maven
### Uses of Maven
- Adding set of Jars in each project
- Creating the right project structure
- Building and Deploying the project

  <hr>

## 3 Parts of pom.xml file
- Maven repo is a java packge repo for each individual project like a github repo
- It has a file called pom.xml (Project Object Model) which contains metadata and info about the dependencies
- Dependencies are searched in first local repo then central repo then remote repo
  1. Project details sections
      - Model version : Version of maven POM
      - GroupID : Group or org. project belongs to 
      - ArtifactID : unique identifier for each project
      - version : project version
      - packaging : JAR, ZIP, WAR...
      - name : Name of the project 
      - url : url of the project
      <details>
      <summary>xml code</summary>

        ```xml
        <project
            xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        
          <modelVersion>4.0.0</modelVersion>

          <groupId>com.example</groupId>
          <artifactId>my-app</artifactId>
          <version>1.0.0</version>
          <packaging>jar</packaging>

          --- other elements ---
        
        </project>
        ```
        </details>
  2. Build 
      - build : Contains settings like location of soruce, test, target dir etc.
      - Plugins : List of plugin to be used
        - ### Plugins are executed as part of the build lifecycle
        - Most commonly used plugins
          - Maven Compiler Plugin: Compiles Java source code.
          - Maven Assembly Plugin: Packages your project into a runnable JAR or other formats.
          - Maven Surefire Plugin: Runs unit tests during the build.
          - Maven Exec Plugin: Runs Java programs from the command line using Maven.
        <details>
        <summary>xml code</summary>

          ```xml

          <build>
          
            <sourceDirectory>src/main/java</sourceDirectory>
            <testSourceDirectory>src/test/java</testSourceDirectory>
            <outputDirectory>target</outputDirectory>

            <plugins>

              <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                  <source>1.8</source>
                  <target>1.8</target>
                </configuration>
              </plugin>
              <!-- more plugins -->

            </plugins>

          </build>
          ```
        </details>  

  3. dependencies
      - Libraries and framework that your project needs to run and compile
      - Example : UJnit, Spring boot, MySQL connector
      - Dependency : Individual dependency elements specifying groupId, artifactId, and version.
        <details>
        <summary>xml code</summary>

          ```xml

            <dependencies>
            
              <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
                <version>2.3.4.RELEASE</version>
              </dependency>

              <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>4.13.1</version>
                <scope>test</scope>
              </dependency>

            </dependencies>

          ```
        </details>
      > - Maven handles the downloading and inclusion of dependencies automatically, whereas plugins require explicit configuration and goals to run.

  4. Other
      ### + Repositories 
        - A list of repositories where dependencies can be found.
        - Repository : Individual repository elements specifying id, url, and layout
          <details>
          <summary>xml code</summary>

            ```xml
            <repositories>
              <repository>
                <id>central</id>
                <url>https://repo.maven.apache.org/maven2</url>
              </repository>
            </repositories>
            ```
          </details>

      ### + Profiles 
        - Allows to customise the `build process` for specific users
        - Like tester may need other plugins as compared to dev
        - Activation : Activated when a specific Maven property is set
          - Like environment is set to dev
        - To set profile use
          ```
          --- develpment profile
          mvn clean install -Pdevelopment
          ```
          <details>
          <summary>xml code</summary>

            ```xml
            <profiles>
              <!-- Development Profile -->
              <profile>
                  <id>development</id>
                  <activation>
                      <property>
                          <name>env</name>
                          <value>dev</value>
                      </property>
                  </activation>
                  <build>
                      <plugins>
                          <plugin>
                              <groupId>org.apache.maven.plugins</groupId>
                              <artifactId>maven-surefire-plugin</artifactId>
                              <version>2.22.1</version>
                          </plugin>
                      </plugins>
                  </build>
              </profile>

              <!-- Production Profile -->
              <profile>
                  <id>production</id>
                  <activation>
                      <property>
                          <name>env</name>
                          <value>prod</value>
                      </property>
                  </activation>
                  <build>
                      <plugins>
                          <plugin>
                              <groupId>org.apache.maven.plugins</groupId>
                              <artifactId>maven-jar-plugin</artifactId>
                              <version>3.1.0</version>
                          </plugin>
                      </plugins>
                  </build>
              </profile>
            </profiles>
            
            ```
          </details>

      ### + Properties : Used to define custom properties like
        - Version management
        - Directory path
        - Custom Values

        <details>
        <summary>xml code</summary>

          ```xml
          <properties>
              <maven.compiler.source>1.8</maven.compiler.source>
              <maven.compiler.target>1.8</maven.compiler.target>
              <project.build.directory>${basedir}/target</project.build.directory>
              <spring.version>2.3.4.RELEASE</spring.version>
          </properties>
          ```
        </details>

      ### + Project info | Organisation | Developer 
        - Info about developer and orgainisation

          <details>
          <summary>xml code</summary>

            ```xml
            <!-- More Project Information -->
            <name>My Application</name>
            <description>This is a sample Maven project.</description>
            <url>https://www.example.com</url>
            
            <!-- Organization and Developers -->
            <organization>
              <name>Example Inc.</name>
              <url>https://www.example.com</url>
            </organization>
            <developers>
              <developer>
                <id>johndoe</id>
                <name>John Doe</name>
                <email>johndoe@example.com</email>
                <organization>Example Inc.</organization>
                <organizationUrl>https://www.example.com</organizationUrl>
              </developer>
            </developers>

            ```
          
          </details>

      ### + Continous integration and Issues tool
        - CI/CD
          <details>
            <summary>xml code</summary>

            ```xml

            <!-- Continuous Integration and Issue Management -->
            <ciManagement>
              <system>Jenkins</system>
              <url>https://ci.example.com</url>
            </ciManagement>
            <issueManagement>
              <system>JIRA</system>
              <url>https://issues.example.com</url>
            </issueManagement> 
            
            ```

          </details>

## Creating project using `maven`

```bash
mvn archetype:generate \
  -DgroupId=com.example \
  -DartifactId=helloworld \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false
```
- Parts of code
  - archtype:generate 
    - Tell maven to run Archetype Plugin and generate a new project.
  - DgroupId=com.example
    - The Group ID acts as a namespace to uniquely identify your project
    - Create a chained child dirs 
    - Like for the given code it create `com` dir and then `example` as its sub dir
    - and this `com` is under src/main/java
    - And main `App.java` is at `src/main/java/com/example` 
  - DartifactId=helloworld
    - Name of the project/module
  - DarchetypeArtifactId=maven-archetype-quickstart
    - archetypeArtifactId : Boiler plate for the project
    - `quickstart` is used for basic java project
    - It generates a basic project structure with:
      - A `src/main/java` directory for your application code.
      - A `src/test/java` directory for unit test code.
      - A basic `App.java` file in `src/main/java`.
      - A corresponding `AppTest.java` file in `src/test/java`.
      - A `pom.xml` file configured with minimal settings.
  - DinteractiveMode=false
    - Assumes default for many things like 
      - version name : default to  `1.0-SNAPSHOT`
      - GroupID 

## Compiling project for first time
`mvn compile`
## Updating pom.xml then updating file
`mvn clean compile`
## Run test
- Test can be run without packaging
`mvn test`
## Create jar file 
`mvn package`

## Maven Plugin : Assembly and Compiler plugins
- Compiler plugin
  - Compiles code into `.class` files
  - `Does not` bundle `.class` files to `jar` files
  - `Does not` include dependencies 
  - `Does not` create jar execuatable
  - Can be used for junit testing standalone
- Assembly plugin 
  - Create bundled JAR file
  - This includes `.class` files, dependencies etc...
  - A.K.A `fat jar`
  - It adds a `MANIFEST.MF` file to the JAR, specifying the Main-Class
  - To run it (Create package out of it first)
    ``` 
      java -jar target/your-app-jar-with-dependencies.jar
    ```
  - Other alternate include Shade, Spring boot

  
