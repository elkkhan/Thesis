package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Result;
import com.thesis.neptun.model.Student;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.persistence.NoResultException;

public class StudentPanelController implements Initializable {

  private static Course selectedCourse;
  private Student loggedInUser;
  @FXML
  private TableView<Course> tableView;
  @FXML
  private TableColumn<Course, String> subjCode;
  @FXML
  private TableColumn<Course, String> subjName;
  @FXML
  private TableColumn<Course, Integer> subjCredits;
  @FXML
  private TableColumn<Course, String> subjStartTime;
  @FXML
  private TextField credits;
  @FXML
  private Label inboxCounter;

  public static Course getSelectedCourse() {
    return selectedCourse;
  }

  @SuppressWarnings("unchecked")
  private ObservableList<Course> getCourseList() {
    String query =
        "select * from course where id in (select courses_id from course_student where students_id = "
            + loggedInUser.getId()
            + ")";
    return TakeNewCourseWindowController.getCourses(query);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    loggedInUser =
        MainWindow.entityManager.find(
            Student.class, MainWindowController.getLoggedInUser().getId());
    inboxCounter.setVisible(false);
    credits.setDisable(true);
    subjCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
    subjName.setCellValueFactory(new PropertyValueFactory<>("name"));
    subjCredits.setCellValueFactory(new PropertyValueFactory<>("credit"));
    subjStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
    tableView.setItems(getCourseList());
    tableView.getColumns().clear();
    tableView.getColumns().addAll(subjCode, subjName, subjCredits, subjStartTime);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    int cred = 0;
    for (Course x : getCourseList()) {
      cred += x.getCredit();
    }
    credits.setText("Total credits: " + cred);

    long count =
        (long)
            MainWindow.entityManager
                .createNativeQuery(
                    "select count(*) from message where receiverEmail = \""
                        + loggedInUser.getEmail()
                        + "\" and isread = 0")
                .getSingleResult();
    if (count > 0) {
      inboxCounter.setVisible(true);
      inboxCounter.setText(String.valueOf(count));
    }
  }

  @FXML
  public void handleCheckGradesButtonAction(ActionEvent event) {
    MainWindow.loadWindow("/CheckGradesWindow.fxml", "Grades Panel");
    ((Stage) tableView.getScene().getWindow()).close();
  }

  @FXML
  public void handleDropButtonAction(ActionEvent event) {
    try {
      Course selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NullPointerException();
      }
      Alert alert =
          new Alert(
              AlertType.CONFIRMATION,
              "Drop " + selectedCourse.getName() + " ?",
              ButtonType.NO,
              ButtonType.YES);
      alert.showAndWait();
      if (alert.getResult() == ButtonType.YES) {
        Student student = (Student) loggedInUser;

        MainWindow.entityManager.getTransaction().begin();
        student.getCourses().remove(selectedCourse);
        selectedCourse.getStudents().remove(student);
        try {
          String findResult =
              "select * from Result where coursecode=\""
                  + selectedCourse.getCourseCode()
                  + "\" and "
                  + "studentcode=\""
                  + student.getCode()
                  + "\"";
          Result resultToDelete =
              (Result)
                  MainWindow.entityManager
                      .createNativeQuery(findResult, Result.class)
                      .getSingleResult();
          MainWindow.entityManager.remove(resultToDelete);
        } catch (NoResultException e) {

        }
        MainWindow.entityManager.getTransaction().commit();

        MainWindow.displayMessage(
            "Neptun System", "Subject " + selectedCourse.getName() + " successfuly " + "dropped.");
        ((Stage) tableView.getScene().getWindow()).close();
        MainWindow.loadWindow("/StudentPanel.fxml", "Student Panel");
      }
    } catch (NullPointerException e) {
      MainWindow.displayMessage("Neptun System", "Please select a subject!");
    }
  }

  @FXML
  public void handleTakeButtonAction(ActionEvent event) {
    ((Stage) tableView.getScene().getWindow()).close();
    MainWindow.loadWindow("/TakeNewCourseWindow.fxml", "New Course");
  }

  @FXML
  public void handleLogOutButtonAction(ActionEvent event) {
    MainWindowController.resetLoggedInUser();
    ((Stage) tableView.getScene().getWindow()).close();
    MainWindow.loadWindow("/MainWindow.fxml", "Main");
  }

  @FXML
  public void handleInboxButtonAction(ActionEvent event) {
    MainWindow.loadWindow("/InboxWindow.fxml", "Inbox");
    ((Stage) credits.getScene().getWindow()).close();
  }

  @FXML
  public void handleAttendanceButtonAction(ActionEvent event) {
    try {
      selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NullPointerException();
      }
      MainWindow.loadWindow("/StudentAttendance.fxml", "Attendance");
      ((Stage) credits.getScene().getWindow()).close();
    } catch (NullPointerException e) {
      MainWindow.displayMessage("Neptun System", "Please select a subject!");
    }
  }

}
