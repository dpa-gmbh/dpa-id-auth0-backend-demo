# dpa Id Auth0 Backend Demo Application
A demo project to set up backend application for Auth0 SPA or RWA applications.

## Application variables related to the demo applications
Please check the proper values for the below variables if you are trying to integrate a new application

Auth0ManagementAPIClient.class
- spring.security.managementAPI.client.provider.dpaid.id
- spring.security.managementAPI.client.provider.dpaid.secret
- spring.security.managementAPI.url
- spring.security.managementAPI.client.provider.dpaid.issuer-uri
- redirectUri

SecurityConfiguration
- spring.security.oauth2.client.provider.dpaid-devel.issuer-uri
- spring.security.oauth2.audience
- spring.security.oauth2.secondAudience

You can reach the current values that we use for the demo applications from application.yml file. 

DemoController 
- The default value that returns getRedirectUri function

## Local run
Provide proper value for the spring.security.managementAPI.client.provider.dpaid.secret variable.
The application will run under http://localhost:8080 

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
