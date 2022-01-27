package com.inert;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.mongodb.ConnectionString;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.plusnconsulting.SampleDocs;
import org.bson.Document;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        // create connection string
        ConnectionString connection = createConnectionString();

        // Create a client.
        try (MongoClient client = CreateClient(connection)) {

            // Get the database
            MongoDatabase db = client.getDatabase(connection.getDatabase());

            // Get a named collection
            MongoCollection<Document> collection = db.getCollection("SyncronousDemo");

            // perform operations

            for (Document doc : collection.find().limit(3)) {
                System.out.println(doc.toJson());
            }

            System.out.println("*** Insert doc 1");
            PlainAdapter.insertOneDocument(collection, SampleDocs.fromParsedJson());
            System.out.println("... Inserted");

            System.out.println("*** Insert doc 2");

            PlainAdapter.insertOneDocument(collection, SampleDocs.fromFluentBuilder());

            System.out.println("... Inserted");

            System.out.println("*** Update a doc");

            UpdateResult updateResult = PlainAdapter.updateOneDocument(collection);

            System.out.println(updateResult);

            System.out.println("*** Find something");

            Document firstOne = PlainAdapter.findSomeDocuments(collection, "shoe").maxAwaitTime(3, TimeUnit.SECONDS)
                    .first();

            System.out.println(firstOne.toJson());

            System.out.println("*** Use aggregation");

            AggregateIterable<Document> ratings = PlainAdapter.getRatings(collection);

            for (Document doc : ratings) {
                System.out.println(doc.toJson());
            }

            System.out.println("*** Direct Command");

            Document mongoCommand = new Document().append("listDatabases", 1);
            Document commandResult = client.getDatabase("admin").runCommand(mongoCommand);

            System.out.println(commandResult.toJson());

        } catch (MongoException | InterruptedException e) {
            System.out.format(e.toString());
            e.printStackTrace();
        }
    }

    public static ConnectionString createConnectionString() {
        return new ConnectionString("mongodb://localhost:27017/demo");
    }

    public static MongoClient CreateClient(ConnectionString connectionString) throws Exception {
        return MongoClients.create(connectionString);

        // throw new RuntimeException("Please create and return a client");
    }

}
