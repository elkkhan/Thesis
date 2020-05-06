package com.thesis.neptun.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import com.thesis.neptun.main.MainWindow;

@Entity
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String senderEmail, receiverEmail;

  private String date;

  private boolean isRead;

  private String message, subject;

  public Message() {
  }

  public Message(String senderEmail, String receiverEmail, String message, String subject) {
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, hh:mm");
    this.date = formatter.format(new Date());
    this.senderEmail = senderEmail;
    this.receiverEmail = receiverEmail;
    this.message = message;
    this.subject = subject;
    this.isRead = false;
  }

  public void setIsRead(boolean isRead) {
    this.isRead = isRead;
  }

  public String getSenderName() {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    String name;
    try {
      name =
          (String)
              MainWindow.entityManager
                  .createNativeQuery(
                      "select name from student where email =\"" + senderEmail + "\"")
                  .getSingleResult();
    } catch (NoResultException e) {
      name =
          (String)
              MainWindow.entityManager
                  .createNativeQuery(
                      "select name from teacher where email =\"" + senderEmail + "\"")
                  .getSingleResult();
    }
    return name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getSenderEmail() {
    return senderEmail;
  }

  public String getDate() {
    return date;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }
}
