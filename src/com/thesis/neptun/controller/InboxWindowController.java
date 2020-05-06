package com.thesis.neptun.controller;

import com.thesis.neptun.controller.MainWindowController.userType;
import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Message;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class InboxWindowController implements Initializable {

  private static Message openedMessage;
  private User loggedInUser = MainWindowController.getLoggedInUser();
  @FXML
  private TableView<Message> tableView;
  @FXML
  private TableColumn<Message, String> sender;
  @FXML
  private TableColumn<Message, String> subject;
  @FXML
  private TableColumn<Message, String> date;

  static Message getOpenedMessage() {
    return openedMessage;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    sender.setCellValueFactory(new PropertyValueFactory<>("senderName"));
    subject.setCellValueFactory(new PropertyValueFactory<>("subject"));
    date.setCellValueFactory(new PropertyValueFactory<>("date"));

    tableView.setItems(getMessageList());
    tableView.getColumns().clear();
    tableView.getColumns().addAll(sender, subject, date);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    tableView.setRowFactory(
        tv -> {
          TableRow<Message> row = new TableRow<>();
          row.setOnMouseClicked(
              event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                  openedMessage = row.getItem();
                  MainWindow.entityManager.getTransaction().begin();
                  openedMessage.setIsRead(true);
                  MainWindow.entityManager.getTransaction().commit();
                  MainWindow.loadWindow("/OpenMessage.fxml", "Message");
                  ((Stage) tableView.getScene().getWindow()).close();
                }
              });
          return row;
        });
  }

  @SuppressWarnings("unchecked")
  private ObservableList<Message> getMessageList() {
    String query =
        "select * from message where receiverEmail = \"" + loggedInUser.getEmail() + "\"";
    ObservableList<Message> messages = FXCollections.observableArrayList();
    List<Message> messageList =
        MainWindow.entityManager.createNativeQuery(query, Message.class).getResultList();
    messages.addAll(messageList);
    return messages;
  }

  @FXML
  public void handleBackButtonAction() {
    ((Stage) tableView.getScene().getWindow()).close();

    if (MainWindowController.getLoggedInUserType() == userType.student) {
      MainWindow.loadWindow("/StudentPanel.fxml", "Student Panel");
    } else if (MainWindowController.getLoggedInUserType() == userType.teacher) {
      MainWindow.loadWindow("/TeacherPanel.fxml", "Teacher");
    }
  }

  @FXML
  public void handleComposeButtonAction() {
    MainWindow.loadWindow("/ComposeMessageWindow.fxml", "Compose new message");
  }
}
