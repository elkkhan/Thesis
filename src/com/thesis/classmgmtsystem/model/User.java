package com.thesis.classmgmtsystem.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "USER_TYPE")
public abstract class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
  private List<Message> inbound;
  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
  private List<Message> outbound;
  private byte[] password, salt;
  private String code, name, email;

  public User() {
  }

  public User(byte[] password, byte[] salt, String code, String name, String email) {
    this.password = password;
    this.salt = salt;
    this.code = code;
    this.name = name;
    this.email = email;
  }

  public List<Message> getOutbound() {
    return outbound;
  }

  public void setOutbound(List<Message> outbound) {
    this.outbound = outbound;
  }

  public List<Message> getInbound() {
    return inbound;
  }

  public void setInbound(List<Message> messageList) {
    this.inbound = messageList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCode() {
    return code;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public byte[] getPassword() {
    return password;
  }

  public void setPassword(byte[] password) {
    this.password = password;
  }

  public byte[] getSalt() {
    return salt;
  }
}
