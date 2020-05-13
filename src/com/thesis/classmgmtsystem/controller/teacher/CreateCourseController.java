package com.thesis.classmgmtsystem.controller.teacher;

import com.thesis.classmgmtsystem.controller.common.MainWindowController;
import com.thesis.classmgmtsystem.model.User;
import com.thesis.classmgmtsystem.exception.InvalidCourseException;
import com.thesis.classmgmtsystem.main.MainWindow;
import com.thesis.classmgmtsystem.model.Course;
import com.thesis.classmgmtsystem.model.Teacher;
import com.thesis.classmgmtsystem.util.ClassMgmtSysUtils;
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
      if (ClassMgmtSysUtils.isArgumentListEmptyStrings(name, code, credits, start_time)) {
        throw new InvalidCourseException("Please fill out all the fields.");
      } else if (Integer.parseInt(credits) < 0
          || Integer.parseInt(credits) > 8) {
        throw new InvalidCourseException("Number of credits should be between 0 and 8 inclusive.");
      } else if (ClassMgmtSysUtils.isInvalidCourseStartTime(start_time) || start_time.length() != 5) {
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

  private void createCourse(EntityManager em, Course course) {
    em.getTransaction().begin();
    course.getTeacher().getCourses().add(course);
    em.persist(course);
    em.getTransaction().commit();
  }

  @FXML
  public void handleCreateCourseButtonAction() {
    try {
      Course course = constructCourse(em, name.getText(), code.getText(), (Teacher) loggedInUser,
          credits.getText(), start_time.getText(), timeoutMinutes.getText(), false);
      createCourse(MainWindow.entityManager, course);
    } catch (InvalidCourseException e) {
      ClassMgmtSysUtils.displayMessage(e.getMessage());
    }
    ClassMgmtSysUtils.displayMessage(
        "Course " + name.getText() + " successfuly created.");
    ((Stage) name.getScene().getWindow()).close();
    ClassMgmtSysUtils.loadWindow("/fxml/teacher/TeacherPanel.fxml", "Teacher Panel");
  }

  @FXML
  public void handleBackButtonAction() {
    ClassMgmtSysUtils.loadWindow("/fxml/teacher/TeacherPanel.fxml", "Teacher Panel");
    ((Stage) name.getScene().getWindow()).close();
  }
}
