package com.thesis.neptun.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends User {

  private String role;

  @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
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

  @Override
  public String toString() {
    return "Teacher [name=" + getName() + ", email=" + getEmail() + ", role=" + role + "]";
  }
}
