package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Result;
import com.thesis.neptun.model.Teacher;
import com.thesis.neptun.model.User;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class TeacherPanelController extends MainWindowController implements Initializable {

  private static Course selectedCourse;
  private User loggedInUser = getLoggedInUser();
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
  private Button createCourse;
  @FXML
  private Label inboxCounter;

  public static Course getSelectedCourse() {
    return selectedCourse;
  }

  @SuppressWarnings("unchecked")
  private ObservableList<Course> getCourseList() {
    ObservableList<Course> courses = FXCollections.observableArrayList();
    List<Course> courseList =
        MainWindow.entityManager
            .createNativeQuery(
                "select * from course where teacher_id=" + loggedInUser.getId(), Course.class)
            .getResultList();
    for (Course x : courseList) {
      courses.add(x);
    }
    return courses;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    inboxCounter.setVisible(false);
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    subjCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
    subjName.setCellValueFactory(new PropertyValueFactory<>("name"));
    subjCredits.setCellValueFactory(new PropertyValueFactory<>("credit"));
    subjStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
    tableView.setItems(getCourseList());
    tableView.getColumns().clear();
    tableView.getColumns().addAll(subjCode, subjName, subjCredits, subjStartTime);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
  public void handleCreateCourseButtonAction(ActionEvent event) {
    ((Stage) createCourse.getScene().getWindow()).close();
    MainWindow.loadWindow("/CreateCourseWindow.fxml", "Course creator");
  }

  public void handleModifyCourseButtonAction(ActionEvent event) {
    try {
      selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NullPointerException();
      }
      ((Stage) createCourse.getScene().getWindow()).close();
      MainWindow.loadWindow("/ModifyCourseWindow.fxml", "Course modifier");
    } catch (NullPointerException e) {
      MainWindow.displayMessage("Neptun System", "Please select a subject!");
    }
  }

  @SuppressWarnings("unchecked")
  public void handleDeleteCourseButtonAction(ActionEvent event) {
    try {
      selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NullPointerException();
      }
      Alert alert =
          new Alert(
              AlertType.CONFIRMATION,
              "Delete " + selectedCourse.getName() + " ?",
              ButtonType.NO,
              ButtonType.YES);
      alert.showAndWait();

      if (alert.getResult() == ButtonType.YES) {
        String findCourse =
            "select id from course where coursecode=\"" + selectedCourse.getCourseCode() + "\"";
        int courseId =
            (int) MainWindow.entityManager.createNativeQuery(findCourse).getSingleResult();
        Course deletedCourse = MainWindow.entityManager.find(Course.class, courseId);
        MainWindow.entityManager.getTransaction().begin();
        Teacher teacher = (Teacher) loggedInUser;
        teacher.getCourses().remove(deletedCourse);
        MainWindow.entityManager.remove(deletedCourse);

        List<Result> resultsToDelete = new ArrayList<>();
        String findResult =
            "select * from Result where coursecode=\"" + selectedCourse.getCourseCode() + "\"";
        try {
          resultsToDelete =
              MainWindow.entityManager.createNativeQuery(findResult, Result.class).getResultList();
        } catch (Exception e) {

        }
        for (Result x : resultsToDelete) {
          MainWindow.entityManager.remove(x);
        }
        MainWindow.entityManager.getTransaction().commit();

        MainWindow.displayMessage(
            "Neptun System", "Course " + deletedCourse.getName() + " successfuly deleted" + ".");
        ((Stage) createCourse.getScene().getWindow()).close();
        MainWindow.loadWindow("/TeacherPanel.fxml", "Teacher Panel");
      }
    } catch (NullPointerException e) {
      MainWindow.displayMessage("Neptun System", "Please select a subject!");
    }
  }

  @FXML
  public void handleCheckStudentsButtonAction(ActionEvent event) {
    try {
      selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NullPointerException();
      }
      ((Stage) createCourse.getScene().getWindow()).close();
      MainWindow.loadWindow("/StudentList.fxml", "Student list");
    } catch (NullPointerException e) {
      MainWindow.displayMessage("Neptun System", "Please select a subject!");
    }
  }

  @FXML
  public void handleClassPageButtonAction(ActionEvent event) {
    try {
      selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NullPointerException();
      }
      ((Stage) createCourse.getScene().getWindow()).close();
      MainWindow.loadWindow("/ClassLogs.fxml", "Class Logs");
    } catch (NullPointerException e) {
      MainWindow.displayMessage("Neptun System", "Please select a subject!");
    }
  }

  @FXML
  public void handleLogOutButtonAction(ActionEvent event) {
    resetLoggedInUser();
    ((Stage) createCourse.getScene().getWindow()).close();
    MainWindow.loadWindow("/MainWindow.fxml", "sMain");
  }

  @FXML
  public void handleInboxButtonAction(ActionEvent event) {
    MainWindow.loadWindow("/InboxWindow.fxml", "Inbox");
    ((Stage) tableView.getScene().getWindow()).close();
  }
}
