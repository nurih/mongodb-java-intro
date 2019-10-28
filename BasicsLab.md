# Getting Started with Java Lab

## Set up the development project

 In a directory on your computer, open a command prompt / terminal.

> You must have sufficient permissions on the base directory, allowing you to create files and execute eventual packages.

Clone  the repo `https://github.com/nurih/mongodb-java-intro` into your directory.

Compile and ensure that the project builds.

You should be able to run the `com.lab.BasicsLab` application.


## Implement boilerplate items


1. Open the file `com.plusnconsulting.mongoDemo1\src\main\java\com\lab\BasicsLab.java`

1. Create a `MongoClient` object. Use `MongoClients.create()` with a connection string to your `mongod` instance. For this lab, use the connection string `mongodb://localhost:27017/lab1` 

1. Create a database object using the name **BasicsLab**.

1. Drop the collection named **students**.

1. Close the mongoClient object.

1. Run the program.

_ If everything ran without exception, proceed to the next step __


## Inserting a document from JSON

1. Use the string below to create an instance of a BSON document named `jsonDocument`;

    ```java
    String jsonDocument = "{\"_id\":{\"$oid\":\"5db5cf8058fb4c25c20cacc8\"},\"degree\":\"PHD\",\"grades\":[{\"grade\":100,\"subject\":\"maths\"},{\"grade\":93,\"subject\":\"history\"},],\"full_name\":\"Bob Throllup\",\"GPA\":\"B+\"}";
    ``` 
1. Use the `Document.parse()` with the string above to create a BSON document.

1. Insert the BSON document into the **students** collection.

1. Use the shell to ensure that the document was indeed inserted.

> If you ran the same program again, you will get an exception. Why is that?

1. Create a new collection instance, this time using the `MongoCollection<T>` type, and supply the `Student` type to the generic type and the `getCollection()` method. Use the same collection name as before.

1. Create an instance of a `Student` using the code below:
    ```java
     final Student chip = new Student();
        chip.name = "Chip Marklar";
        chip.degree = Degree.PHD;
        chip.grades.add(new Grade("maths", 100));
    ```
    Review the `Student` class. You will notice that there are a few attributes on some of the fields. These attributes affect how the codec maps the POJO to a document and vice-versa.

1. Insert the POJO instance into MongoDB using the strongly typed collection instance.

1. Check that the document was inserted into MongoDB using the shell.

1. Retrieve the document using the `find()` command. Use the [filter builder eq](https://mongodb.github.io/mongo-java-driver/3.11/builders/filters/), and the method `.first()` to get the first document matching the filter.
1. Print out the studentId of the document retrieved.

> Who assigned that id? This was created by the driver - not the server. 

## More than one document 

1. Inserting a document at a time incurs the overhead of network protocol and acknowledgment. When needing to insert many documents at once, this overhead can become a significant factor. Use `insertMany()` to send multiple documents at once to be inserted. Use `StudentList.get()` which generates a list of students as the document values.

1. Count the number of documents in the collection, and print it out.

1. Using `find()`, iterate over all students in the students collection and print out their name only.

> How can you reduce network traffic to your client for the operation above? 

1. Get any of the students again, but this time limit the number of documents returned to 3, and print the student names.

1. Instead of any 3 students, get the 3 with the _highest grade on the first element_ in the **grades** array. (hint: use dot-notation for the first ordinal element in the array)

## Updates

1. To each student with any grade below 70, add a field to the document with a field `needs_help` with value `true`. 

1. Find each of the students needing help and print our their name, and the first grade in their grades.

1. Update just the one student named **bob1** and rename him to **Smarty**. If there is no such student, then insert one with the name Smarty.

1. Find the student Smarty and print out the student id.

1. Update all students that have a grade above 90 and add a GPA field set to an "A" letter grade.

1. Count the students who have an "A" GPA.

