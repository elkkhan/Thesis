package com.thesis.neptun.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class AttendanceLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  private ClassLog classLog;
  @ManyToOne
  private Student student;

  public AttendanceLog() {
  }

  public AttendanceLog(ClassLog cl, Student student) {
    this.classLog = cl;
    this.student = student;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
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
}
