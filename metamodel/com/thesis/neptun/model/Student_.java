package com.thesis.neptun.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-05-29T11:39:39.032+0200")
@StaticMetamodel(Student.class)
public class Student_ extends User_ {
	public static volatile ListAttribute<Student, Course> courses;
}
