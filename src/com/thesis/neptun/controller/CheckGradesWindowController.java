package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Result;
import com.thesis.neptun.model.Student;
import com.thesis.neptun.model.User;
import com.thesis.neptun.util.NeptunUtils;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.persistence.EntityManager;

public class CheckGradesWindowController implements Initializable {

  User loggedInUser = MainWindowController.getLoggedInUser();
  private EntityManager em = MainWindow.entityManager;
  @FXML
  private TableView<Result> tableView;
  @FXML
  private TableColumn<Result, String> code;
  @FXML
  private TableColumn<Result, String> name;
  @FXML
  private TableColumn<Result, String> grade;
  @FXML
  private TextField gpa;

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    em.refresh(loggedInUser);
    tableView.refresh();
    gpa.setDisable(true);
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    code.setCellValueFactory(data->new ReadOnlyStringWrapper(data.getValue().getCourse().getCourseCode()));
    name.setCellValueFactory(data->new ReadOnlyStringWrapper(data.getValue().getCourse().getName()));
    grade.setCellValueFactory(new PropertyValueFactory<>("grade"));
    ObservableList<Result> results = FXCollections.observableArrayList();
    results.addAll(((Student)loggedInUser).getResults());
    tableView.setItems(results);
    tableView.getColumns().clear();
    tableView.getColumns().addAll(code, name, grade);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    gpa.setText("GPA: " + calculateGpa((Student)loggedInUser));
  }

  private long calculateGpa(Student student) {
    List<Result> resultList = student.getResults();
    long gpaTemp = 0;
    for (Result x : resultList) {
      if (!x.getGrade().equals("None")) {
        gpaTemp += Integer.parseInt(x.getGrade());
      }
    }
    if (gpaTemp > 0) {
      gpaTemp /= resultList.size();
    }
    return gpaTemp;
  }

  @FXML
  public void handleBackButtonAction() {
    NeptunUtils.loadWindow("/StudentPanel.fxml", "Student Panel");
    ((Stage) tableView.getScene().getWindow()).close();
  }
}
