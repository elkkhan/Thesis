package com.thesis.neptun.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-05-30T16:26:06.438+0200")
@StaticMetamodel(Message.class)
public class Message_ {
	public static volatile SingularAttribute<Message, Integer> id;
	public static volatile SingularAttribute<Message, String> senderEmail;
	public static volatile SingularAttribute<Message, String> receiverEmail;
	public static volatile SingularAttribute<Message, String> date;
	public static volatile SingularAttribute<Message, Boolean> isRead;
	public static volatile SingularAttribute<Message, String> message;
	public static volatile SingularAttribute<Message, String> subject;
}
