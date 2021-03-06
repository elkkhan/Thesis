package com.thesis.classmgmtsystem.controller.student;

import com.thesis.classmgmtsystem.controller.common.MainWindowController;
import com.thesis.classmgmtsystem.exception.NoRowSelectedException;
import com.thesis.classmgmtsystem.model.Student;
import com.thesis.classmgmtsystem.model.User;
import com.thesis.classmgmtsystem.main.MainWindow;
import com.thesis.classmgmtsystem.model.Course;
import com.thesis.classmgmtsystem.model.Result;
import com.thesis.classmgmtsystem.util.ClassMgmtSysUtils;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.persistence.EntityManager;

public class StudentPanelController implements Initializable {

  private static Course selectedCourse;
  private static EntityManager em = MainWindow.entityManager;
  private User loggedInUser = MainWindowController.getLoggedInUser();
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
  private ObservableList<Course> getCourseList(Student student) {
    List<Course> courseList = new ArrayList<>();
    courseList.addAll(student.getCourses());
    ObservableList<Course> courses = FXCollections.observableArrayList();
    for (Course x : courseList) {
      em.refresh(x);
      courses.add(x);
    }
    return courses;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    em.refresh(loggedInUser);
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    ObservableList<Course> courseList = getCourseList((Student) loggedInUser);
    inboxCounter.setVisible(false);
    credits.setDisable(true);
    subjCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
    subjName.setCellValueFactory(new PropertyValueFactory<>("name"));
    subjCredits.setCellValueFactory(new PropertyValueFactory<>("credit"));
    subjStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
    tableView.setItems(courseList);
    tableView.getColumns().clear();
    tableView.getColumns().addAll(subjCode, subjName, subjCredits, subjStartTime);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    int cred = 0;
    for (Course x : courseList) {
      cred += x.getCredit();
    }
    credits.setText("Total credits: " + cred);

    long count = ClassMgmtSysUtils.countUnreadEmails(loggedInUser);
    if (count > 0) {
      inboxCounter.setVisible(true);
      inboxCounter.setText(String.valueOf(count));
    }
  }

  @FXML
  public void handleCheckGradesButtonAction(ActionEvent event) {
    ClassMgmtSysUtils.loadWindow("/fxml/student/CheckGradesWindow.fxml", "Grades Panel");
    ((Stage) tableView.getScene().getWindow()).close();
  }

  @FXML
  public void handleDropButtonAction(ActionEvent event) {
    try {
      Course selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NoRowSelectedException("Please select a subject!");
      }
      Alert alert =
          new Alert(
              AlertType.CONFIRMATION,
              "Drop " + selectedCourse.getName() + " ?",
              ButtonType.NO,
              ButtonType.YES);
      alert.showAndWait();
      if (alert.getResult() == ButtonType.YES) {
        dropCourse(em, (Student) loggedInUser, selectedCourse);
        ClassMgmtSysUtils.displayMessage(
            "Subject " + selectedCourse.getName() + " successfuly " + "dropped.");
        ((Stage) tableView.getScene().getWindow()).close();
        ClassMgmtSysUtils.loadWindow("/fxml/student/StudentPanel.fxml", "Student Panel");
      }
    } catch (NoRowSelectedException e) {
      ClassMgmtSysUtils.displayMessage(e.getMessage());
    }
  }

  private void dropCourse(EntityManager em, Student student, Course course) {
    em.getTransaction().begin();
    student.getCourses().remove(course);
    course.getStudents().remove(student);
    for (Result result : student.getResults()) {
      if (course.getResults().contains(result)) {
        student.getResults().remove(result);
        course.getResults().remove(result);
        em.remove(result);
        break;
      }
    }
    em.getTransaction().commit();
  }

  @FXML
  public void handleTakeButtonAction(ActionEvent event) {
    ((Stage) tableView.getScene().getWindow()).close();
    ClassMgmtSysUtils.loadWindow("/fxml/student/TakeNewCourseWindow.fxml", "New Course");
  }

  @FXML
  public void handleLogOutButtonAction(ActionEvent event) {
    MainWindowController.resetLoggedInUser();
    ((Stage) tableView.getScene().getWindow()).close();
    ClassMgmtSysUtils.loadWindow("/fxml/common/MainWindow.fxml", "Main");
  }

  @FXML
  public void handleInboxButtonAction(ActionEvent event) {
    ClassMgmtSysUtils.loadWindow("/fxml/common/InboxWindow.fxml", "Inbox");
    ((Stage) credits.getScene().getWindow()).close();
  }

  @FXML
  public void handleAttendanceButtonAction(ActionEvent event) {
    try {
      selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NoRowSelectedException("Please select a subject!");
      }
      ClassMgmtSysUtils.loadWindow("/fxml/student/StudentAttendance.fxml", "Attendance");
      ((Stage) credits.getScene().getWindow()).close();
    } catch (NoRowSelectedException e) {
      ClassMgmtSysUtils.displayMessage(e.getMessage());
    }
  }

}
