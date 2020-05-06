package com.thesis.neptun.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-05-23T22:56:05.098+0200")
@StaticMetamodel(Course.class)
public class Course_ {
	public static volatile SingularAttribute<Course, Integer> id;
	public static volatile SingularAttribute<Course, String> name;
	public static volatile SingularAttribute<Course, String> courseCode;
	public static volatile SingularAttribute<Course, Teacher> teacher;
	public static volatile ListAttribute<Course, Student> students;
	public static volatile SingularAttribute<Course, Integer> credit;
	public static volatile SingularAttribute<Course, String> startTime;
}
