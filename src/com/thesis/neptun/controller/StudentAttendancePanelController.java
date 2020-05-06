package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.AttendanceLog;
import com.thesis.neptun.model.ClassLog;
import com.thesis.neptun.model.Course;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@SuppressWarnings("unchecked")
public class StudentAttendancePanelController implements Initializable {

  private EntityManager em = MainWindow.entityManager;
  private Course selectedCourse = StudentPanelController.getSelectedCourse();
  @FXML
  private Button attend;
  @FXML
  private Label subjectName;
  @FXML
  private TableView<ClassLog> tableView;
  @FXML
  private TableColumn<ClassLog, String> date;
  @FXML
  private TableColumn<ClassLog, String> attendanceWindow;
  @FXML
  private TableColumn<ClassLog, String> attended;
  @FXML
  private Label percentage;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    subjectName.setText(StudentPanelController.getSelectedCourse().getName());
    attendanceWindow.setCellValueFactory(data -> {
      if (data.getValue().isAttendanceWindowClosed()) {
        return new ReadOnlyStringWrapper("Closed");
      }
      return new ReadOnlyStringWrapper("Open");
    });
    date.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDate()));
    attended.setCellValueFactory(data -> {
      if (hasAttended(data.getValue(), MainWindowController.getLoggedInUser().getCode())) {
        return new ReadOnlyStringWrapper("Yes");
      }
      return new ReadOnlyStringWrapper("No");
    });
    constructTableView();
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  private double getAttendancePercentage() {
    String queryTotalClasses =
        "select count(*) from classlog where coursecode = \"" + selectedCourse.getCourseCode()
            + "\"";
    int totalClasses = ((Long) em.createNativeQuery(queryTotalClasses).getSingleResult())
        .intValue();
    String queryTotalAttendances =
        "select count(*) from attendancelog where studentcode = \"" + MainWindowController
            .getLoggedInUser().getCode() + "\" and coursecode =\"" + selectedCourse.getCourseCode()
            + "\"";
    int totalAttendances = ((Long) em.createNativeQuery(queryTotalAttendances).getSingleResult())
        .intValue();
    return Math.round((double) totalAttendances / (double) totalClasses * 100);
  }

  private void constructTableView() {
    tableView.setItems(getClassLogs());
    tableView.getColumns().clear();
    tableView.getColumns().addAll(attendanceWindow, date, attended);
    percentage.setText("Overall attendance: " + getAttendancePercentage() + "%");
  }

  @FXML
  public void handleAttendButtonAction() {
    ClassLog cl = tableView.getSelectionModel().getSelectedItem();
    if (cl == null) {
      MainWindow.displayMessage("No selection", "Please select a class.");
      return;
    }
    String courseCode = cl.getCourseCode();
    String studentCode = MainWindowController.getLoggedInUser().getCode();
    System.out.println(cl);
    if (cl.isAttendanceWindowClosed()) {
      MainWindow.displayMessage("Attendance window not open",
          "Cannot register attendance outside of attendance period.");
      return;
    } else if (hasAttended(cl, studentCode)) {
      MainWindow.displayMessage("Already registered", "Attendance already marked.");
      return;
    }
    AttendanceLog al = new AttendanceLog(cl, courseCode, studentCode);
    em.getTransaction().begin();
    em.persist(al);
    em.getTransaction().commit();
    constructTableView();
    MainWindow.displayMessage("OK", "Attendance marked.");
  }

  private ObservableList<ClassLog> getClassLogs() {
    em.clear();
    ObservableList<ClassLog> courses = FXCollections.observableArrayList();
    String q =
        "select * from classlog where coursecode = \"" + StudentPanelController.getSelectedCourse()
            .getCourseCode() + "\"";
    Query query = em.createNativeQuery(q, ClassLog.class);
    List<ClassLog> classLogs = query.getResultList();
    courses.addAll(classLogs);
    return courses;
  }

  private boolean hasAttended(ClassLog cl, String studentCode) {
    String query =
        "select count(*) from attendancelog where classlog_id=" + cl.getId() + " and studentcode=\""
            + studentCode + "\"";
    return (long) em.createNativeQuery(query).getSingleResult() != 0;
  }

  @FXML
  public void handleBackButtonAction() {
    ((Stage) attend.getScene().getWindow()).close();
    MainWindow.loadWindow("/StudentPanel.fxml", "Student Panel");
  }

  @FXML
  public void handleRefreshButtonAction() {
    constructTableView();
  }
}
