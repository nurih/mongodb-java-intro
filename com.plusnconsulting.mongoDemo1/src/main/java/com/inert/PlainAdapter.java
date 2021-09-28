package com.inert;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

import java.util.Arrays;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import org.bson.conversions.Bson;

public final class PlainAdapter {

    public static FindIterable<Document> findSomeDocuments(MongoCollection<Document> collection, String string) {

        return collection.find(eq("name", string)).projection(Projections.exclude("updated"));
    }

    public static void insertOneDocument(MongoCollection<Document> collection, Document doc) {

        collection.insertOne(doc);
    }

    public static UpdateResult updateOneDocument(MongoCollection<Document> collection)
            throws InterruptedException {
        Bson theUpdate = combine(Updates.push("ratings", new Document("name", "ogg")), Updates.set("rating", 4.0),
                Updates.currentDate("updated"));

        return collection.updateOne(eq("name", "shoe"), theUpdate);
    }

    public static AggregateIterable<Document> getRatings(MongoCollection<Document> collection) {
        
        Bson unwindStage = unwind("$ratings");

        Bson groupStage = group("$name", avg("averageRating", "$ratings.rating"));

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(unwindStage, groupStage));
        
        return result;
    }

    public static Document runCommand(MongoClient client, String dbName, Document command){
        MongoDatabase db = client.getDatabase(dbName);
        Document result = db.runCommand(command);
        return result;
    }

}
