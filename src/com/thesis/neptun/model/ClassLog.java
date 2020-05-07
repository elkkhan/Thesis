package com.thesis.neptun.model;

import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Cacheable(false)
public class ClassLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @ManyToOne
  private Course course;
  @OneToMany(mappedBy = "classLog",cascade = CascadeType.ALL)
  private List<AttendanceLog> attendanceLogs;
  private String date;
  private boolean attendanceWindowClosed;

  public ClassLog() {
  }

  public ClassLog(Course course, String date, boolean attendanceWindowClosed) {
    this.course = course;
    this.date = date;
    this.attendanceWindowClosed = attendanceWindowClosed;
  }

  @Override
  public String toString() {
    return "ClassLog{" +
        "id=" + id +
        ", courseCode='" + course.getCourseCode() + '\'' +
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

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public List<AttendanceLog> getAttendanceLogs() {
    return attendanceLogs;
  }

  public void setAttendanceLogs(List<AttendanceLog> attendanceLogs) {
    this.attendanceLogs = attendanceLogs;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }
}
