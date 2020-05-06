package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Result;
import com.thesis.neptun.model.Student;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CheckGradesWindowController implements Initializable {
  @FXML private TableView<Result> tableView;
  @FXML private TableColumn<Result, String> code;
  @FXML private TableColumn<Result, String> name;
  @FXML private TableColumn<Result, String> grade;
  @FXML private TextField gpa;

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    tableView.refresh();
    gpa.setDisable(true);
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    code.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
    name.setCellValueFactory(new PropertyValueFactory<>("name"));
    grade.setCellValueFactory(new PropertyValueFactory<>("grade"));
    tableView.setItems(getResultList());
    tableView.getColumns().clear();
    tableView.getColumns().addAll(code, name, grade);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  private ObservableList<Result> getResultList() {
    ObservableList<Result> Result = FXCollections.observableArrayList();
    Student student = (Student) MainWindowController.getLoggedInUser();
    String query = "select * from Result where studentcode = \"" + student.getCode() + "\"";
    MainWindow.entityManager.getEntityManagerFactory().getCache().evictAll();
    @SuppressWarnings("unchecked")
    List<Result> resultList =
        MainWindow.entityManager.createNativeQuery(query, Result.class).getResultList();
    long gpaTemp = 0;
    for (Result x : resultList) {
      gpaTemp += Integer.parseInt(x.getGrade());
      try {
        String name =
            (String)
                MainWindow.entityManager
                    .createNativeQuery(
                        "select name from course where coursecode = \"" + x.getCourseCode() + "\"")
                    .getSingleResult();
        x.setName(name);
      } catch (Exception ignore) {

      }
      Result.add(x);
    }

    if (gpaTemp > 0) gpaTemp /= resultList.size();
    gpa.setText("GPA: " + gpaTemp);
    return Result;
  }

  @FXML
  public void handleBackButtonAction() {
    MainWindow.loadWindow("/StudentPanel.fxml", "Student Panel");
    ((Stage) tableView.getScene().getWindow()).close();
  }
}
