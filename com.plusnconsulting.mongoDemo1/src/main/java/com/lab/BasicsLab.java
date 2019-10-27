package com.lab;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.set;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 * The POJO QuickTour code example
 */
public class BasicsLab {

    public final static String COLLECTION_NAME = "students";

    /**
     * Run this main method to see the output of this quick example.
     *
     * @param args takes an optional single argument for the connection string
     * @throws InterruptedException if a latch is interrupted
     */
    public static void main(final String[] args) throws InterruptedException {

        MongoClient mongoClient = new MongoClient("localhost");

        // create codec registry for POJOs
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        // get the "lab1" database
        MongoDatabase database = mongoClient.getDatabase("lab1").withCodecRegistry(pojoCodecRegistry);

        // drop all the data in collection COLLECTION_NAME
        database.getCollection(COLLECTION_NAME).drop();

        // make a document from JSON
        String jsonDocument = "{\"_id\":{\"$oid\":\"5db5cf8058fb4c25c20cacc8\"},\"degree\":\"PHD\",\"grades\":[{\"grade\":100,\"subject\":\"maths\"},{\"grade\":93,\"subject\":\"history\"},],\"full_name\":\"Bob Throllup\",\"GPA\":\"B+\"}";

        Document bsonStudent = Document.parse(jsonDocument);
        database.getCollection("people").insertOne(bsonStudent);

        // get the strongly typed collection COLLECTION_NAME for a Student pojo
        final MongoCollection<Student> collection = database.getCollection(COLLECTION_NAME, Student.class);

        // make a document and insert it
        final Student chip = new Student();
        chip.name = "Chip Marklar";
        chip.degree = Degree.PHD;
        chip.grades.add(new Grade("maths", 100));

        collection.insertOne(chip);

        Student found = collection.find(eq("full_name", chip.name)).first();
        System.out.println("Found student with id " + found.studentId);

        collection.insertMany(StudentList.get());

        long count = collection.count();

        System.out.printf("Student count %d\n", count);

        collection.find().forEach((Block<? super Student>) (Student d) -> {
            System.out.printf("\t==>> %s\n", d.name);
        });

        collection.find().limit(3).forEach((Block<? super Student>) (Student d) -> {
            System.out.printf("\t==>> %s\n", d.name);
        });

        collection.find().sort(descending("grades.0.grade")).limit(3).forEach((Block<? super Student>) (Student d) -> {
            System.out.printf("High Score!  %s %d\n", d.name, d.grades.get(0).grade);
        });

        collection.updateMany(lt("grades.grade", 70), set("needs_help", true));

        collection.find(eq("needs_help", true)).forEach((Block<? super Student>) (Student d) -> {
            System.out.printf("Help %s (%d)\n", d.name, d.grades.get(0).grade);
        });

        collection.updateOne(eq("full_name", "bob1"), set("full_name", "Smarty"), new UpdateOptions().upsert(true));

        Student smarty = collection.find(eq("full_name", "Smarty")).first();
        System.out.println("Found smarty" + smarty.studentId);

        collection.updateMany(gt("grades.grade", 90), set("GPA", "A"));

        mongoClient.close();
    }
}
