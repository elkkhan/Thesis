package com.thesis.classmgmtsystem.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @ManyToOne
  private User sender;
  @ManyToOne
  private User receiver;
  private String date;
  private boolean isRead;
  private String message, subject;

  public Message() {
  }

  public Message(User sender, User receiver, String message, String subject) {
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, hh:mm");
    this.date = formatter.format(new Date());
    this.sender = sender;
    this.receiver = receiver;
    this.message = message;
    this.subject = subject;
    this.isRead = false;
  }

  public User getSender() {
    return sender;
  }

  public void setSender(User sender) {
    this.sender = sender;
  }

  public User getReceiver() {
    return receiver;
  }

  public void setReceiver(User receiver) {
    this.receiver = receiver;
  }

  public boolean isRead() {
    return isRead;
  }

  public void setRead(boolean read) {
    isRead = read;
  }

  public void setIsRead(boolean isRead) {
    this.isRead = isRead;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
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
