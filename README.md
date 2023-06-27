# R.E.S.T API

## Description

This is a simple REST API that allows you to create, read, update and delete;

## Technologies used:

- JDK 17 (Java 17)
- Hibernate (JPA Provider)
- Javalin (Web Framework)
- PostgreSQL (Database)
- Maven (Dependency Management)
- Docker (Containerization)
- Docker Compose (Container Orchestration)
- JUnit (Unit Testing)
- Mockito (Mocking Framework)
- Log4j (Logging Framework)
- Testcontainers (Integration Testing)
- Rest Assured (API Testing)

## Step 1: Setup the project

- new project -> maven -> java 17 -> artifactID and groupID -> finish

### pom.xml - Project Object Model (properties, dependencies and build)

- add properties to pom.xml (inside <project> tag):

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <javalin.version>5.5.0</javalin.version>
    <jackson.version>2.15.0</jackson.version>
    <hibernate.version>6.2.4.Final</hibernate.version>
    <postgresql.version>42.6.0</postgresql.version>
    <junit.version>5.9.1</junit.version>
    <hamcrest.version>2.0.0.0</hamcrest.version>
    <restassured.version>5.3.0</restassured.version>
    <testcontainers.version>1.18.0</testcontainers.version>
    <logging.version>2.14.1</logging.version>
    <lombok.version>1.18.20</lombok.version>
    <jbcrypt.version>0.4</jbcrypt.version>
    <jwt.version>9.0.1</jwt.version>

    <!-- DATABASE PROJECT NAME ON SERVER  -->
    <db.name>projectdb</db.name>
    <javalin.port>7070</javalin.port>
</properties>
```

- add dependencies below *properties* to pom.xml:

```xml
<dependencies>
</dependencies>
```

- add build section below *dependencies* to pom.xml:

```xml
<build>
        <finalName>app</finalName> <!-- This is the name of the jar file -->
        <plugins>
        </plugins>
</build>
```

### Dependencies

- Inside *dependencies* add **Javalin** for restful api (Study: https://javalin.io/documentation#getting-started)

```xml
<dependency>
    <groupId>io.javalin</groupId>
    <artifactId>javalin-bundle</artifactId>
    <version>${javalin.version}</version>
</dependency>
```

- add **Jackson** for serialization/deserialization of JSON (Study:https://www.baeldung.com/jackson):

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>${jackson.version}</version>
</dependency>
```

- add **Hibernate** for JPA (
  Study:https://www.theserverside.com/blog/Coffee-Talk-Java-News-Stories-and-Opinions/The-JPA-and-Hibernate-CRUD-operations-example):

```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>${hibernate.version}</version>
</dependency>
```

- add **PostgreSQL** for database (Study:https://www.postgresqltutorial.com/postgresql-getting-started/):

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>${postgresql.version}</version>
</dependency>
```

- add security package: **JBCrypt** for password hashing (Study:https://www.baeldung.com/java-password-hashing):

```xml
<!--        https://www.mindrot.org/projects/jBCrypt/ for Hashing passwords-->
<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>${jbcrypt.version}</version>
</dependency>
```

- add security package: **JWT** for authentication
```xml
<!--  https://nimbusds.com/products/nimbus-jose-jwt   -->
<dependency>
    <groupId>com.nimbusds</groupId>
    <artifactId>nimbus-jose-jwt</artifactId>
    <version>${jwt.version}</version>
</dependency>
```
  
- add **jUnit** for unit testing (Study:https://www.baeldung.com/junit-5  ):
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>${junit.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>${junit.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>java-hamcrest</artifactId>
    <version>${hamcrest.version}</version>
    <scope>test</scope>
</dependency>
```

- add **Rest Assured** for API testing (Study:https://www.baeldung.com/rest-assured-tutorial):

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>${restassured.version}</version>
    <scope>test</scope>
</dependency>
```

- OPTIONAL: add **Rest Assured JSON schema validation** for API testing (
  Study: https://medium.com/@iamfaisalkhatri/how-to-perform-json-schema-validation-using-rest-assured-64c3b6616a91):

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>${restassured.version}</version>
    <scope>test</scope>
</dependency>
```

- add **Testcontainers** for integration testing (Study:https://java.testcontainers.org/quickstart/junit_5_quickstart/):

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>jdbc</artifactId>
    <version>${testcontainers.version}</version>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>
```

- OPTIONAL add **Mockito** for mocking in test (https://www.baeldung.com/mockito-annotations):

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.3.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>4.6.1</version>
    <scope>test</scope>
</dependency>
```

- OPTIONAL add **Log4j** for logging (https://www.baeldung.com/log4j2-xml):

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.7</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-core</artifactId>
    <version>1.4.7</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.7</version>
</dependency>
```

- OPTIONAL add **Lombok** for less boilerplate code (https://www.baeldung.com/intro-to-project-lombok):

```xml
<dependency>
  <!--   lombok        -->
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <version>${lombok.version}</version>
  <scope>provided</scope>
</dependency>
```

### Plugins

- add **Maven Properties** Plugin to pom.xml inside the build.plugins section (
  Study:https://www.baeldung.com/java-accessing-maven-properties)

```xml
<plugin>
    <!--  https://www.baeldung.com/java-accessing-maven-properties  -->
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>properties-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <phase>generate-resources</phase>
            <goals>
                <goal>write-project-properties</goal>
            </goals>
            <configuration>
                <outputFile>${project.build.outputDirectory}/properties-from-pom.properties</outputFile>
            </configuration>
        </execution>
    </executions>
</plugin>
```

- add **Surefire Plugin** for automatic testing to pom.xml inside the build.plugins section (
  Study:https://www.baeldung.com/maven-surefire-report-plugin). By default, surefire automatically includes all test
  classes whose name starts with Test, or ends with Test, Tests or TestCase.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0</version>
</plugin>
```

- add **Maven Shade Plugin** for creating a fat jar to pom.xml inside the build.plugins section (
  Study:https://www.baeldung.com/executable-jar-with-maven)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.4.1</version>
    <configuration>
        <transformers>
            <transformer
                    implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                <mainClass>dk/lyngby/Main
                </mainClass> <!-- Here you should put the main class of your application -->
            </transformer>
        </transformers>
        <filters>
            <filter> <!-- This filter is needed to avoid a bug in the shade plugin -->
                <artifact>*:*</artifact>
                <excludes>
                    <exclude>module-info.class</exclude>
                    <exclude>META-INF/*.SF</exclude>
                    <exclude>META-INF/*.DSA</exclude>
                    <exclude>META-INF/*.RSA</exclude>
                </excludes>
            </filter>
        </filters>
        <relocations>
            <relocation>
                <pattern>com.example</pattern>
                <shadedPattern>my.project.com.example</shadedPattern>
            </relocation>
        </relocations>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

- In the maven shade plugin remember to change the main class to the main class of your application
- Now remember to click the *load maven changes* button in the top right corner of the maven tab in IntelliJ

## Step 2: Add a web server

- Open your Main class file and change the main method to the following code:

```java
public static void main(String[] args) {
    Javalin app = Javalin.create().start(7070);
    app.get("/api/demo", ctx -> {
        ctx.result("Hello World");
    });
}
```

- add this import: `import io.javalin.Javalin; `
- now start the application and go to http://localhost:7070/api/demo in your browser
    - you should see the text "Hello World" in your browser

## Step 3: Add a database

### Entities

- create 2 new files in a package: entities:
    - Employee.java
    - Department.java

```java
package entities;

import dtos.EmployeeDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

    public Employee(EmployeeDTO employeeDTO) {
        this.id = employeeDTO.getId();
        this.firstName = employeeDTO.getFirstName();
        this.lastName = employeeDTO.getLastName();
        this.email = employeeDTO.getEmail();
    }
}
``` 

and

```java
package entities;

import dtos.DepartmentDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    private String departmentName;
    private String departmentCode;
    private String departmentDescription;

    @OneToMany(mappedBy = "department")
    private java.util.Set<Employee> employees;

    public Department(DepartmentDTO departmentDTO) {
        this.id = departmentDTO.getId();
        this.departmentName = departmentDTO.getDepartmentName();
        this.departmentCode = departmentDTO.getDepartmentCode();
        this.departmentDescription = departmentDTO.getDepartmentDescription();
        if(departmentDTO.getEmployeeDTOs() != null) {
            this.employees = departmentDTO.getEmployeeDTOs().stream().map(Employee::new).collect(java.util.stream.Collectors.toSet());
        }
    }

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
    }
    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
    }
}
```

### DTOs

- create 2 new files in a package: dtos:
    - EmployeeDTO.java
    - DepartmentDTO.java

```java
package dtos;

import entities.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String departmentName;
    public EmployeeDTO(Employee e){
        this.id = e.getId();
        this.firstName = e.getFirstName();
        this.lastName = e.getLastName();
        this.email = e.getEmail();
        if(e.getDepartment() != null) {
            this.departmentName = e.getDepartment().getDepartmentName();
        }
    }
    public EmployeeDTO(String firstName, String lastName, String email, String departmentName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.departmentName = departmentName;
    }
    public Set<EmployeeDTO> getEmployeeDTOs(Set<Employee> employees) {
        Set<EmployeeDTO> employeeDTOS = employees.stream().map(EmployeeDTO::new).collect(java.util.stream.Collectors.toSet());
        return employeeDTOS;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}
```

and

```java
package dtos;

import entities.Department;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DepartmentDTO {
    private Long id;
    private String departmentName;
    private String departmentCode;
    private String departmentDescription;
    private Set<EmployeeDTO> employeeDTOs;

    public DepartmentDTO(Department d) {
        this.id = d.getId();
        this.departmentName = d.getDepartmentName();
        this.departmentCode = d.getDepartmentCode();
        this.departmentDescription = d.getDepartmentDescription();
        if(d.getEmployees() != null) {
            this.employeeDTOs = d.getEmployees().stream().map(EmployeeDTO::new).collect(java.util.stream.Collectors.toSet());
        }
    }
    public DepartmentDTO(String departmentName, String departmentCode, String departmentDescription) {
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.departmentDescription = departmentDescription;
    }
    public Set<DepartmentDTO> getDepartmentDTOs(Set<Department> departments) {
        return departments.stream().map(DepartmentDTO::new).collect(java.util.stream.Collectors.toSet());
    }
}
```

### DAOs

- create 1 new file in a package: daos:
    - EmployeeDAO.java

```java
public interface IDAO<T> {
  T create(T t) throws Exception;
  T getById(String fileName) throws EntityNotFoundException;
  List<T> getAll();
  T update(T t) throws EntityNotFoundException;
  T delete(String id) throws EntityNotFoundException;
  List<T> findByProperty(String property, String propValue) throws EntityNotFoundException;

}
```

- create 2 new files in a package: daos, that implements the IDAO interface:
    - EmployeeDAO.java
    - DepartmentDAO.java
- create a new file in a package: exceptions:
    - EntityNotFoundException.java

### Setup database connection and JPA configuration

- create a new file in a package: utils:
    - HibernateConfig.java

```java
package dk.cphbusiness.config;

import dk.cphbusiness.entities.Department;
import dk.cphbusiness.entities.Employee;
import dk.cphbusiness.utils.Utils;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.management.relation.Role;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HibernateConfig {
  private static EntityManagerFactory emf;
  private static EntityManagerFactory emfTest;
  public static EntityManagerFactory getEntityManagerFactory() {
    if (emf == null) {
      emf = getEMF();
    }
    return emf;
  }
  public static EntityManagerFactory getEntityManagerFactoryForTest() {
    if (emfTest == null) {
      emfTest = getEMFforTest();
    }
    return emfTest;
  }

  private static EntityManagerFactory getEMF() {
    try {
      Configuration configuration = new Configuration();
      Properties props = new Properties();
      boolean isDeployed = (System.getenv("DEPLOYED") != null);

      if(isDeployed) {
        String DB_PASSWORD = System.getenv("DB_PASSWORD");
        props.setProperty("hibernate.connection.url", System.getenv("CONNECTION_STR") + getDBName());
        props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
      } else {
        props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/"+ getDBName());
        props.put("hibernate.connection.username", "dev");
        props.put("hibernate.connection.password", "ax2");
      }
      props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
      props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
      // Are these needed?
      props.put("hibernate.archive.autodetection", "class");
      props.put("hibernate.current_session_context_class", "thread");
      props.put("hibernate.show_sql", "true");
      props.put("hibernate.hbm2ddl.auto", "update");

      props.put("hibernate.format_sql", "true");
      props.put("hibernate.use_sql_comments", "true");

      configuration.setProperties(props);

      getAnnotationConfiguration(configuration);

      ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
      System.out.println("Hibernate Java Config serviceRegistry created");
      SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
      EntityManagerFactory emf = sf.unwrap(EntityManagerFactory.class);
      return emf;
    } catch (Throwable ex) {
      System.err.println("Initial SessionFactory creation failed." + ex);
      throw new ExceptionInInitializerError(ex);
    }
  }

  private static EntityManagerFactory getEMFforTest() {
    try {
      Configuration configuration = new Configuration();

      Properties props = new Properties();
      props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
      props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
      props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test_db");
      props.put("hibernate.connection.username", "postgres");
      props.put("hibernate.connection.password", "postgres");
      props.put("hibernate.archive.autodetection", "class");
      props.put("hibernate.show_sql", "true");
      props.put("hibernate.hbm2ddl.auto", "create-drop");

      configuration.setProperties(props);

      getAnnotationConfiguration(configuration);

      ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
      System.out.println("Hibernate Java Config serviceRegistry created");

      return configuration.buildSessionFactory(serviceRegistry).unwrap(EntityManagerFactory.class);
    } catch (Throwable ex) {
      System.err.println("Initial EntityManagerFactory creation failed." + ex);
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static void getAnnotationConfiguration(Configuration configuration) {
    configuration.addAnnotatedClass(Department.class);
    configuration.addAnnotatedClass(Employee.class);
  }

  private static String getDBName() {
    return Utils.getPomProp("db.name");
  }
}
```

### Register Entities in HibernateConfig.java

- add the following lines to the getAnnotationConfiguration method:

```java
configuration.addAnnotatedClass(Department.class);
configuration.addAnnotatedClass(Employee.class);
```

### Test your DAO

- create a main method in the EmployeeDAO.java file, to test the methods
- create a main method in the DepartmentDAO.java file, to test the methods
- Create a test class for your DAO
-

### Properties from POM.xml

- create a util method:

```java
public static String getPomProp(String propName)  {
        InputStream is = dk.cphbusiness.utils.Utils.class.getClassLoader().getResourceAsStream("properties-from-pom.properties");
        Properties pomProperties = new Properties();
        try {
        pomProperties.load(is);
        } catch (IOException e) {
        e.printStackTrace();
        }
        return pomProperties.getProperty(propName);
        }
```

### Build your project with maven

- In Intellij -> maven tab -> Build tools settings -> check "Delegate IDE build/run actions to maven"
- Build the project (green hammer icon)
- Add your database name and javalin port to the pom.xml properties, so they can be read with the above method.

## Create a REST API

### Create Routes and Handlers

- create a new package: rest:
- create a new file: RootRoutes.java
- In RootRoutes.java create a method to set up the root path. It returns an EndpointGroup, which is a functional
  interface. It has a method called getEndpoints, which returns a list of endpoints. The EndpointGroup is used to group
  endpoints together.

```java
public static EndpointGroup getRoutes(Javalin app) {
        return () -> {
            app.routes(() -> {
//                path("/", authenticationRoutes.getRoutes());
                path("/", EmployeeRoutes.getEmployeeRoutes());
            });
        };
    }
```

- change your main method in Main.java to use the getRoutes method:

```java
app.routes(RootRoutes.getRoutes(app));
```

- create a new file: EmployeeRoutes.java
- create a method to set up the employee routes:

```java
public static EndpointGroup getEmployeeRoutes() {
        return () -> {
            path("employees", () -> {
                get(EmployeeController::getEmployees);
                post(EmployeeController::createEmployee);
                path(":id", () -> {
                    get(EmployeeController::getEmployee);
                    put(EmployeeController::updateEmployee);
                    delete(EmployeeController::deleteEmployee);
                });
            });
        };
    }
```

- create the EmployeeController.java file
- create a method to get all employees:

```java
public static void getEmployees(Context context) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        context.json(employees);
    }
```
### Security
- create an interface with the following methods (U for User, R for Role):
```java
public interface ISecurityDAO<U,R> {
U getVerifiedUser(String username, String password);
R createRole(String role);
boolean hasRole(String role, User user);
String createToken(String username, Set<String> roles);
U verifyToken(String token);
}
```
- implement the interface in a UserDao file
- implement the IDAO interface in the UserDao file as well
-  












