package com.thesis.neptun.controller.student;

import com.thesis.neptun.controller.common.MainWindowController;
import com.thesis.neptun.exception.AttendanceException;
import com.thesis.neptun.exception.NoRowSelectedException;
import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.AttendanceLog;
import com.thesis.neptun.model.ClassLog;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Student;
import com.thesis.neptun.util.NeptunUtils;
import java.net.URL;
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

public class StudentAttendancePanelController implements Initializable {

  private EntityManager em = MainWindow.entityManager;
  private Student student = (Student) MainWindowController.getLoggedInUser();
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
      if (hasAttended(data.getValue(), student)) {
        return new ReadOnlyStringWrapper("Yes");
      }
      return new ReadOnlyStringWrapper("No");
    });
    constructTableView();
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  private void constructTableView() {
    em.refresh(selectedCourse);
    ObservableList<ClassLog> classLogs = FXCollections.observableArrayList();
    classLogs.addAll(selectedCourse.getClassLogs());
    tableView.setItems(classLogs);
    tableView.getColumns().clear();
    tableView.getColumns().addAll(attendanceWindow, date, attended);
    percentage.setText(
        "Overall attendance: " + NeptunUtils.getAttendancePercentage(selectedCourse, student)
            + "%");
  }

  @FXML
  public void handleAttendButtonAction() {
    try {
      ClassLog cl = tableView.getSelectionModel().getSelectedItem();
      if (cl == null) {
        throw new NoRowSelectedException("Please select a class.");
      }
      AttendanceLog al = attendClass(em, cl, student);
      constructTableView();
      NeptunUtils
          .displayMessage("OK", "Attendance marked for " + al.getClassLog().getCourse().getName());
    } catch (NoRowSelectedException e) {
      NeptunUtils.displayMessage("No selection", e.getMessage());
    } catch (AttendanceException e) {
      NeptunUtils.displayMessage("Attendance error", e.getMessage());
    }
  }

  private AttendanceLog attendClass(EntityManager em, ClassLog cl, Student student)
      throws AttendanceException {
    if (cl.isAttendanceWindowClosed()) {
      throw new AttendanceException("Cannot register attendance outside of attendance period.");
    } else if (hasAttended(cl, student)) {
      throw new AttendanceException("Attendance already marked.");
    }
    AttendanceLog al = new AttendanceLog(cl, student);
    em.getTransaction().begin();
    student.getAttendanceLogs().add(al);
    cl.getAttendanceLogs().add(al);
    em.persist(al);
    em.getTransaction().commit();
    return al;
  }

  private boolean hasAttended(ClassLog cl, Student student) {
    for (AttendanceLog attendanceLog : student.getAttendanceLogs()) {
      if (cl.getAttendanceLogs().contains(attendanceLog)) {
        return true;
      }
    }
    return false;
  }

  @FXML
  public void handleBackButtonAction() {
    ((Stage) attend.getScene().getWindow()).close();
    NeptunUtils.loadWindow("/fxml/student/StudentPanel.fxml", "Student Panel");
  }

  @FXML
  public void handleRefreshButtonAction() {
    constructTableView();
  }
}
