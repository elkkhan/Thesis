package com.thesis.classmgmtsystem.controller.teacher;

import com.thesis.classmgmtsystem.exception.NoRowSelectedException;
import com.thesis.classmgmtsystem.model.AttendanceLog;
import com.thesis.classmgmtsystem.model.ClassLog;
import com.thesis.classmgmtsystem.model.Student;
import com.thesis.classmgmtsystem.model.User;
import com.thesis.classmgmtsystem.controller.common.MainWindowController;
import com.thesis.classmgmtsystem.main.MainWindow;
import com.thesis.classmgmtsystem.model.Course;
import com.thesis.classmgmtsystem.model.Teacher;
import com.thesis.classmgmtsystem.util.ClassMgmtSysUtils;
import java.net.URL;
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
import javax.persistence.EntityManager;

public class TeacherPanelController extends MainWindowController implements Initializable {

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
  private Button createCourse;
  @FXML
  private Label inboxCounter;

  public static Course getSelectedCourse() {
    return selectedCourse;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    em.refresh(loggedInUser);
    inboxCounter.setVisible(false);
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    subjCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
    subjName.setCellValueFactory(new PropertyValueFactory<>("name"));
    subjCredits.setCellValueFactory(new PropertyValueFactory<>("credit"));
    subjStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
    ObservableList<Course> courses = FXCollections.observableArrayList();
    courses.addAll(((Teacher) loggedInUser).getCourses());
    tableView.setItems(courses);
    tableView.getColumns().clear();
    tableView.getColumns().addAll(subjCode, subjName, subjCredits, subjStartTime);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    long count = ClassMgmtSysUtils.countUnreadEmails(loggedInUser);
    if (count > 0) {
      inboxCounter.setVisible(true);
      inboxCounter.setText(String.valueOf(count));
    }
  }

  @FXML
  public void handleCreateCourseButtonAction(ActionEvent event) {
    ((Stage) createCourse.getScene().getWindow()).close();
    ClassMgmtSysUtils.loadWindow("/fxml/teacher/CreateCourseWindow.fxml", "Course creator");
  }

  @FXML
  public void handleModifyCourseButtonAction(ActionEvent event) {
    try {
      selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NullPointerException();
      }
      ((Stage) createCourse.getScene().getWindow()).close();
      ClassMgmtSysUtils.loadWindow("/fxml/teacher/ModifyCourseWindow.fxml", "Course modifier");
    } catch (NullPointerException e) {
      ClassMgmtSysUtils.displayMessage("Please select a subject!");
    }
  }

  @SuppressWarnings("unchecked")
  @FXML
  public void handleDeleteCourseButtonAction(ActionEvent event) {
    try {
      selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NoRowSelectedException("Please select a subject!");
      }
      Alert alert =
          new Alert(
              AlertType.CONFIRMATION,
              "Delete " + selectedCourse.getName() + " ?",
              ButtonType.NO,
              ButtonType.YES);
      alert.showAndWait();

      if (alert.getResult() == ButtonType.YES) {
        deleteCourse(em, selectedCourse);
        ClassMgmtSysUtils.displayMessage(
            "Course " + selectedCourse.getName() + " successfuly deleted" + ".");
        ((Stage) createCourse.getScene().getWindow()).close();
        ClassMgmtSysUtils.loadWindow("/fxml/teacher/TeacherPanel.fxml", "Teacher Panel");
      }
    } catch (NoRowSelectedException e) {
      ClassMgmtSysUtils.displayMessage(e.getMessage());
    }
  }

  private void deleteCourse(EntityManager em, Course course) throws NoRowSelectedException {
    em.refresh(course);
    em.getTransaction().begin();
    course.getTeacher().getCourses().remove(course);
    for (Student student : course.getStudents()) {
      student.getCourses().remove(course);
    }
    for (ClassLog cl : course.getClassLogs()) {
      for (AttendanceLog al : cl.getAttendanceLogs()) {
        em.remove(al);
      }
    }
    em.flush();
    em.remove(course);
    em.getTransaction().commit();
  }

  @FXML
  public void handleCheckStudentsButtonAction(ActionEvent event) {
    try {
      selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NoRowSelectedException("Please select a subject!");
      }
      ((Stage) createCourse.getScene().getWindow()).close();
      ClassMgmtSysUtils.loadWindow("/fxml/teacher/StudentList.fxml", "Student list");
    } catch (NoRowSelectedException e) {
      ClassMgmtSysUtils.displayMessage(e.getMessage());
    }
  }

  @FXML
  public void handleClassPageButtonAction(ActionEvent event) {
    try {
      selectedCourse = tableView.getSelectionModel().getSelectedItem();
      if (selectedCourse == null) {
        throw new NoRowSelectedException("Please select a subject!");
      }
      ((Stage) createCourse.getScene().getWindow()).close();
      ClassMgmtSysUtils.loadWindow("/fxml/teacher/ClassLogs.fxml", "Class Logs");
    } catch (NoRowSelectedException e) {
      ClassMgmtSysUtils.displayMessage(e.getMessage());
    }
  }

  @FXML
  public void handleLogOutButtonAction(ActionEvent event) {
    resetLoggedInUser();
    ((Stage) createCourse.getScene().getWindow()).close();
    ClassMgmtSysUtils.loadWindow("/fxml/common/MainWindow.fxml", "sMain");
  }

  @FXML
  public void handleInboxButtonAction(ActionEvent event) {
    ClassMgmtSysUtils.loadWindow("/fxml/common/InboxWindow.fxml", "Inbox");
    ((Stage) tableView.getScene().getWindow()).close();
  }
}
