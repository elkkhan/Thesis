package com.thesis.neptun.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-05-29T11:39:17.320+0200")
@StaticMetamodel(Teacher.class)
public class Teacher_ extends User_ {
	public static volatile SingularAttribute<Teacher, String> role;
	public static volatile ListAttribute<Teacher, Course> courses;
}
