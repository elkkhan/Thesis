package com.thesis.neptun.controller.teacher;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.AttendanceLog;
import com.thesis.neptun.model.ClassLog;
import com.thesis.neptun.util.NeptunUtils;
import java.net.URL;
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
import javafx.stage.Stage;
import javax.persistence.EntityManager;

public class AttendancePanelController implements Initializable {

  private EntityManager em = MainWindow.entityManager;
  private ClassLog currentClasslog = ClassLogsController.getCurrentClassLog();
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
    neptun.setCellValueFactory(
        data -> new ReadOnlyStringWrapper(data.getValue().getStudent().getCode()));
    name.setCellValueFactory(
        data -> new ReadOnlyStringWrapper(data.getValue().getStudent().getName()));
    percentage.setCellValueFactory(data -> new ReadOnlyStringWrapper(
        String.valueOf(NeptunUtils
            .getAttendancePercentage(data.getValue().getClassLog().getCourse(),
                data.getValue().getStudent()))));
    constructTableView();
  }

  private void constructTableView() {
    em.refresh(currentClasslog);
    ObservableList<AttendanceLog> attendanceLogs = FXCollections.observableArrayList();
    attendanceLogs.addAll(currentClasslog.getAttendanceLogs());
    tableView.setItems(attendanceLogs);
    tableView.getColumns().clear();
    tableView.getColumns().addAll(neptun, name, percentage);
  }

  @FXML
  public void handleBackButtonAction() {
    ((Stage) subjectName.getScene().getWindow()).close();
    NeptunUtils.loadWindow("/fxml/teacher/ClassLogs.fxml", "Class logs");
  }

  @FXML
  public void handleRefreshButtonAction() {
    constructTableView();
  }
}
