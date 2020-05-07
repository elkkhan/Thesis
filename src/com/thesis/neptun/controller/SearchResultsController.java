package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Student;
import com.thesis.neptun.model.Teacher;
import com.thesis.neptun.model.User;
import java.net.URL;
import java.util.ArrayList;
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
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class SearchResultsController implements Initializable {

  private EntityManager em = MainWindow.entityManager;
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
    ObservableList<User> users = FXCollections.observableArrayList();
    users.addAll(getSearchResults(em, receiverData));
    return users;
  }

  private List<User> getSearchResults(EntityManager em, String receiverData) {
    String query =
        "select id from (select id, code, name, email  from student union \n"
            + "select id, code, name, email  from teacher \n"
            + "order by name) as t where (t.name like \"%"
            + receiverData
            + "%\" or email like \"%"
            + receiverData
            + "%\" or code like \"%"
            + receiverData
            + "%\")";
    List<Integer> matchedIds = em.createNativeQuery(query).getResultList();
    List<User> matchedUsers = new ArrayList<>();
    for (Integer id : matchedIds) {
      User user;
      try {
        user = (User) em.find(Student.class, id);
      } catch (NoResultException e) {
        user = (User) em.find(Teacher.class, id);
      }
      matchedUsers.add(user);
    }
    return matchedUsers;
  }


  public void handleSelectButtonAction() {
    ComposeMessageWindowController.getBackReceiverData(
        tableView.getSelectionModel().getSelectedItem());
    ((Stage) tableView.getScene().getWindow()).close();
  }
}
