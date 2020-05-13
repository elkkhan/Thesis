package com.thesis.classmgmtsystem.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Result {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @ManyToOne
  private Student student;
  @ManyToOne
  private Course course;
  private String grade = "";

  public Result() {
  }

  public Result(Student student, Course course, String grade) {
    this.student = student;
    this.course = course;
    this.grade = grade;
  }

  public Result(Student student, Course course, int grade) {
    this.student = student;
    this.course = course;
    this.grade = String.valueOf(grade);
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getGrade() {
    return grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }
}
