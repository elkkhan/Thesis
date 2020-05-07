package com.thesis.neptun.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;


@Entity
public class Student extends User {

  private transient String grade = "";
  @ManyToMany(mappedBy = "students")
  private Set<Course> courses = new HashSet<>();
  @OneToMany(mappedBy = "student",cascade = CascadeType.ALL)
  private List<AttendanceLog> attendanceLogs;
  @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
  private List<Result> results;

  public Student() {
  }

  public Student(String code, byte[] password, byte[] salt, String name, String email) {
    super(password, salt, code, name, email);
  }

  public List<Result> getResults() {
    return results;
  }

  public void setResults(List<Result> result) {
    this.results = result;
  }

  public List<AttendanceLog> getAttendanceLogs() {
    return attendanceLogs;
  }

  public void setAttendanceLogs(List<AttendanceLog> attendanceLogs) {
    this.attendanceLogs = attendanceLogs;
  }

  public Set<Course> getCourses() {
    return courses;
  }

  public void setCourses(Set<Course> courses) {
    this.courses = courses;
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

  public String getGrade() {
    return grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }


}
