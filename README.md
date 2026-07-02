# Spring MongoDB Test

Spring MongoDB Test is a TestExecutionListener that simplifies seeding the database in spring-data-mongodb integration
tests.

## Dependency

### Gradle

#### Spring Boot 2

```groovy
testImplementation 'com.hws-software:spring-mongodb-test-boot2:1.0.0'
```

### Maven

```xml

<dependency>
    <groupId>com.hws-software</groupId>
    <artifactId>spring-mongodb-test-boot2</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

#### Spring Boot 3

```groovy
testImplementation 'com.hws-software:spring-mongodb-test-boot3:1.0.0'
```

### Maven

```xml

<dependency>
    <groupId>com.hws-software</groupId>
    <artifactId>spring-mongodb-test-boot3</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

#### Spring Boot 4

```groovy
testImplementation 'com.hws-software:spring-mongodb-test-boot4:1.0.0'
```

### Maven

```xml

<dependency>
    <groupId>com.hws-software</groupId>
    <artifactId>spring-mongodb-test-boot4</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

## Setup

The following lines of code add the `MongoDBTestExecutionListener` to the SpringBootTest lifecycle.

```java
import com.hwssoftware.springmongotest.MongoDBTestExecutionListener;

@SpringBootTest
@TestExecutionListeners(
        value = {MongoDBTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class MyIntegrationTest {
    // ...
}
```

The database will be determined through one of the following properties:

```yaml
spring.data.mongodb.database: my_mongo_database
# or
spring.data.mongodb.uri: mongodb://localhost:27017/my_mongo_database

# on Spring Boot 4, the properties were renamed (no more "data"):
spring.mongodb.database: my_mongo_database
# or
spring.mongodb.uri: mongodb://localhost:27017/my_mongo_database
```

In this case everything will be executed against `my_mongo_database`.

**Alternatively**: One can set the database manually inside the `@Doc` annotation through the `db` property.

## Tests with Documents

The `@Doc` annotation allows inserting documents into the given database.

A typical test will look like this:

```java
import com.hwssoftware.springmongotest.Doc;
import com.hwssoftware.springmongotest.MongoDBTest;
import org.junit.jupiter.api.Test;

@Doc(collection = "person", files = {"/documents/john-doe.bson"}, /* optional: db = "my_mongo_database" */)
public class MyTest extends MyIntegrationTest {
    @Test
    @MongoDBTest
    public void myTest() {
        // ...    
    }
}
```

A bson file, located in `<projectRoot>/src/test/resources/documents/john-doe.bson` could look like this:

```bson
{
    _id: ObjectId('6032473edd63e50bd3565171'),
    firstName: 'John',
    lastName: 'Doe',
    _class: 'person'
}
```

For the following entity:

```java

@Document("person")
@TypeAlias("person")
public class Person {
    @Id
    private ObjectId id;
    private String firstName;
    private String lastName;

    //...
}
```

You can even aggregate multiple `@Doc` annotations into custom annotations like:

```java
import com.hwssoftware.springmongotest.Doc;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Doc(collection = "person", files = {"/documents/john-doe.bson"})
@Doc(collection = "department", files = {"/documents/sales.bson"})
public @interface InsertSalesDepartment {
}
```

## Tests with Files

The `@GridFsFile` annotation allows inserting files into GridFs of the given database.

A test will look like this:

```java
import com.hwssoftware.springmongotest.GridFsFile;
import com.hwssoftware.springmongotest.MongoDBTest;
import org.junit.jupiter.api.Test;

@GridFsFile(id = "60327c7f9189342c201e0e11", filePath = "/files/test.pdf", /* optional: db = "my_mongo_database" */)
public class MyGridFsTest extends MyIntegrationTest {
    @Test
    @MongoDBTest
    public void myTest() {
        // ...    
    }
}
```

## Example

For a more thorough example, please check out the [examples](example). It contains a functional Spring Boot application
and several tests that showcase this library.

## License

The MIT License (MIT)

Copyright (c) 2026 HWS Software GmbH, Jimi Steidl <www.hws-software.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
