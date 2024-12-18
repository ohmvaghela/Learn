# Java Enterprise Edition

## Collections

<img src="./Java-Hierarchy.png" /> 

## JAR files
- `.jar` is a package format
- It is a collection of many Java class files and associated metadata and resources (text, images etc.)
- This is like a package in other languages which we import
- We need a tool to manage these, we cant manually download and place these JAR files in correct folder every time
- Hence tools like Maven are used for the same

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
