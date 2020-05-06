package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Result;
import com.thesis.neptun.model.Student;
import java.net.URL;
import java.util.List;
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

public class StudentListController implements Initializable {

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
    query = "select id from course where coursecode=\"" + selectedCourse.getCourseCode() + "\"";
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    neptun.setCellValueFactory(new PropertyValueFactory<>("studentCode"));
    name.setCellValueFactory(new PropertyValueFactory<>("name"));
    grade.setCellValueFactory(new PropertyValueFactory<>("grade"));
    percentage.setCellValueFactory(data -> {
      em.clear();
      ;
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

    tableView.setItems(getStudentList());
    tableView.getColumns().clear();
    tableView.getColumns().addAll(neptun, name, grade, percentage);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    headcount.setText("Student headcount: " + getStudentList().size());
  }

  @SuppressWarnings("unchecked")
  private ObservableList<Result> getStudentList() {
    ObservableList<Result> resultObservableList = FXCollections.observableArrayList();
    int id = (int) MainWindow.entityManager.createNativeQuery(query).getSingleResult();
    List<Student> studentList =
        MainWindow.entityManager
            .createNativeQuery(
                "select * from student where id IN (select students_id"
                    + " from course_student where courses_id="
                    + id
                    + ")",
                Student.class)
            .getResultList();

    for (Student x : studentList) {
      x.setGradeDb(selectedCourse.getCourseCode());
      if (x.getGrade().equals("")) {
        x.setGrade("No grade");
      }
      Result result = new Result();
      result.setStudentCode(x.getCode());
      result.setCourseCode(selectedCourse.getCourseCode());
      result.setGrade(x.getGrade());
      result.setName(x.getName());
      resultObservableList.add(result);
    }
    return resultObservableList;
  }

  @FXML
  public void handleBackButtonAction(ActionEvent event) {
    MainWindow.loadWindow("/TeacherPanel.fxml", "Teacher Panel");
    ((Stage) tableView.getScene().getWindow()).close();
  }

  @FXML
  public void handleModifyGradeButtonAction(ActionEvent event) {
    try {
      selectedResult = tableView.getSelectionModel().getSelectedItem();
      if (selectedResult == null) {
        throw new NullPointerException();
      }
      ((Stage) tableView.getScene().getWindow()).close();
      MainWindow.loadWindow("/ModifyGradeWindow.fxml", "Modifying grade");
    } catch (NullPointerException e) {
      MainWindow.displayMessage("Neptun System", "Please select an enrty!");
    }
  }
}
