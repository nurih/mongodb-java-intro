package com.plusnconsulting;

import java.util.HashMap;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;

public class SampleDocs {
    public static Document fromParsedJson() {

        String json = "{" + " \"name\" : \"shoe\", " + "\"size\" : 6, " + "\"colors\" : [ \"taupe\", \"black\" ], "
                + "\"ratings\" : [ " + "{ \"name\" : \"larry\", \"rating\" : 1.0 } "
                + "{ \"name\" : \"moe\", \"rating\" : 2.9 } " + "{ \"name\" : \"curly\", \"rating\" : 4.3 } " + "], "
                + "\"updated\" : ISODate(\"2018-04-01\")" + " }";

        return Document.parse(json);
    }

    public static Document fromFluentBuilder() {

        BsonValue rating = new BsonDocument("name", new BsonString("kim")).append("rating", new BsonDouble(4.0));

        BsonArray ratings = new BsonArray(Arrays.asList(rating));

        Document result = new Document().append("name", "shirt").append("size", "M")
                .append("colors", new BsonArray(Arrays.asList(new BsonString("orange"), new BsonString("green"))))
                .append("ratings", ratings);

        return result;
    }

    public static final UUID serial = UUID.fromString("a0a0a0a0-beef-0000-7eee-acdcacdcf00d");

    public static DeviceReading sampleDeviceData() {
        return new DeviceReading(serial,
                new double[] { 34.505898, -120.500595 }, new HashMap<String, Double>(), new Date());
    }
}