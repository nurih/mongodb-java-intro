package com.lab;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.set;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
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

        MongoClient mongoClient = MongoClients.create("mongodb://localhost");

        // create codec registry for POJOs
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        // get the "lab1" database
        MongoDatabase database = mongoClient.getDatabase("lab1").withCodecRegistry(pojoCodecRegistry);

        // drop all the data in collection COLLECTION_NAME
        database.getCollection(COLLECTION_NAME).drop();

        // make a document from JSON
        String jsonDocument = "{\"_id\":{\"$oid\":\"5db5cf8058fb4c25c20cacc8\"},\"degree\":\"PHD\",\"grades\":[{\"grade\":100,\"subject\":\"maths\"},{\"grade\":93,\"subject\":\"history\"},],\"full_name\":\"Bob Throllup\",\"GPA\":\"B+\"}";

        Document bsonStudent = Document.parse(jsonDocument);
        database.getCollection(COLLECTION_NAME).insertOne(bsonStudent);

        // get the strongly typed collection COLLECTION_NAME for a Student pojo
        final MongoCollection<Student> studentCollection = database.getCollection(COLLECTION_NAME, Student.class);

        // make a document and insert it
        final Student chip = new Student();
        chip.name = "Chip Marklar";
        chip.degree = Degree.PHD;
        chip.grades.add(new Grade("maths", 100));

        studentCollection.insertOne(chip);

        Student found = studentCollection.find(eq("full_name", chip.name)).first();
        System.out.println("Found student with id " + found.studentId);

        studentCollection.insertMany(StudentList.get());

        long count = studentCollection.countDocuments();

        System.out.printf("Student count %d\n", count);

        studentCollection.find().forEach((Student d) -> {
            System.out.printf("\t==>> %s\n", d.name);
        });

        studentCollection.find().limit(3).forEach((Student d) -> {
            System.out.printf("\t==>> %s\n", d.name);
        });

        studentCollection.find().sort(descending("grades.0.grade")).limit(3).forEach((Student d) -> {
            System.out.printf("High Score!  %s %d\n", d.name, d.grades.get(0).grade);
        });

        studentCollection.updateMany(lt("grades.grade", 70), set("needs_help", true));

        studentCollection.find(eq("needs_help", true)).forEach((Student d) -> {
            System.out.printf("Help %s (%d)\n", d.name, d.grades.get(0).grade);
        });

        studentCollection.updateOne(eq("full_name", "bob1"), set("full_name", "Smarty"),
                new UpdateOptions().upsert(true));

        Student smarty = studentCollection.find(eq("full_name", "Smarty")).first();
        System.out.println("Found smarty" + smarty.studentId);

        studentCollection.updateMany(gt("grades.grade", 90), set("GPA", "A"));

        long count_gpa_a = studentCollection.countDocuments(eq("GPA", "A"));

        System.out.printf("Number of 'A' GPA: %d\n" , count_gpa_a);

        mongoClient.close();
    }
}
