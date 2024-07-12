# **Drive your validation messages from database**

# Introduction
Developers often go through repetitive development for setting messages for different client supporting different languages. This is the solution.
This is a Java library built using the Spring Boot framework that handles database-driven Jakarta validation messages  for your Spring Boot projects developed 
with JDK greater than 17 and also supports database driven message for your Rest Api Response. This library is not for the purpose of validation of fields but 
only supports storing validation messages for Jakarta validation and 
making the response messages dynamic to support any language dynamically.

## Requirements

1. JDK greater than or equal to 17.
2. Currently supports sql databases only

## How to Use in Your Project

1. Clone this project.

2. Install Redis server and SQL database (in this case, using PostgreSQL).

3. After installing this library, add the following Maven project dependency in `pom.xml` of your project:

    ```xml
    <dependency>
       <groupId>com.message</groupId>
       <artifactId>message-util</artifactId>
       <version>0.0.1</version>
    </dependency>
    ```

   For Gradle, you can include:

    ```gradle
    implementation 'com.message:message-util:0.0.1'
    ```

   Optionally, you can use a Maven dependency server eg. reposilite for maintaining this dependency.


4. In your project, Set up `application.properties` as follows:

    ```properties
    spring.data.redis.database=0
    spring.data.redis.host=localhost
    spring.data.redis.port=6379
    spring.data.redis.password=admin
    spring.data.redis.inward-key-ttl=1
    spring.data.redis.timeout=10000
    
    spring.datasource.url=jdbc:postgresql://db_host:port/db_name
    spring.datasource.username=${SPRING_DATASOURCE_USERNAME:db_username}
    spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:db_password}
    spring.datasource.driver-class-name=org.postgresql.Driver

    spring.datasource.hikari.connection-timeout=60000
    spring.datasource.hikari.maximum-pool-size=100
    spring.datasource.hikari.max-lifetime=600000
    spring.datasource.hikari.minimum-idle=20
    spring.datasource.hikari.validation-timeout=3000
    spring.datasource.hikari.idle-timeout=30000
    ```
   You can also use yml file for configuring.
   Note that datasource configuration can be different if you use other sql database than postgresql database.


5. In your project, Set up your code like following code snippets:

   ### Model

    ```java
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public class SampleModel {
        @NotBlank(message = "{sample.id.required}")
        private String sampleId;
    }
    ```

   In the above example, **`{sample.id.required}`** is a message placeholder that displays a message if the field is blank, achieved using **`@NotBlank(message = "{sample.id.required}")`**.

   ### Controller

    ```java
    @RestController
    @RequestMapping("/sample")
    @Validated
    public class SampleController {
        @PostMapping(value = "sample", produces = "application/json")
        ResponseEntity<?> sampleRequest(@Valid @RequestBody SampleModel sampleModel) {
            return ResponseEntity.ok(Map.of("message","${sample.success.message}")).build();
        }
    }
   
    ```
   In the above example, **`${sample.success.message}`** is a message placeholder that displays a message for success message`**.
   Other than jakarta validation message, you shoule use '$' for placeholder messages.
   Also you can follow same approach for error messages.

   In the above example, the **`@Validated`** annotation should be used at the class level 
   and **`@Valid`** should be used for the payload like **`@Valid @RequestBody SampleModel sampleModel`**. 
   You can use it for GET request query params validation as well.


6. Execute the following DDL scripts to create a table to store validation messages.

    ```sql
    CREATE SEQUENCE IF NOT EXISTS locale_messages_id_seq;
    CREATE TABLE IF NOT EXISTS public.locale_messages
    (
        id bigint NOT NULL DEFAULT nextval('locale_messages_id_seq'::regclass),
        lang character varying(255) COLLATE pg_catalog."default",
        local_code character varying(255) COLLATE pg_catalog."default",
        local_message character varying(2555) COLLATE pg_catalog."default",
        placeholder character varying(2555) COLLATE pg_catalog."default",
        CONSTRAINT locale_messages_pkey PRIMARY KEY (id)
    );
    ```

7. Execute DML scripts to insert your validation messages like:

    ```sql
    INSERT INTO locale_messages (lang, local_code, local_message, placeholder)
    VALUES ('en_US', '400', 'Sample Id is Required.', 'sample.id.required'),
           ('ar', '400', 'Sample Id is Required in Arabic language', 'sample.id.required'),
           ('np', '400', 'Sample Id Anibarya cha', 'sample.id.required')
           ('en_US', '400', 'This is success request.', 'sample.success.message'),
           ('ar', '400', 'This is success request in Arabic language', 'sample.success.message'),
           ('np', '400', 'Sucess request ho', 'sample.success.message')
   ;
    ```


8. In your request, include the header **`Accept-Language:en_US`** for English language , **`Accept-Language:ar`** for Arabic language , and **`Accept-Language:np`** for Nepali language in case of failed validation.

## Support

For support, contact the author **Hemanta Ghimire** at **rockingguyheman.hg@gmail.com**. You can also contact via LinkedIn [Hemanta Ghimire](https://www.linkedin.com/in/hemanta-ghimire-23b7a4135/).
