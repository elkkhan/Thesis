package com.thesis.neptun.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;
  private String courseCode;
  private int timeoutMinutes;
  @ManyToOne
  private Teacher teacher;
  @ManyToMany
  private List<Student> students = new ArrayList<>();
  private int credit;
  private String startTime;
  public Course() {
  }
  public Course(String name, String courseCode, Teacher teacher, int credit, String startTime, int timeoutMinutes) {
    super();
    this.name = name;
    this.courseCode = courseCode;
    this.teacher = teacher;
    this.credit = credit;
    this.startTime = startTime;
    this.timeoutMinutes = timeoutMinutes;
  }

  public int getTimeoutMinutes() {
    return timeoutMinutes;
  }

  public void setTimeoutMinutes(int timeoutMinutes) {
    this.timeoutMinutes = timeoutMinutes;
  }

  public List<Student> getStudents() {
    return students;
  }

  public void setStudents(List<Student> students) {
    this.students = students;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public void setCourseCode(String courseCode) {
    this.courseCode = courseCode;
  }

  public Teacher getTeacher() {
    return teacher;
  }

  public void setTeacher(Teacher teacher) {
    this.teacher = teacher;
  }

  public int getCredit() {
    return credit;
  }

  public void setCredit(int credit) {
    this.credit = credit;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  @Override
  public String toString() {
    return "Course [id="
        + id
        + ", name="
        + name
        + ", courseCode="
        + courseCode
        + ", teacher="
        + teacher
        + ", credit="
        + credit
        + ", startTime="
        + startTime
        + "]";
  }
}
