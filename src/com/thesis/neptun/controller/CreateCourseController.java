package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Teacher;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateCourseController implements Initializable {

  @FXML
  private TextField name, code, start_time, credits, timeoutMinutes;

  static boolean isInvalidTime(String start_time) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    dateFormat.setLenient(false);
    try {
      dateFormat.parse(start_time.trim());
    } catch (ParseException pe) {
      return true;
    }
    return false;
  }

  private static boolean checkEmpty(String a, String b, String c, String d) {
    boolean isOk = true;
    if (a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
      isOk = false;
    }
    return isOk;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
  }

  @FXML
  public void handleCreateCourseButtonAction() {
    String countQuery =
        "select count(coursecode) from course where coursecode=\"" + code.getText() + "\"";
    long subjCount =
        (long) MainWindow.entityManager.createNativeQuery(countQuery).getSingleResult();

    try {
      if (!checkEmpty(name.getText(), code.getText(), credits.getText(), start_time.getText())) {
        MainWindow.displayMessage("Neptun System", "Please fill out all the fields.");
      } else if (Integer.parseInt(credits.getText()) < 0
          || Integer.parseInt(credits.getText()) > 8) {
        MainWindow.displayMessage(
            "Neptun System", "Number of credits should be between 0 and 8 inclusive.");
        credits.clear();
      } else if (isInvalidTime(start_time.getText()) || start_time.getText().length() != 5) {
        MainWindow.displayMessage(
            "Neptun System", "Invalid time format.\nStart time must be in hh:mm format.");
        start_time.clear();
      } else if (subjCount > 0) {
        MainWindow.displayMessage(
            "Neptun System",
            "A subject with course code " + code.getText() + " alerady " + "exists.");
      } else {
        Teacher teacher = (Teacher) MainWindowController.getLoggedInUser();
        Course course =
            new Course(
                name.getText(),
                code.getText(),
                teacher,
                Integer.parseInt(credits.getText()),
                start_time.getText(), Integer.parseInt(timeoutMinutes.getText()));
        teacher.getCourses().add(course);
        MainWindow.entityManager.getTransaction().begin();
        MainWindow.entityManager.persist(course);
        MainWindow.entityManager.getTransaction().commit();

        MainWindow.displayMessage(
            "Neptun System", "Course " + name.getText() + " successfuly created.");
        ((Stage) name.getScene().getWindow()).close();
        MainWindow.loadWindow("/TeacherPanel.fxml", "Teacher Panel");
      }
    } catch (NumberFormatException e) {
      MainWindow.displayMessage("Neptun System", "Invalid input for credits field.");
      credits.clear();
    }
  }

  @FXML
  public void handleBackButtonAction() {
    MainWindow.loadWindow("/TeacherPanel.fxml", "Teacher Panel");
    ((Stage) name.getScene().getWindow()).close();
  }
}
