package com.thesis.neptun.model;

import com.thesis.neptun.main.MainWindow;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User extends MainWindow {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private int id;

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
