package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.AttendanceLog;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@SuppressWarnings("unchecked")
public class AttendancePanelController implements Initializable {

  private EntityManager em = MainWindow.entityManager;
  @FXML
  private Label subjectName;
  @FXML
  private TableView<AttendanceLog> tableView;
  @FXML
  private TableColumn<AttendanceLog, String> neptun;
  @FXML
  private TableColumn<AttendanceLog, String> name;
  @FXML
  private TableColumn<AttendanceLog, String> percentage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    subjectName.setText(TeacherPanelController.getSelectedCourse().getName());
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    neptun.setCellValueFactory(new PropertyValueFactory<>("studentCode"));
    name.setCellValueFactory(data -> {
      em.clear();
      String query =
          "select name from student where code = \"" + data.getValue().getStudentCode() + "\"";
      return new ReadOnlyStringWrapper(
          (String) em.createNativeQuery(query).getSingleResult());
    });
    percentage.setCellValueFactory(data -> {
      String queryTotalClasses =
          "select count(*) from classlog where coursecode = \"" + data.getValue().getCourseCode()
              + "\"";
      int totalClasses = ((Long) em.createNativeQuery(queryTotalClasses).getSingleResult())
          .intValue();
      String queryTotalAttendances =
          "select count(*) from attendancelog where studentcode = \"" + data.getValue()
              .getStudentCode() + "\" and coursecode =\"" + data.getValue().getCourseCode() + "\"";
      int totalAttendances = ((Long) em.createNativeQuery(queryTotalAttendances).getSingleResult())
          .intValue();
      return new ReadOnlyStringWrapper(
          String.valueOf(Math.round((double) totalAttendances / (double) totalClasses * 100)));
    });
    constructTableView();
  }

  private void constructTableView() {
    em.clear();
    tableView.setItems(getAttendanceLogs());
    tableView.getColumns().clear();
    tableView.getColumns().addAll(neptun, name, percentage);
  }

  public ObservableList<AttendanceLog> getAttendanceLogs() {
    em.clear();
    ObservableList<AttendanceLog> attendanceLogs = FXCollections.observableArrayList();
    String q =
        "select * from attendancelog where classlog_id = \"" + ClassLogsController
            .getCurrentClassLogId() + "\"";
    Query query = em.createNativeQuery(q, AttendanceLog.class);
    List<AttendanceLog> attendanceLogsList = query.getResultList();
    attendanceLogs.addAll(attendanceLogsList);
    return attendanceLogs;
  }

  @FXML
  public void handleBackButtonAction() {
    ((Stage) subjectName.getScene().getWindow()).close();
    MainWindow.loadWindow("/ClassLogs.fxml", "Class logs");
  }

  @FXML
  public void handleRefreshButtonAction() {
    constructTableView();
  }
}
