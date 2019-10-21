package com.plusnconsulting;

import java.util.concurrent.TimeUnit;

import com.mongodb.ConnectionString;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.mongodb.reactivestreams.client.Success;
import com.plusnconsulting.SubscribeHelpers.InvokingSubscriber;
import com.plusnconsulting.SubscribeHelpers.ObservableSubscriber;
import com.plusnconsulting.SubscribeHelpers.PrintDocumentSubscriber;
import com.plusnconsulting.SubscribeHelpers.PrintSubscriber;

import org.bson.Document;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        // create connection string
        ConnectionString connection = createConnectionString();

        try {
            // Create a client.
            MongoClient client = CreateClient(connection);

            // Get the database
            MongoDatabase db = client.getDatabase(connection.getDatabase());

            // Get a named collection
            MongoCollection<Document> collection = db.getCollection("lab1");

            ObservableSubscriber<Success> sub = new InvokingSubscriber<Success>();

            // perform operations

            PrintDocumentSubscriber printer = new PrintDocumentSubscriber();
            collection.find().limit(3).subscribe(printer);
            printer.await();

            System.out.println("*** Insert doc 1");
            MyAdapter.insertOneDocument(collection, SampleDocs.fromParsedJson())
                    .subscribe(sub);
            sub.await();

            sub = new ObservableSubscriber<Success>();
            System.out.println("*** Insert doc 2");
            MyAdapter.insertOneDocument(collection, SampleDocs.fromFluentBuilder()).subscribe(sub);
            sub.await();

            System.out.println("*** Update a doc");
            InvokingSubscriber<UpdateResult> updateSubscriber = new InvokingSubscriber<UpdateResult>();
            MyAdapter.updateOneDocument(collection).subscribe(updateSubscriber);
            updateSubscriber.await();

            System.out.println("*** Find something");
            MyAdapter.findSomeDocuments(collection, "shoe").maxAwaitTime(3, TimeUnit.SECONDS).first()
                    .subscribe(new PrintSubscriber<Document>("Yo %s"));

            System.out.println("*** Use aggregation");
            PrintSubscriber<Document> printingSubscriber = new PrintSubscriber<Document>("Yo %s");

            MyAdapter.getRatings(collection).subscribe(printingSubscriber);

            ///////////////
            /// wait
            System.in.read();

        } catch (Exception e) {
            System.out.format(e.toString());
            e.printStackTrace();
        } catch (Throwable e) {
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
