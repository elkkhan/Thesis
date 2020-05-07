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