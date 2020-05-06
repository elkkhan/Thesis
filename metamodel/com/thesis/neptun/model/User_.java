package com.thesis.neptun.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-05-29T13:43:43.862+0200")
@StaticMetamodel(User.class)
public class User_ {
	public static volatile SingularAttribute<User, Integer> id;
	public static volatile SingularAttribute<User, byte[]> password;
	public static volatile SingularAttribute<User, byte[]> salt;
	public static volatile SingularAttribute<User, String> code;
	public static volatile SingularAttribute<User, String> name;
	public static volatile SingularAttribute<User, String> email;
}
