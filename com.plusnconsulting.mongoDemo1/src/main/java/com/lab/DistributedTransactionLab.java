package com.lab;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import java.util.Arrays;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.TransactionBody;

import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * The POJO QuickTour code example
 */
public class DistributedTransactionLab {

    public final static String DATABASE_NAME = "transactionDemo";
    public final static String TICKETS_COLLECTION = "tickets";
    public final static String SALES_COLLECTION = "sales";

    /**
     * Transaction around several operations
     * 
     * @throws InterruptedException if a latch is interrupted
     */
    public static void main(final String[] args) throws InterruptedException {

        /******************************************/
        /** CODE FOR LAB BELOW THIS POINT **/
        /******************************************/
        
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/?replSet=trx");

        MongoDatabase db = mongoClient.getDatabase(DATABASE_NAME);

        // Seed the database
        seedDatabase(db);

        final com.mongodb.client.ClientSession clientSession = mongoClient.startSession();

        TransactionOptions transactionOptions = TransactionOptions.builder().readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.LOCAL).writeConcern(WriteConcern.MAJORITY).build();

        TransactionBody<String> transactionBody = new TransactionBody<String>() {
            public String execute() {
                MongoCollection<Document> tickets = db.getCollection(TICKETS_COLLECTION);

                MongoCollection<Document> sales = db.getCollection(SALES_COLLECTION);

                // Create a filter for a specific ticket.

                Bson ticketFilter = and(eq("show", "Pink Martini"), eq("seat.row", 14), eq("seat.number", 31));

                Document ticket = tickets.find(ticketFilter).first();

                if (ticket == null) {
                    throw new RuntimeException("No Ticket");
                }

                if (ticket.getString("soldTo") != null) {
                    throw new RuntimeException("Sorry... already sold");
                }

                // Set the soldTo field to the value "zelda" in the tickets collection

                tickets.updateOne(clientSession, ticketFilter, set("soldTo", "zelda"));

                // Create a sale document from the ticket, and assign it to "zelda"

                Document sale = createSaleDocument(ticket, "zelda");
                
                // Insert the sale document into the sales collection

                sales.insertOne(clientSession, sale);

                return "Sale Complete";
            }

        };
        try {

            // Print out the transaction message which was returned 
            // from the transaction body's execute() method
            
            String message = clientSession.withTransaction(transactionBody, transactionOptions);

            System.out.println(message);

        } catch (RuntimeException e) {
            System.out.println(e);
        } finally {
            clientSession.close();
        }

        // Close client
        mongoClient.close();
    }

    private static Document createSaleDocument(final Document ticket, final String name) {
        Document result = Document.parse("{\"name\":\"" + name + "\",\"tickets\":[]}");
        result.getList("tickets", Document.class).add(ticket);
        return result;
    }

    private static void seedDatabase(MongoDatabase db) {
        db.drop();

        db.getCollection(TICKETS_COLLECTION).withWriteConcern(WriteConcern.MAJORITY).insertMany(Arrays.asList(
                Document.parse("{\"show\":\"Pink Martini\",\"seat\":{\"row\":14,\"number\":31}}"),
                Document.parse("{\"show\":\"Pink Martini\",\"seat\":{\"row\":14,\"number\":32}}"),
                Document.parse("{\"show\":\"Pink Martini\",\"seat\":{\"row\":14,\"number\":33}}"),
                Document.parse("{\"show\":\"Pink Martini\",\"seat\":{\"row\":14,\"number\":34}, \"soldTo\": \"fran\"}")

        ));

        db.getCollection(SALES_COLLECTION).withWriteConcern(WriteConcern.MAJORITY).insertOne(Document.parse(
                "{\"name\":\"fran\",\"tickets\":[{\"show\":\"Pink Martini\",\"seat\":{\"row\":14,\"number\":34}}]}"));
    }
}
