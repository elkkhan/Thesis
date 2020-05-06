package com.thesis.neptun.model;

import com.thesis.neptun.main.MainWindow;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NoResultException;


@Entity
public class Student extends User {

  private transient String grade = "";

  @ManyToMany(mappedBy = "students")
  private List<Course> courses = new ArrayList<>();

  public Student() {
  }

  public Student(String code, byte[] password, byte[] salt, String name, String email) {
    super(password, salt, code, name, email);
  }

  public List<Course> getCourses() {
    return courses;
  }

  public void addCourses(Course... courses) {
    for (Course course : courses) {
      this.courses.add(course);
      course.getStudents().add(this);
    }
  }

  @Override
  public String toString() {
    return "Student [name=" + getName() + ", email=" + getEmail() + "]";
  }

  public void setGradeDb(String courseCode) {
    try {
      this.grade =
          (String)
              MainWindow.entityManager
                  .createNativeQuery(
                      "select grade from Result where coursecode=\""
                          + courseCode
                          + "\" and studentcode=\""
                          + getCode()
                          + "\"")
                  .getSingleResult();
    }catch (NoResultException e) {
      this.grade = "";
    }
  }

  public String getGrade() {
    return grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }
}
