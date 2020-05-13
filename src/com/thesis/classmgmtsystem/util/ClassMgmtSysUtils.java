package com.thesis.classmgmtsystem.util;

import com.thesis.classmgmtsystem.controller.common.MainWindowController;
import com.thesis.classmgmtsystem.exception.RegistrationException;
import com.thesis.classmgmtsystem.main.MainWindow;
import com.thesis.classmgmtsystem.model.AttendanceLog;
import com.thesis.classmgmtsystem.model.Course;
import com.thesis.classmgmtsystem.model.Message;
import com.thesis.classmgmtsystem.model.Result;
import com.thesis.classmgmtsystem.model.Student;
import com.thesis.classmgmtsystem.model.User;
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

public class ClassMgmtSysUtils {

  private static EntityManager em = MainWindow.entityManager;

  public static void setEntityManager(EntityManager entityManager) {
    em = entityManager;
  }

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

  public static boolean isValidVeneraCode(String venera) {
    if (venera.length() != 6) {
      return false;
    }
    Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(venera);
    if (m.find()) {
      return false;
    }
    p = Pattern.compile("([0-9])");
    m = p.matcher(venera);
    return m.find();
  }

  public static boolean isVeneraUnique(EntityManager em, String venera) {
    String sameVeneraUsersCountQuery = "select count(code) from user where code=\"" + venera + "\"";
    long sameVeneraUsersCount;
    sameVeneraUsersCount = (long) em.createNativeQuery(sameVeneraUsersCountQuery).getSingleResult();
    return sameVeneraUsersCount == 0;
  }

  public static boolean isEmailUnique(EntityManager em, String email) {
    String sameEmailUsersQuery = "select count(email) from user where email=\"" + email + "\"";
    long sameEmailUsersCount;
    sameEmailUsersCount = (long) em.createNativeQuery(sameEmailUsersQuery).getSingleResult();
    return sameEmailUsersCount == 0;
  }

  public static void displayMessage(String context) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Alert");
    alert.setHeaderText(MainWindowController.alertTitle);
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

  public static boolean isRegistrationDataValid(EntityManager em, String venera, char[] password,
      String email)
      throws RegistrationException {
    if (!ClassMgmtSysUtils.isValidVeneraCode(venera)) {
      throw new RegistrationException(
          "Venera code should be 6 characters long, contain at least 1 digit and not contain any special characters. \nPlease re-enter.");
    } else if (!ClassMgmtSysUtils.isValidPassword(new String(password))) {
      throw new RegistrationException(
          "Password should be at least 8 characters long, contain at least 1 "
              + "digit, lower and upper-case letter.\nPlease re-enter.");
    } else if (!ClassMgmtSysUtils.isVeneraUnique(em, venera)) {
      throw new RegistrationException("User with Venera code " + venera + " already exists.");
    } else if (!ClassMgmtSysUtils.isEmailUnique(em, email)) {
      throw new RegistrationException("User with email " + email + " already exists.");
    } else {
      return true;
    }
  }
}
