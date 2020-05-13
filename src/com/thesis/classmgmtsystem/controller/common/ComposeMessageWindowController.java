package com.thesis.classmgmtsystem.controller.common;

import com.thesis.classmgmtsystem.model.Message;
import com.thesis.classmgmtsystem.model.User;
import com.thesis.classmgmtsystem.main.MainWindow;
import com.thesis.classmgmtsystem.util.ClassMgmtSysUtils;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class ComposeMessageWindowController implements Initializable {

  private static String receiverSearchKeyword;
  private static User receiver;
  private EntityManager em = MainWindow.entityManager;
  private User loggedInUser = MainWindowController.getLoggedInUser();
  @FXML
  private TextField receiverData;
  @FXML
  private TextField subject;
  @FXML
  private TextArea message;

  public static String getReceiverSearchKeyword() {
    return receiverSearchKeyword;
  }

  static void setReceiverData(User selectedUser) {
    receiver = selectedUser;
  }

  public static void sendMessage(EntityManager em, User sender, User receiver,
      String subject, String content) {
    em.getTransaction().begin();
    Message message = new Message(sender, receiver, content, subject);
    sender.getOutbound().add(message);
    receiver.getInbound().add(message);
    em.persist(message);
    em.getTransaction().commit();
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
  }

  public void setReplyData(User receiver, String subjectDataText) {
    ComposeMessageWindowController.receiver = receiver;
    receiverData.setText(receiver.getEmail());
    subject.setText(subjectDataText);
  }

  public void handleSearchButtonAction() {
    if (receiverData.getText().isEmpty()) {
      ClassMgmtSysUtils.displayMessage("Please enter the receipent name.");
    } else {
      receiverSearchKeyword = receiverData.getText();
      try {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/common/SearchResults.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Search Result");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
      } catch (Exception ignore) {
      }
      receiverData.setText(receiver.getEmail());
    }
  }

  public void handleSendButtonAction() {
    if (receiverData.getText().isEmpty()) {
      ClassMgmtSysUtils.displayMessage("Please enter the receiver's email.");
    } else if (subject.getText().isEmpty()) {
      ClassMgmtSysUtils.displayMessage("Please enter a subject.");
    } else if (message.getText().isEmpty()) {
      ClassMgmtSysUtils.displayMessage("Please enter you message.");
    } else {
      try {
        em.find(User.class, receiver.getId());
      } catch (NoResultException e) {
        ClassMgmtSysUtils.displayMessage(
            "No users with such email exist in the database.");
        return;
      }
      sendMessage(em, loggedInUser, receiver, subject.getText(), message.getText());
      ClassMgmtSysUtils
          .displayMessage("Message sent to " + receiverData.getText() + ".");
      ((Stage) message.getScene().getWindow()).close();
    }
  }
}
