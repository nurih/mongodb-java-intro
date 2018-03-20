package com.plusnconsulting;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.mongodb.ConnectionString;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.rx.client.*;

import org.bson.Document;
import org.bson.conversions.Bson;

import rx.Subscriber;

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

            // perform operations
            System.out.println("*** Insert doc 1");
            insertOneDocument(collection, SampleDoc.fromParsedJson()).toBlocking().single();

            System.out.println("*** Insert doc 2");
            insertOneDocument(collection, SampleDoc.fromFluentBuilder()).toBlocking().single();

            System.out.println("*** Update a doc");
            updateOneDocument(collection).toBlocking().single();

            System.out.println("*** Find something");
            findSomeDocuments(collection, "shoe").maxAwaitTime(3, TimeUnit.SECONDS).first().subscribe(d -> {
                System.out.println(d);
            });

            System.out.println("*** Use aggregation");
            Subscriber<? super Document> subscriber = createPrintingSubscriber();

            getRatings(collection).toObservable().subscribe(subscriber);

            System.in.read();

        } catch (Exception e) {
            System.out.format(e.toString());
            e.printStackTrace();
        }
    }

    private static Subscriber<? super Document> createPrintingSubscriber() {
        return new Subscriber<Document>() {

            @Override
            public void onCompleted() {
                System.out.println("aggPrinter:: operation completed.");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Document t) {
                System.out.println(t);
            }
        };
    }

    private static FindObservable<Document> findSomeDocuments(MongoCollection<Document> collection, String string) {

        return collection.find(eq("name", string)).projection(Projections.exclude("updated"));
    }

    private static rx.Observable<Success> insertOneDocument(MongoCollection<Document> collection, Document doc) {

        return collection.insertOne(doc);
    }

    private static rx.Observable<UpdateResult> updateOneDocument(MongoCollection<Document> collection)
            throws InterruptedException {
        Bson theUpdate = combine(push("ratings", new Document("name", "ogg").append("rating", 4.0)),
                currentDate("updated"));

        return collection.updateOne(eq("name", "shoe"), theUpdate);
    }

    public static AggregateObservable<Document> getRatings(MongoCollection<Document> collection) {
        Bson unwindStage = unwind("$ratings");
        Bson groupStage = group("$name", avg("averageRating", "$ratings.rating"));

        AggregateObservable<Document> result = collection.aggregate(Arrays.asList(unwindStage, groupStage));
        return result;
    }

    public static ConnectionString createConnectionString() {
        return new ConnectionString("mongodb://localhost:27017/demo");
    }

    public static MongoClient CreateClient(ConnectionString connectionString) throws Exception {
        return MongoClients.create(connectionString);

        //throw new RuntimeException("Please create and return a client");
    }

}
