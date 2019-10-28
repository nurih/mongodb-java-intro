# Transaction Lab

This lab explores a simple transaction against two collections.

The scenario for this lab is as follows:

- Concert tickets are stored in the **tickets** collection.
- Ticket sales are recorded in the **sales** collection. 
- When a ticket is sold, it must be marked as sold.
- When a ticket is sold, the sale must be recorded, with the ticket awarded to the purchaser.

The ticket document looks like this:

```json
{
    "_id" : ObjectId("..."),
    "show" : "Pink Martini",
    "seat" : {
            "row" : 14,
            "number" : 31
    },
    "soldTo" : "robert"
}
```
The _soldTo_ field will may be either null or not present.

The sale document looks like this:

```json
{
        "_id" : ObjectId("..."),
        "name" : "terry",
        "tickets" : [
                {
                        "show" : "Pop Favorites",
                        "seat" : {
                                "row" : 189,
                                "number" : 11
                        }
                }
        ]
}
```

## Run a MongoDB server

> __For this lab, you will need to run Mongo version 4.0 or above.__

1. Run a `mongod` instance with a new data directly. Start it with a Replica Set name *trx*

1. Initiate the replica set using `rs.initiate()`
    > Transactions leverage the OpLog, and therefore only work against Replica Sets.

1. Wait for the single node Replica Set to to become primary before running any code.

## Create the code

1. Open the file `com.plusnconsulting.mongoDemo1\src\main\java\com\lab\DistributedTransactionLab.java`

1. Compile and ensure you can run that program.

1. Set the connection string to point to your replica set. Remember to include the `replSet` query string variable and set its value to the replica set name *trx*.

1. Under the comment "Seed the database" call the method `seedDatabase()` with the database instance.

1. Under the comment "Create a filter for a specific ticket", assign the filter an expression using the filter expression builders `and` and `eq`:
    1. The  "show" named "Pink Martini"
    1. Seat in Row  14
    1. Seat number 31

1. The code checks to see if the ticket exists. If not, an exception is thrown.

1. The code checks to see if the ticket is sold already. If not, an exception is thrown.

1. Update the ticket in the tickets collection using the filter created previously, and set the value of `soldTo` to "zelda".

1. Create a sale document of type  `Document` by calling `createSaleDocument()` supplying the ticket queried previously and the name "zelda".

1. Insert the sale document into the `sales` collection.

    > Everythin thus far was inside the body of the `execute()` method. All that remain is to run the transaction.

1. Place the code below in the body of the `try` block:
    ```java
    String message = clientSession.withTransaction(transactionBody, transactionOptions);
    System.out.println(message);
    ```

1. Compile and run the program. Upon successful run, the sales collection should have 2 documents, and the tickets collection should have 2 tickets sold.

1. Change the filter to use seat 999 row 14, and run the program. Verify that no ticket was sold, and that the program indicated no such ticket exists.

1. Change the filter to use seat number 31, row 14 and run the program. Verify that no ticket was sold, and that the program indicated the ticket is already sold.



