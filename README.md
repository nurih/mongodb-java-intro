Getting Started with Java Lab
========

### Create a new project use Maven

 In a directory on your computer, open a command prompt / terminal.

> You must have sufficient permissions on the base directory, allowing you to create files and execute eventual packages.

```bash
mvn archetype:generate -DgroupId=com.plusnconsulting.mongoDemo1 -DartifactId=com.plusnconsulting.mongoDemo1 -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

## Implement boilerplate items

1. Create a `MongoConnection` object. The connection can be created in several ways. It is common though to create one from a connection string. A MongoDB connection string is a single URI representation of the connection target servers, options, security and cluster mode. For this lab, use the connection string `mongodb://localhost:27017/lab1` 

1. Create a `MongoClient` object. Typically, one client instance is created and re-used across an application. This object uses a connection pool internally, and is cluster-aware for recovery from replica-set primary transitions.

1. Create a database and collection. Here, we can re-use the connection string, and chose the database the connection string specified. use the `getDatabase()` method of the client, passing it the database name from the `getDatabase()` method of the connection string.

1. Create a collection names `lab1`

1. Create an instance of a BSON document by parsing this JSON string:
    ```JSON 
    {"name": "shirt", "colors": ["red","blue","orange"]}
    ```

