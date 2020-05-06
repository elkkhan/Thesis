package com.thesis.neptun.controller;

import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Message;
import com.thesis.neptun.model.User;
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
import javax.persistence.NoResultException;

public class ComposeMessageWindowController implements Initializable {

  private static String receiverDataText = null;
  private User loggedInUser = MainWindowController.getLoggedInUser();
  @FXML
  private TextField receiverData;
  @FXML
  private TextField subject;
  @FXML
  private TextArea message;

  static String getReceiverData() {
    return receiverDataText;
  }

  static void getBackReceiverData(String data) {
    receiverDataText = data;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
  }

  void setReplyData(String receiverDataText, String subjectDataText) {
    receiverData.setText(receiverDataText);
    subject.setText(subjectDataText);
  }

  public void handleSearchButtonAction() {
    if (receiverData.getText().isEmpty()) {
      MainWindow.displayMessage("Neptun System", "Please enter the receipent name.");
    } else {
      receiverDataText = receiverData.getText();
      try {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/SearchResults.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Search Result");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
      } catch (Exception ignore) {}
      receiverData.setText(receiverDataText);
    }
  }

  public void handleSendButtonAction() {
    if (receiverData.getText().isEmpty()) {
      MainWindow.displayMessage("Neptun System", "Please enter the receiver's email.");
    } else if (subject.getText().isEmpty()) {
      MainWindow.displayMessage("Neptun System", "Please enter a subject.");
    } else if (message.getText().isEmpty()) {
      MainWindow.displayMessage("Neptun System", "Please enter you message.");
    } else {
      try {
        String query =
            "select email from (select email  from student union \n"
                + "select email  from teacher \n"
                + "order by email) as t where t.email like \"%"
                + receiverData.getText()
                + "%\"";
        MainWindow.entityManager.createNativeQuery(query).getSingleResult();
      } catch (NoResultException e) {
        MainWindow.displayMessage(
            "Neptun System", "No users with such email exist in the database.");
        return;
      }
      MainWindow.entityManager.getTransaction().begin();
      Message messageToDb =
          new Message(
              loggedInUser.getEmail(),
              receiverData.getText(),
              message.getText(),
              subject.getText());
      MainWindow.entityManager.persist(messageToDb);
      MainWindow.entityManager.getTransaction().commit();
      MainWindow.displayMessage("Neptun System", "Message sent to " + receiverData.getText() + ".");
      ((Stage) message.getScene().getWindow()).close();
    }
  }
}
