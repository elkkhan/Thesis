package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Message;
import com.thesis.neptun.model.Result;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.persistence.NoResultException;

public class ModifyGradeController implements Initializable {

  @FXML
  private TextField studentName;
  @FXML
  private TextField grade;

  private Result selectedResult;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    selectedResult = StudentListController.getSelectedResult();
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    studentName.setDisable(true);
    studentName.setText(selectedResult.getName());
    grade.setText(selectedResult.getGrade());
  }

  @FXML
  public void handleSaveGradeButtonAction() {
    try {
      if (Integer.parseInt(grade.getText()) < 1 || Integer.parseInt(grade.getText()) > 5) {
        MainWindow.displayMessage("Neptun System", "Grade must be between 1 and 5 inclusive.");
        grade.clear();
        return;
      }
    } catch (NumberFormatException e) {
      MainWindow.displayMessage("Neptun System", "Invalid input, please enter a valid grade.");
      grade.clear();
      return;
    }

    MainWindow.entityManager.getTransaction().begin();
    String subjectName =
        (String)
            MainWindow.entityManager
                .createNativeQuery(
                    "select name from course where coursecode = \""
                        + selectedResult.getCourseCode()
                        + "\"")
                .getSingleResult();
    String senderEmail = MainWindowController.getLoggedInUser().getEmail();
    String receiverEmail =
        (String)
            MainWindow.entityManager
                .createNativeQuery(
                    "select email from student where code = \""
                        + selectedResult.getStudentCode()
                        + "\"")
                .getSingleResult();
    String messageText =
        "Grade '"
            + grade.getText()
            + "' has been submitted by "
            + MainWindowController.getLoggedInUser().getName()
            + " for subject "
            + subjectName
            + "\n\n\n"
            + "This is an automatically generated mail, please do not reply to it.";
    String subject = "Grade changed for " + subjectName;
    Message message = new Message(senderEmail, receiverEmail, messageText, subject);
    MainWindow.entityManager.persist(message);

    String query =
        "select id from Result where studentcode = \""
            + selectedResult.getStudentCode()
            + "\" and "
            + "coursecode = \""
            + selectedResult.getCourseCode()
            + "\"";
    Result result;
    try {
      int id = (int) MainWindow.entityManager.createNativeQuery(query).getSingleResult();
      result = MainWindow.entityManager.find(Result.class, id);
      result.setGrade(grade.getText());
    } catch (NoResultException e) {
      result =
          new Result(
              selectedResult.getStudentCode(), selectedResult.getCourseCode(), grade.getText());
    }
    MainWindow.entityManager.persist(result);
    MainWindow.entityManager.getTransaction().commit();
    MainWindow.displayMessage("Neptun System", "Grade changed to " + grade.getText());
    ((Stage) grade.getScene().getWindow()).close();
    MainWindow.loadWindow("/StudentList.fxml", "Student count");
  }
}
