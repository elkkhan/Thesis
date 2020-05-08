package com.thesis.neptun.controller;

import com.thesis.neptun.exception.InvalidCourseException;
import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Teacher;
import com.thesis.neptun.model.User;
import com.thesis.neptun.util.NeptunUtils;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.persistence.EntityManager;

public class CreateCourseController implements Initializable {

  private EntityManager em = MainWindow.entityManager;
  private User loggedInUser = MainWindowController.getLoggedInUser();
  @FXML
  private TextField name, code, start_time, credits, timeoutMinutes;

  public static Course constructCourse(EntityManager em, String name, String code, Teacher teacher,
      String credits,
      String start_time, String timeoutMinutes, boolean modifying) throws InvalidCourseException {

    try {
      String countQuery =
          "select count(coursecode) from course where coursecode=\"" + code + "\"";
      long subjCount =
          (long) em.createNativeQuery(countQuery).getSingleResult();
      if (NeptunUtils.isArgumentListEmptyStrings(name, code, credits, start_time)) {
        throw new InvalidCourseException("Please fill out all the fields.");
      } else if (Integer.parseInt(credits) < 0
          || Integer.parseInt(credits) > 8) {
        throw new InvalidCourseException("Number of credits should be between 0 and 8 inclusive.");
      } else if (NeptunUtils.isInvalidCourseStartTime(start_time) || start_time.length() != 5) {
        throw new InvalidCourseException(
            "Invalid time format.\nStart time must be in hh:mm format.");
      } else if (subjCount > 0 && !modifying) {
        throw new InvalidCourseException(
            "A subject with course code " + code + " alerady " + "exists.");
      } else if (Integer.parseInt(timeoutMinutes) > 120) {
        throw new InvalidCourseException("Timeout can't exceed 120 minutes.");
      }
    } catch (NumberFormatException e) {
      throw new InvalidCourseException("Invalid input for credits or timeout field.");
    }

    return new Course(
        name,
        code,
        teacher,
        Integer.parseInt(credits),
        start_time, Integer.parseInt(timeoutMinutes));
  }


  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));

  }

  private void createCourse(Course course) {
    MainWindow.entityManager.getTransaction().begin();
    course.getTeacher().getCourses().add(course);
    MainWindow.entityManager.persist(course);
    MainWindow.entityManager.getTransaction().commit();
  }

  @FXML
  public void handleCreateCourseButtonAction() {
    try {
      Course course = constructCourse(em, name.getText(), code.getText(), (Teacher) loggedInUser,
          credits.getText(), start_time.getText(), timeoutMinutes.getText(),false);
      createCourse(course);
    } catch (InvalidCourseException e) {
      NeptunUtils.displayMessage("Invalid course", e.getMessage());
    }
    NeptunUtils.displayMessage(
        "Neptun System", "Course " + name.getText() + " successfuly created.");
    ((Stage) name.getScene().getWindow()).close();
    NeptunUtils.loadWindow("/TeacherPanel.fxml", "Teacher Panel");
  }

  @FXML
  public void handleBackButtonAction() {
    NeptunUtils.loadWindow("/TeacherPanel.fxml", "Teacher Panel");
    ((Stage) name.getScene().getWindow()).close();
  }
}
