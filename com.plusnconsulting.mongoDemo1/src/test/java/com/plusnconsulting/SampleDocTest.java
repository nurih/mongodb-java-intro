package com.plusnconsulting;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.bson.*;

import org.junit.jupiter.api.Test;

public class SampleDocTest {
    @Test
    public void fromParsedJson_CreatesBsonDocument() {
        Document actual = SampleDoc.fromParsedJson();

        assertEquals("shoe", actual.getString("name"));
        assertEquals(6, actual.getInteger("size", 0));

    }

    @Test
    public void fromFluentBuilders_CreatesBsonDocument() {
        Document actual = SampleDoc.fromFluentBuilder();

        assertEquals("shirt", actual.getString("name"));
        assertEquals("M", actual.getString("size"));
        
        List<BsonValue> actualColors = actual.get("colors", BsonArray.class).getValues();
        assertIterableEquals(Arrays.asList(new BsonString("orange"), new BsonString("green")), actualColors);
    }
}