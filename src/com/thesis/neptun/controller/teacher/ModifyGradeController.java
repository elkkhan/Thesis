package com.thesis.neptun.controller.teacher;

import com.thesis.neptun.controller.common.ComposeMessageWindowController;
import com.thesis.neptun.controller.common.MainWindowController;
import com.thesis.neptun.exception.InvalidGradeException;
import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Result;
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

public class ModifyGradeController implements Initializable {

  private User teacher = MainWindowController.getLoggedInUser();
  private EntityManager em = MainWindow.entityManager;
  @FXML
  private TextField studentName;
  @FXML
  private TextField grade;

  private Result selectedResult;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    selectedResult = CourseResultListController.getSelectedResult();
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    studentName.setDisable(true);
    studentName.setText(selectedResult.getStudent().getName());
    grade.setText(selectedResult.getGrade());
  }

  private void changeGrade(EntityManager em, String grade, Result result)
      throws InvalidGradeException {
    try {
      if (Integer.parseInt(grade) < 1 || Integer.parseInt(grade) > 5) {
        throw new InvalidGradeException("Grade must be between 1 and 5 inclusive.");
      }
    } catch (NumberFormatException e) {
      throw new InvalidGradeException("Invalid input, please enter a valid grade.");
    }
    em.getTransaction().begin();
    result.setGrade(grade);
    if (!result.getStudent().getResults().contains(result)) {
      result.getStudent().getResults().add(result);
      result.getCourse().getResults().add(result);
      em.persist(result);
    }
    em.getTransaction().commit();
  }

  @FXML
  public void handleSaveGradeButtonAction() {
    if (!selectedResult.getGrade().equals("None")) {
      em.refresh(selectedResult);
    }
    try {
      changeGrade(em, grade.getText(), selectedResult);
      String messageText =
          "Grade '"
              + grade.getText()
              + "' has been submitted by "
              + selectedResult.getCourse().getTeacher().getName()
              + " for subject "
              + selectedResult.getCourse().getName()
              + "\n\n\n"
              + "This is an automatically generated mail, please do not reply to it.";
      String subject = "Grade changed for " + selectedResult.getCourse().getName();
      ComposeMessageWindowController
          .sendMessage(em, teacher, selectedResult.getStudent(), subject, messageText);

    } catch (InvalidGradeException e) {
      NeptunUtils.displayMessage("Neptun System", e.getMessage());
      grade.clear();
    }
    NeptunUtils.displayMessage("Neptun System", "Grade changed to " + grade.getText());
    ((Stage) grade.getScene().getWindow()).close();
    NeptunUtils.loadWindow("/fxml/teacher/StudentList.fxml", "Student count");
  }
}
