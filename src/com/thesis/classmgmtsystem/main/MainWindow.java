package com.thesis.classmgmtsystem.main;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class MainWindow extends Application {

  private static EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("venera");
  public static EntityManager entityManager = emfactory.createEntityManager();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws IOException {
    Parent root = FXMLLoader.load(MainWindow.class.getResource("/fxml/common/MainWindow.fxml"));
    Scene scene = new Scene(root);
    scene.getStylesheets().add("css/venera.css");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.setTitle("Class Management System");
    stage.show();
  }
}