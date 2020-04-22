You can set it through an environment variable:

SPRING_CONFIG_LOCATION=classpath:/local-config/,classpath:/config/b/

or

java -jar target/spring-batch-csv-partition-0.1.0.jar --spring.config.location=file:src/main/resources/local-config/
