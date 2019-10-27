package com.lab;

import java.util.ArrayList;
import java.util.List;

public class StudentList {
    public static List<Student> get() {
        List<Student> result = new ArrayList<Student>();
        for (int i = 0; i < 41; i++) {
            Student s = new Student();
            s.name = "bob" + i;
            s.degree = Degree.values()[i % 3];
            s.grades.add(new Grade("maths", (60 + i) % 100));
            result.add(s);
        }
        return result;
    }
}
