package com.inert;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class Dice {
    @BsonId
    public ObjectId diceId;
    public int number;
}
