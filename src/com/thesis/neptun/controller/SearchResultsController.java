package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Student;
import com.thesis.neptun.model.Teacher;
import com.thesis.neptun.model.User;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.persistence.NoResultException;

public class SearchResultsController implements Initializable {

  private User loggedInUser = MainWindowController.getLoggedInUser();
  @FXML
  private TableView<User> tableView;
  @FXML
  private TableColumn<User, String> neptunSearch;
  @FXML
  private TableColumn<User, String> emailSearch;

  private String receiverData = ComposeMessageWindowController.getReceiverData();

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    neptunSearch.setCellValueFactory(new PropertyValueFactory<>("code"));
    emailSearch.setCellValueFactory(new PropertyValueFactory<>("email"));
    tableView.setItems(getUserList());
    tableView.getColumns().clear();
    tableView.getColumns().addAll(neptunSearch, emailSearch);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  @SuppressWarnings("unchecked")
  private ObservableList<User> getUserList() {
    String loggedInNeptun = loggedInUser.getCode();

    String query =
        "select code from (select code, name, email  from student union \n"
            + "select code, name, email  from teacher \n"
            + "order by name) as t where not(t.code=\""
            + loggedInNeptun
            + "\") and (t.name like \"%"
            + receiverData
            + "%\" or email like \"%"
            + receiverData
            + "%\" or code like \"%"
            + receiverData
            + "%\")";

    ObservableList<User> users = FXCollections.observableArrayList();

    List<String> neptunList = MainWindow.entityManager.createNativeQuery(query).getResultList();

    for (String x : neptunList) {
      User user;
      try {
        user =
            (User)
                MainWindow.entityManager
                    .createNativeQuery(
                        "select * from student where code = \"" + x + "\"", Student.class)
                    .getSingleResult();
      } catch (NoResultException e) {
        user =
            (User)
                MainWindow.entityManager
                    .createNativeQuery(
                        "select * from teacher where code = \"" + x + "\"", Teacher.class)
                    .getSingleResult();
      }
      users.add(user);
    }
    return users;
  }

  public void handleSelectButtonAction() {
    ComposeMessageWindowController.getBackReceiverData(
        tableView.getSelectionModel().getSelectedItem().getEmail());
    ((Stage) tableView.getScene().getWindow()).close();
  }
}
