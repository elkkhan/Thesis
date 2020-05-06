package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Student;
import com.thesis.neptun.model.User;
import java.net.URL;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

@SuppressWarnings(value = "unchecked")
public class TakeNewCourseWindowController implements Initializable {

  private User loggedInUser = MainWindowController.getLoggedInUser();
  @FXML
  private TableView<Course> tableView;
  @FXML
  private TableColumn<Course, String> code;
  @FXML
  private TableColumn<Course, String> name;
  @FXML
  private TableColumn<Course, Integer> credits;
  @FXML
  private TableColumn<Course, String> startTime;

  static ObservableList<Course> getCourses(String query) {
    ObservableList<Course> courses = FXCollections.observableArrayList();
    List<Course> courseList =
        MainWindow.entityManager.createNativeQuery(query, Course.class).getResultList();
    for (Course x : courseList) {
      courses.add(x);
    }
    return courses;
  }

  private ObservableList<Course> getCourseList() {
    String query =
        "select * from course where id not in (select courses_id from course_student where students_id"
            + " = "
            + loggedInUser.getId()
            + ")";
    return getCourses(query);
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    code.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
    name.setCellValueFactory(new PropertyValueFactory<>("name"));
    credits.setCellValueFactory(new PropertyValueFactory<>("credit"));
    startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
    tableView.setItems(getCourseList());
    tableView.getColumns().clear();
    tableView.getColumns().addAll(code, name, credits, startTime);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  @FXML
  public void handleTakeNewCourseButtonAction(ActionEvent event) {
    try {
      Course selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NullPointerException();
      }
      Alert alert =
          new Alert(
              AlertType.CONFIRMATION,
              "Take " + selectedCourse.getName() + " ?",
              ButtonType.NO,
              ButtonType.YES);
      alert.showAndWait();
      if (alert.getResult() == ButtonType.YES) {
        Student student = (Student) loggedInUser;
        MainWindow.entityManager.getTransaction().begin();
        student.getCourses().add(selectedCourse);
        selectedCourse.getStudents().add(student);
        MainWindow.entityManager.getTransaction().commit();
        MainWindow.displayMessage(
            "Neptun System", "Subject " + selectedCourse.getName() + " successfuly taken" + ".");
        ((Stage) tableView.getScene().getWindow()).close();
        MainWindow.loadWindow("/StudentPanel.fxml", "Student Panel");
      }
    } catch (NullPointerException e) {
      MainWindow.displayMessage("Neptun System", "Please select a subject!");
    }
  }

  @FXML
  public void handleBackButtonAction(ActionEvent event) {
    ((Stage) tableView.getScene().getWindow()).close();
    MainWindow.loadWindow("/StudentPanel.fxml", "Student Panel");
  }
}
