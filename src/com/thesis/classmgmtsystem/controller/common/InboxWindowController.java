package com.thesis.classmgmtsystem.controller.common;

import com.thesis.classmgmtsystem.controller.common.MainWindowController.userType;
import com.thesis.classmgmtsystem.model.Message;
import com.thesis.classmgmtsystem.model.User;
import com.thesis.classmgmtsystem.main.MainWindow;
import com.thesis.classmgmtsystem.util.ClassMgmtSysUtils;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.persistence.EntityManager;

public class InboxWindowController implements Initializable {

  private static Message openedMessage;
  private EntityManager em = MainWindow.entityManager;
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

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    em.refresh(loggedInUser);
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    sender.setCellValueFactory(
        data -> new ReadOnlyStringWrapper(data.getValue().getSender().getName()));
    subject.setCellValueFactory(new PropertyValueFactory<>("subject"));
    date.setCellValueFactory(new PropertyValueFactory<>("date"));
    ObservableList<Message> messages = FXCollections.observableArrayList();
    messages.addAll(loggedInUser.getInbound());
    tableView.setItems(messages);
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
                  em.getTransaction().begin();
                  openedMessage.setIsRead(true);
                  em.getTransaction().commit();
                  ClassMgmtSysUtils.loadWindow("/fxml/common/OpenMessage.fxml", "Message");
                  ((Stage) tableView.getScene().getWindow()).close();
                }
              });
          return row;
        });
  }

  @FXML
  public void handleBackButtonAction() {
    ((Stage) tableView.getScene().getWindow()).close();

    if (MainWindowController.getLoggedInUserType() == userType.student) {
      ClassMgmtSysUtils.loadWindow("/fxml/student/StudentPanel.fxml", "Student Panel");
    } else if (MainWindowController.getLoggedInUserType() == userType.teacher) {
      ClassMgmtSysUtils.loadWindow("/fxml/teacher/TeacherPanel.fxml", "Teacher");
    }
  }

  @FXML
  public void handleComposeButtonAction() {
    ClassMgmtSysUtils.loadWindow("/fxml/common/ComposeMessageWindow.fxml", "Compose new message");
  }
}
