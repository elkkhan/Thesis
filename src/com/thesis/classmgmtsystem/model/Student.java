package com.thesis.classmgmtsystem.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;


@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

  @JoinTable(name = "course_student",
      joinColumns = @JoinColumn(name = "student_id"),
      inverseJoinColumns = @JoinColumn(name = "course_id"))
  @ManyToMany
  private Set<Course> courses = new HashSet<>();
  @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
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
}
