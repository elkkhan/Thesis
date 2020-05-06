package com.thesis.neptun.main;

import java.io.IOException;
import java.util.TimeZone;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class MainWindow extends Application {

  private static EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("neptun");
  public static EntityManager entityManager = emfactory.createEntityManager();

  public static void main(String[] args) {
    launch(args);
  }

  public static void displayMessage(String title, String context) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText("Neptun System");
    alert.setContentText(context);
    alert.showAndWait();
  }

  public static void loadWindow(String fxmlPath, String title) {
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
      Stage stage = new Stage();
      Parent root = FXMLLoader.load(MainWindow.class.getResource(fxmlPath));
      Scene scene = new Scene(root);
      stage.setTitle(title);
      stage.setScene(scene);
      stage.setResizable(false);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void start(Stage stage) throws IOException {
    Parent root = FXMLLoader.load(MainWindow.class.getResource("/MainWindow.fxml"));
    Scene scene = new Scene(root);
    scene.getStylesheets().add("learning.css");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.setTitle("Neptun System");
    stage.show();
  }
}