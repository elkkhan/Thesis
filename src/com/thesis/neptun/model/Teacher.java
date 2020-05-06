package com.thesis.neptun.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Teacher extends User {

  private String role;

  @OneToMany(mappedBy = "teacher")
  private List<Course> courses = new ArrayList<>();

  public Teacher() {
  }

  public Teacher(
      String code, byte[] password, byte[] salt, String name, String email, String role) {
    super(password, salt, code, name, email);
    this.role = role;
  }

  public List<Course> getCourses() {
    return courses;
  }

  public void setCourses(List<Course> courses) {
    this.courses = courses;
  }

  //	public String getName() {
  //		return name;
  //	}
  //	public void setName(String name) {
  //		this.name = name;
  //	}
  //	public String getEmail() {
  //		return email;
  //	}
  //	public void setEmail(String email) {
  //		this.email = email;
  //	}

  @Override
  public String toString() {
    return "Teacher [name=" + getName() + ", email=" + getEmail() + ", role=" + role + "]";
  }
}
