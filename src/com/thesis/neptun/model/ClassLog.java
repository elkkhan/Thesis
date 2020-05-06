package com.thesis.neptun.model;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Cacheable(false)
public class ClassLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String courseCode;
  private String date;
  private boolean attendanceWindowClosed;

  public ClassLog() {
  }

  public ClassLog(String courseCode, String date, boolean attendanceWindowClosed) {
    this.courseCode = courseCode;
    this.date = date;
    this.attendanceWindowClosed = attendanceWindowClosed;
  }

  @Override
  public String toString() {
    return "ClassLog{" +
        "id=" + id +
        ", courseCode='" + courseCode + '\'' +
        ", date=" + date +
        ", attendanceWindowClosed=" + attendanceWindowClosed +
        '}';
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean isAttendanceWindowClosed() {
    return attendanceWindowClosed;
  }

  public void setAttendanceWindowClosed(boolean bool) {
    this.attendanceWindowClosed = bool;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public void setCourseCode(String courseCode) {
    this.courseCode = courseCode;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }
}
