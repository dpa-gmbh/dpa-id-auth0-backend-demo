# dpa Id Auth0 Backend Demo Application


## Setting Java Version
In this project we are using **Java 21** version.

You can install java 21-amzn package by using [sdkman](https://sdkman.io/jdks)

## Build
To build the project, run the command

```
maven clean build
```

Now Maven should have created the file `/target/dpa-id-auth0-backend-demo.jar` which you can run with the command

```
java -jar target/dpa-id-auth0-backend-demo.jar
```

## API docs
You can access the API documentation from below URL
```shell
http://localhost:8080/swagger-ui/index.html
```

### Configuration variables

Variables are referred to in Spring's classes using the following notation.

````
@Value("${path.to.variable}")
public String testToken = "undefinded"; 
````

These variables are then defined in AWS Parameter store like so

````
/config/<application-name>_<stage_name>/path.to.variable
````

