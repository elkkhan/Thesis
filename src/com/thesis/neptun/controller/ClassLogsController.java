package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.ClassLog;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
import javax.persistence.NoResultException;
import javax.persistence.Query;

@SuppressWarnings("unchecked")
public class ClassLogsController implements Initializable {

  private static int currentClassLogId;
  private EntityManager em = MainWindow.entityManager;
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

  public static int getCurrentClassLogId() {
    return currentClassLogId;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    subjectName.setText(TeacherPanelController.getSelectedCourse().getName());
    attendanceWindow.setCellValueFactory(data -> {
      if (data.getValue().isAttendanceWindowClosed()) {
        return new ReadOnlyStringWrapper("Closed");
      }
      return new ReadOnlyStringWrapper("Open");
    });
    date.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDate()));
    attendanceCount.setCellValueFactory(
        data -> new ReadOnlyStringWrapper(String.valueOf(countAttendance(data.getValue()))));
    constructTableView();
  }

  private int countAttendance(ClassLog classLog) {
    em.clear();
    String query =
        "select count(*) from attendancelog where coursecode = \"" + classLog.getCourseCode()
            + "\" and classlog_id = " + classLog.getId();
    int count = 0;
    try {
      count = ((Long) em.createNativeQuery(query).getSingleResult()).intValue();
    } catch (NoResultException ignore) {
    }
    return count;
  }

  private void constructTableView() {
    tableView.setItems(getClassLogs());
    tableView.getColumns().clear();
    tableView.getColumns().addAll(date, attendanceWindow, attendanceCount);
  }

  private ObservableList<ClassLog> getClassLogs() {
    em.clear();
    ObservableList<ClassLog> courses = FXCollections.observableArrayList();
    String q =
        "select * from classlog where coursecode = \"" + TeacherPanelController.getSelectedCourse()
            .getCourseCode() + "\"";
    Query query = em.createNativeQuery(q, ClassLog.class);
    List<ClassLog> classLogs = query.getResultList();
    courses.addAll(classLogs);
    return courses;
  }

  @FXML
  public void handleBackButtonAction() {
    ((Stage) tableView.getScene().getWindow()).close();
    MainWindow.loadWindow("/TeacherPanel.fxml", "Teacher Panel");
  }

  @FXML
  public void handleRefreshButtonAction() {
    constructTableView();
  }

  @FXML
  public void handleOpenClasslogButtonAction() {
    currentClassLogId = tableView.getSelectionModel().getSelectedItem().getId();
    ((Stage) tableView.getScene().getWindow()).close();
    MainWindow.loadWindow("/AttendancePanel.fxml", "Attendance panel");
  }

  @FXML
  public void handleStartClassButtonAction() {
    String courseCode = TeacherPanelController.getSelectedCourse().getCourseCode();
    Date date = new Date();
    String dateString = sdf.format(date);
    ClassLog classLog = new ClassLog(courseCode, dateString, false);
    MainWindow.entityManager.getTransaction().begin();
    MainWindow.entityManager.persist(classLog);
    MainWindow.entityManager.getTransaction().commit();
    String s = "select cl.id from ClassLog cl where cl.date = ?1 and cl.courseCode = ?2 order by cl.id desc";
    Query query = em.createQuery(s).setParameter(1, dateString)
        .setParameter(2, courseCode);
    query.setMaxResults(1);
    currentClassLogId = (Integer) query.getResultList().get(0);
    constructTableView();
    String countdownQuery =
        "select timeoutminutes from course where coursecode = \"" + classLog.getCourseCode()
            + "\"";
    int timoeutMinutes = (Integer) em.createNativeQuery(countdownQuery).getSingleResult();
    Thread countDown = new Thread(() -> {
      try {
        long millis = TimeUnit.MILLISECONDS.convert(timoeutMinutes, TimeUnit.MINUTES);
        Thread.sleep(millis);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      em.clear();
      em.getTransaction().begin();
      em.find(ClassLog.class, classLog.getId()).setAttendanceWindowClosed(true);
      em.getTransaction().commit();
    });
    MainWindow.displayMessage("Countdown",
        "Attendance window will close in " + timoeutMinutes + " minutes.");
    countDown.start();
  }
}