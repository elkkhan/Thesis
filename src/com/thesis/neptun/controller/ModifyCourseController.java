package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Course;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifyCourseController implements Initializable {

  @FXML
  private TextField nameModify;
  @FXML
  private TextField codeModify;
  @FXML
  private TextField creditModify;
  @FXML
  private TextField startTimeModify;
  @FXML
  private TextField timeoutMinutes;

  private Course selectedCourse;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    selectedCourse = TeacherPanelController.getSelectedCourse();

    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    nameModify.setText(selectedCourse.getName());
    codeModify.setText(selectedCourse.getCourseCode());
    creditModify.setText(String.valueOf(selectedCourse.getCredit()));
    startTimeModify.setText(selectedCourse.getStartTime());
    timeoutMinutes.setText(String.valueOf(selectedCourse.getTimeoutMinutes()));
  }

  public void handleSaveButtonAction() {
    String countQuery =
        "select count(coursecode) from course where coursecode=\"" + codeModify.getText() + "\"";
    long subjCount =
        (long) MainWindow.entityManager.createNativeQuery(countQuery).getSingleResult();

    if (CreateCourseController.isInvalidTime(startTimeModify.getText())
        || startTimeModify.getText().length() != 5) {
      MainWindow.displayMessage(
          "Neptun System", "Invalid time format.\nStart time must be in hh:mm format.");
    } else if (Integer.parseInt(creditModify.getText()) < 0
        || Integer.parseInt(creditModify.getText()) > 8) {
      MainWindow.displayMessage(
          "Neptun System", "Number of credits should be between 0 and 8 inclusive.");
    } else if (subjCount > 0 && !(codeModify.getText().equals(selectedCourse.getCourseCode()))) {
      MainWindow.displayMessage(
          "Neptun System",
          "A subject with course code " + codeModify.getText() + " " + "alerady exists.");
    } else {
      String findCourse =
          "select id from course where coursecode=\"" + selectedCourse.getCourseCode() + "\"";
      int courseId = (int) MainWindow.entityManager.createNativeQuery(findCourse).getSingleResult();
      Course modifiedCourse = MainWindow.entityManager.find(Course.class, courseId);
      MainWindow.entityManager.getTransaction().begin();
      modifiedCourse.setCourseCode(codeModify.getText());
      modifiedCourse.setName(nameModify.getText());
      modifiedCourse.setCredit(Integer.parseInt(creditModify.getText()));
      modifiedCourse.setStartTime(startTimeModify.getText());
      modifiedCourse.setTimeoutMinutes(Integer.parseInt(timeoutMinutes.getText()));
      MainWindow.entityManager.getTransaction().commit();
      MainWindow.displayMessage("Neptun System", "Changes saved.");
      ((Stage) nameModify.getScene().getWindow()).close();
      MainWindow.loadWindow("/TeacherPanel.fxml", "Teacher Panel");
    }
  }
}
