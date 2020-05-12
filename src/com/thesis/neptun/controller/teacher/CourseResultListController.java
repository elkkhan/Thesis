package com.thesis.neptun.controller.teacher;

import com.thesis.neptun.exception.NoRowSelectedException;
import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Result;
import com.thesis.neptun.model.Student;
import com.thesis.neptun.util.NeptunUtils;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.persistence.EntityManager;

public class CourseResultListController implements Initializable {

  private static Result selectedResult;
  private EntityManager em = MainWindow.entityManager;
  @FXML
  private TableView<Result> tableView;
  @FXML
  private TableColumn<Result, String> neptun;
  @FXML
  private TableColumn<Result, String> name;
  @FXML
  private TableColumn<Result, String> grade;
  @FXML
  private TableColumn<Result, String> percentage;
  @FXML
  private TextField headcount;

  private Course selectedCourse;
  private String query;

  public static Result getSelectedResult() {
    return selectedResult;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    headcount.setDisable(true);
    selectedCourse = TeacherPanelController.getSelectedCourse();
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    neptun.setCellValueFactory(
        data -> new ReadOnlyStringWrapper(data.getValue().getStudent().getCode()));
    name.setCellValueFactory(
        data -> new ReadOnlyStringWrapper(data.getValue().getStudent().getName()));
    grade.setCellValueFactory(new PropertyValueFactory<>("grade"));
    percentage.setCellValueFactory(data -> {
      return new ReadOnlyStringWrapper(
          String.valueOf(
              NeptunUtils.getAttendancePercentage(selectedCourse, data.getValue().getStudent())));
    });

    ObservableList<Result> enrolledStudentResultList = FXCollections.observableArrayList();
    enrolledStudentResultList.addAll(getAllCourseResults(selectedCourse));
    tableView.setItems(enrolledStudentResultList);
    tableView.getColumns().clear();
    tableView.getColumns().addAll(neptun, name, grade, percentage);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    headcount.setText("Student headcount: " + enrolledStudentResultList.size());
  }

  //get persisted result from db and created new transient results with grade "None" for students without a grade in the database
  private ObservableList<Result> getAllCourseResults(Course course) {
    em.refresh(selectedCourse);
    ObservableList<Result> enrolledStudentResultList = FXCollections.observableArrayList();
    enrolledStudentResultList.addAll(course.getResults());
    for (Student student : course.getStudents()) {
      if (NeptunUtils.getCourseResult(student, course) == null) {
        Result result = new Result(student, course, "None");
        result.setId(-1);
        enrolledStudentResultList.add(result);
      }
    }
    return enrolledStudentResultList;
  }

  @FXML
  public void handleBackButtonAction(ActionEvent event) {
    NeptunUtils.loadWindow("/fxml/teacher/TeacherPanel.fxml", "Teacher Panel");
    ((Stage) tableView.getScene().getWindow()).close();
  }

  @FXML
  public void handleModifyGradeButtonAction(ActionEvent event) {
    try {
      selectedResult = tableView.getSelectionModel().getSelectedItem();
      if (selectedResult == null) {
        throw new NoRowSelectedException("Please select an enrty!");
      }
      ((Stage) tableView.getScene().getWindow()).close();
      NeptunUtils.loadWindow("/fxml/teacher/ModifyGradeWindow.fxml", "Modifying grade");
    } catch (NoRowSelectedException e) {
      NeptunUtils.displayMessage("Neptun System", e.getMessage());
    }
  }
}
