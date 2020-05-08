package com.thesis.neptun.controller;

import com.thesis.neptun.exception.NoRowSelectedException;
import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Student;
import com.thesis.neptun.model.User;
import com.thesis.neptun.util.NeptunUtils;
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
import javax.persistence.EntityManager;

@SuppressWarnings(value = "unchecked")
public class TakeNewCourseWindowController implements Initializable {

  private EntityManager em = MainWindow.entityManager;
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

  private ObservableList<Course> getNotTakenCourseList(EntityManager em, Student student) {
    String query =
        "select * from course where id not in (select course_id from course_student where student_id"
            + " = "
            + student.getId()
            + ")";
    ObservableList<Course> courses = FXCollections.observableArrayList();
    List<Course> courseList =
        em.createNativeQuery(query, Course.class).getResultList();
    for (Course x : courseList) {
      courses.add(x);
    }
    return courses;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    code.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
    name.setCellValueFactory(new PropertyValueFactory<>("name"));
    credits.setCellValueFactory(new PropertyValueFactory<>("credit"));
    startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
    tableView.setItems(getNotTakenCourseList(em, (Student) loggedInUser));
    tableView.getColumns().clear();
    tableView.getColumns().addAll(code, name, credits, startTime);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  @FXML
  public void handleTakeNewCourseButtonAction(ActionEvent event) {
    try {
      Course selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NoRowSelectedException("Please select a subject!");
      }
      Alert alert =
          new Alert(
              AlertType.CONFIRMATION,
              "Take " + selectedCourse.getName() + " ?",
              ButtonType.NO,
              ButtonType.YES);
      alert.showAndWait();
      if (alert.getResult() == ButtonType.YES) {
        takeCourse(em, (Student) loggedInUser, selectedCourse);
        NeptunUtils.displayMessage(
            "Neptun System", "Subject " + selectedCourse.getName() + " successfuly taken" + ".");
        ((Stage) tableView.getScene().getWindow()).close();
        NeptunUtils.loadWindow("/StudentPanel.fxml", "Student Panel");
      }
    } catch (NoRowSelectedException e) {
      NeptunUtils.displayMessage("Neptun System", e.getMessage());
    }
  }

  private void takeCourse(EntityManager em, Student student, Course course) {
    em.getTransaction().begin();
    student.getCourses().add(course);
    course.getStudents().add(student);
    em.getTransaction().commit();
  }

  @FXML
  public void handleBackButtonAction(ActionEvent event) {
    ((Stage) tableView.getScene().getWindow()).close();
    NeptunUtils.loadWindow("/StudentPanel.fxml", "Student Panel");
  }
}
