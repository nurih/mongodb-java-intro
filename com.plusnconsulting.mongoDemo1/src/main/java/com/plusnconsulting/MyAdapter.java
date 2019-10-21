package com.plusnconsulting;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

import java.util.Arrays;

import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.AggregatePublisher;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.Success;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;

public final class MyAdapter {

    public static FindPublisher<Document> findSomeDocuments(MongoCollection<Document> collection, String string) {

        return collection.find(eq("name", string)).projection(Projections.exclude("updated"));
    }

    public static Publisher<Success> insertOneDocument(MongoCollection<Document> collection, Document doc) {

        return collection.insertOne(doc);
    }

    public static Publisher<UpdateResult> updateOneDocument(MongoCollection<Document> collection)
            throws InterruptedException {
        Bson theUpdate = combine(Updates.push("ratings", new Document("name", "ogg")), Updates.set("rating", 4.0),
                Updates.currentDate("updated"));

        return collection.updateOne(eq("name", "shoe"), theUpdate);
    }

    public static AggregatePublisher<Document> getRatings(MongoCollection<Document> collection) {
        Bson unwindStage = unwind("$ratings");
        Bson groupStage = group("$name", avg("averageRating", "$ratings.rating"));

        AggregatePublisher<Document> result = collection.aggregate(Arrays.asList(unwindStage, groupStage));
        return result;
    }

}
