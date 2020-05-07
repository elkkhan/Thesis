package com.thesis.neptun.controller;

import com.thesis.neptun.model.Message;
import com.thesis.neptun.util.NeptunUtils;
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

public class OpenMessageController implements Initializable {

  private Message message;

  @FXML
  private TextArea messageField;
  @FXML
  private TextField sender;
  @FXML
  private TextField subject;
  @FXML
  private TextField date;

  public void initialize(URL url, ResourceBundle rb) {
    messageField.setWrapText(true);
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    message = InboxWindowController.getOpenedMessage();
    messageField.setText(message.getMessage());
    sender.setText(message.getSender().getName());
    subject.setText(message.getSubject());
    date.setText(message.getDate());

    sender.setDisable(true);
    subject.setDisable(true);
    date.setDisable(true);
    messageField.setEditable(false);
  }

  public void handleReplyButtonAction() {
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
      Stage stage = new Stage();
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ComposeMessageWindow.fxml"));
      Parent root = fxmlLoader.load();
      Scene scene = new Scene(root);
      stage.setTitle("Reply to " + sender.getText());
      stage.setScene(scene);
      stage.setResizable(false);
      ComposeMessageWindowController controller = fxmlLoader.getController();
      controller.setReplyData(message.getSender().getEmail(), "Re:" + subject.getText());
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void handleBackButtonAction() {
    ((Stage) sender.getScene().getWindow()).close();
    NeptunUtils.loadWindow("/InboxWindow.fxml", "Inbox");
  }
}
