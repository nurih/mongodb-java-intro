package com.lab;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Student {

    @BsonId
    public ObjectId studentId;

    @BsonProperty("full_name")
    public String name;

    public Degree degree;

    public List<Grade> grades = new ArrayList<Grade>();
    
    public Student() {
    };

}
