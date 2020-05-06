package com.thesis.neptun.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Result {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String studentCode, courseCode;
  private String grade = "";
  private transient String name;

  public Result() {
  }

  public Result(String studentCode, String courseCode, String grade) {
    this.studentCode = studentCode;
    this.courseCode = courseCode;
    this.grade = grade;
  }

  public Result(String studentCode, String courseCode, int grade) {
    this.studentCode = studentCode;
    this.courseCode = courseCode;
    this.grade = String.valueOf(grade);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getStudentCode() {
    return studentCode;
  }

  public void setStudentCode(String studentCode) {
    this.studentCode = studentCode;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public void setCourseCode(String courseCode) {
    this.courseCode = courseCode;
  }

  public String getGrade() {
    return grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }
}
