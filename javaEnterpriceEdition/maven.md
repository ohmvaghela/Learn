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

- Maven repo is a java packge repo for each individual project like a github repo
- It has a file called pom.xml (Project Object Model) which contains metadata and info about the dependencies
- Dependencies are searched in first local repo then central repo then remote repo
- Components of `pom.xml` files
  - Project coordinates
    - GroupID : Group or org. project belongs to 
    - ArtifactID : unique identifier for each project
    - version
  - Model version : Version of maven POM
  ```xml
  <project
      xmlns="http://maven.apache.org/POM/4.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-app</artifactId>
    <version>1.0.0</version>

    --- other elements ---
  
  </project>
  ```

  - Build config
    - packaging : JAR, ZIP, WAR...
    - build : Contains settings like location of soruce, test, target dir etc.
  - Plugins : List of plugin to be used
    - Plugins are executed as part of the build lifecycle
    - Plugin : Individual plugin element with groupId, artifactId, and version
  ```xml
  <packaging>jar</packaging>
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

    </plugins>

  </build>
  ```
  - Dependencies
    - Dependency : Individual dependency elements specifying groupId, artifactId, and version.
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
    
  > - Dependencies provide libraries your project needs to function, while plugins define tasks to be executed during the build process
  > - Maven handles the downloading and inclusion of dependencies automatically, whereas plugins require explicit configuration and goals to run.

  - Repositories : A list of repositories where dependencies can be found.
    - Repository : Individual repository elements specifying id, url, and layout
  ```xml
  <repositories>
    <repository>
      <id>central</id>
      <url>https://repo.maven.apache.org/maven2</url>
    </repository>
  </repositories>
  ```

  - Profiles : Allows to customise the `build process` for specific users
    - Like tester may need other plugins as compared to dev
    - Activation : Activated when a specific Maven property is set
      - Like environment is set to dev
    - To set profile use
      ```
      --- develpment profile
      mvn clean install -Pdevelopment
      ```
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
  - Properties : Used to define custom properties like
    - Version management
    - Directory path
    - Custom Values
  ```
  <properties>
      <maven.compiler.source>1.8</maven.compiler.source>
      <maven.compiler.target>1.8</maven.compiler.target>
      <project.build.directory>${basedir}/target</project.build.directory>
      <spring.version>2.3.4.RELEASE</spring.version>
  </properties>
  ```
 - Project info 
 - Organisation and Developer : Info about developer and orgainisation
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
- Continous integration and Issues tool
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
