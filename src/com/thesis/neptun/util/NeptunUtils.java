package com.thesis.neptun.util;

import com.thesis.neptun.exception.RegistrationException;
import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.AttendanceLog;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Message;
import com.thesis.neptun.model.Result;
import com.thesis.neptun.model.Student;
import com.thesis.neptun.model.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javax.persistence.EntityManager;

public class NeptunUtils {

  private static EntityManager em = MainWindow.entityManager;

  public static long countUnreadEmails(User loggedInUser) {
    em.refresh(loggedInUser);
    long count = 0;
    for (Message message : loggedInUser.getInbound()) {
      if (!message.isRead()) {
        count++;
      }
    }
    return count;
  }

  public static void loadWindow(String fxmlPath, String title) {
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
      Stage stage = new Stage();
      Parent root = FXMLLoader.load(MainWindow.class.getResource(fxmlPath));
      Scene scene = new Scene(root);
      stage.setTitle(title);
      stage.setScene(scene);
      stage.setResizable(false);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static double getAttendancePercentage(Course course, Student student) {
    em.refresh(course);
    em.refresh(student);
    int totalClasses = course.getClassLogs().size();
    int totalAttendances = 0;
    for (AttendanceLog attendanceLog : student.getAttendanceLogs()) {
      if (course.getClassLogs().contains(attendanceLog.getClassLog())) {
        totalAttendances++;
      }
    }
    return Math.round((double) totalAttendances / (double) totalClasses * 100);
  }

  public static Result getCourseResult(Student student, Course course) {
    em.refresh(student);
    em.refresh(course);
    for (Result result : student.getResults()) {
      if (result.getCourse().equals(course)) {
        return result;
      }
    }
    return null;
  }


  public static boolean isValidPassword(String password) {
    if (password.length() < 8) {
      return false;
    }
    Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(password);
    if (!m.find()) {
      return false;
    }
    if (password.equals(password.toLowerCase()) || password.equals(password.toUpperCase())) {
      return false;
    }
    p = Pattern.compile("([0-9])");
    m = p.matcher(password);
    return m.find();
  }

  public static boolean isValidNeptunCode(String neptun) {
    if (neptun.length() != 6) {
      return false;
    }
    Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(neptun);
    if (m.find()) {
      return false;
    }
    p = Pattern.compile("([0-9])");
    m = p.matcher(neptun);
    return m.find();
  }

  public static boolean isNeptunUnique(EntityManager em, String neptun) {
    String neptunStudent = "\"" + neptun.toUpperCase() + "\"";
    String neptunTeacher = "\"" + neptun.toUpperCase() + "\"";

    String sameNeptunStudents = "select count(code) from student where code=" + neptunStudent;
    String sameNeptunTeachers = "select count(code) from teacher where code=" + neptunTeacher;
    long sameNeptunStudentsCount, sameNeptunTeachersCount;
    sameNeptunStudentsCount = (long) em.createNativeQuery(sameNeptunStudents).getSingleResult();
    sameNeptunTeachersCount = (long) em.createNativeQuery(sameNeptunTeachers).getSingleResult();
    return sameNeptunStudentsCount == 0 && sameNeptunTeachersCount == 0;
  }

  public static boolean isEmailUnique(EntityManager em, String email) {
    String emailStudent = "\"" + email.toUpperCase() + "\"";
    String emailTeacher = "\"" + email.toUpperCase() + "\"";

    String sameEmailStudents = "select count(email) from student where email=" + emailStudent;
    String sameEmailTeachers = "select count(email) from teacher where email=" + emailTeacher;
    long sameEmailStudentsCount, sameEmailTeachersCount;
    sameEmailStudentsCount = (long) em.createNativeQuery(sameEmailStudents).getSingleResult();
    sameEmailTeachersCount = (long) em.createNativeQuery(sameEmailTeachers).getSingleResult();
    return sameEmailStudentsCount == 0 && sameEmailTeachersCount == 0;
  }

  public static void displayMessage(String title, String context) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText("Neptun System");
    alert.setContentText(context);
    alert.showAndWait();
  }

  public static boolean isInvalidCourseStartTime(String start_time) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    dateFormat.setLenient(false);
    try {
      dateFormat.parse(start_time.trim());
    } catch (ParseException pe) {
      return true;
    }
    return false;
  }

  public static boolean isArgumentListEmptyStrings(String... a) {
    boolean isEmpty = false;
    for (String string : a) {
      if (string.isEmpty()) {
        isEmpty = true;
        break;
      }
    }
    return isEmpty;
  }

  public static boolean isRegistrationDataValid(EntityManager em, String neptun, char[] password, String email)
      throws RegistrationException {
    if (!NeptunUtils.isValidNeptunCode(neptun)) {
      throw new RegistrationException(
          "Neptun code should be 6 characters long, contain at least 1 digit and not contain any special characters. \nPlease re-enter.");
    } else if (!NeptunUtils.isValidPassword(new String(password))) {
      throw new RegistrationException(
          "Password should be at least 8 characters long, contain at least 1 "
              + "digit, lower and upper-case letter.\nPlease re-enter.");
    } else if (!NeptunUtils.isNeptunUnique(em, neptun)) {
      throw new RegistrationException("User with Neptun code " + neptun + " already exists.");
    } else if (!NeptunUtils.isEmailUnique(em, email)) {
      throw new RegistrationException("User with email " + email + " already exists.");
    } else {
      return true;
    }
  }

}
