package com.thesis.classmgmtsystem.controller.teacher;

import com.thesis.classmgmtsystem.main.MainWindow;
import com.thesis.classmgmtsystem.model.ClassLog;
import com.thesis.classmgmtsystem.model.Course;
import com.thesis.classmgmtsystem.util.ClassMgmtSysUtils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javax.persistence.EntityManager;

public class ClassLogsController implements Initializable {

  private static ClassLog currentClassLog;
  private static EntityManager em = MainWindow.entityManager;
  private Course selectedCourse = TeacherPanelController.getSelectedCourse();
  @FXML
  private Label subjectName;
  @FXML
  private TableView<ClassLog> tableView;
  @FXML
  private TableColumn<ClassLog, String> date;
  @FXML
  private TableColumn<ClassLog, String> attendanceWindow;
  @FXML
  private TableColumn<ClassLog, String> attendanceCount;
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  public static ClassLog getCurrentClassLog() {
    return currentClassLog;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    subjectName.setText(selectedCourse.getName());
    attendanceWindow.setCellValueFactory(data -> {
      if (data.getValue().isAttendanceWindowClosed()) {
        return new ReadOnlyStringWrapper("Closed");
      }
      return new ReadOnlyStringWrapper("Open");
    });
    date.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDate()));
    attendanceCount.setCellValueFactory(
        data -> new ReadOnlyStringWrapper(
            String.valueOf(data.getValue().getAttendanceLogs().size())));
    constructTableView();
  }


  private void constructTableView() {
    em.refresh(selectedCourse);
    ObservableList<ClassLog> courses = FXCollections.observableArrayList();
    courses.addAll(selectedCourse.getClassLogs());
    tableView.setItems(courses);
    tableView.getColumns().clear();
    tableView.getColumns().addAll(date, attendanceWindow, attendanceCount);
  }

  @FXML
  public void handleBackButtonAction() {
    ((Stage) tableView.getScene().getWindow()).close();
    ClassMgmtSysUtils.loadWindow("/fxml/teacher/TeacherPanel.fxml", "Teacher Panel");
  }

  @FXML
  public void handleRefreshButtonAction() {
    constructTableView();
  }

  @FXML
  public void handleOpenClasslogButtonAction() {
    currentClassLog = tableView.getSelectionModel().getSelectedItem();
    ((Stage) tableView.getScene().getWindow()).close();
    ClassMgmtSysUtils.loadWindow("/fxml/teacher/AttendancePanel.fxml", "Attendance panel");
  }

  @FXML
  public void handleStartClassButtonAction() {
    ClassLog classLog = startClasslog(em, selectedCourse);
    currentClassLog = classLog;
    constructTableView();
    int timeoutMinutes = classLog.getCourse().getTimeoutMinutes();
    Thread countDown = new Thread(() -> {
      try {
        long millis = TimeUnit.MILLISECONDS.convert(timeoutMinutes, TimeUnit.MINUTES);
        Thread.sleep(millis);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      em.getTransaction().begin();
      em.find(ClassLog.class, classLog.getId()).setAttendanceWindowClosed(true);
      em.getTransaction().commit();
    });
    ClassMgmtSysUtils.displayMessage(
        "Attendance window will close in " + timeoutMinutes + " minutes.");
    countDown.start();
  }

  @FXML
  public void handleEndClassButtonAction() {
    ClassLog classLog = tableView.getSelectionModel().getSelectedItem();
    if (classLog == null) {
      ClassMgmtSysUtils.displayMessage("Please select a class.");
      return;
    }
    em.getTransaction().begin();
    classLog.setAttendanceWindowClosed(true);
    em.getTransaction().commit();
    constructTableView();
  }

  private ClassLog startClasslog(EntityManager em, Course course) {
    Date date = new Date();
    String dateString = sdf.format(date);
    ClassLog classLog = new ClassLog(course, dateString, false);
    em.getTransaction().begin();
    course.getClassLogs().add(classLog);
    em.persist(classLog);
    em.getTransaction().commit();
    return classLog;
  }
}