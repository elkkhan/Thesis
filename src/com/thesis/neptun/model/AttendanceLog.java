package com.thesis.neptun.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class AttendanceLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String courseCode;
  private String studentCode;
  @OneToOne
  private ClassLog classLog;
  public AttendanceLog() {
  }
  public AttendanceLog(ClassLog cl, String courseCode, String studentCode) {
    this.classLog = cl;
    this.courseCode = courseCode;
    this.studentCode = studentCode;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public ClassLog getClassLog() {
    return classLog;
  }

  public void setClassLog(ClassLog classLog) {
    this.classLog = classLog;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public void setCourseCode(String courseCode) {
    this.courseCode = courseCode;
  }

  public String getStudentCode() {
    return studentCode;
  }

  public void setStudentCode(String studentCode) {
    this.studentCode = studentCode;
  }
}
