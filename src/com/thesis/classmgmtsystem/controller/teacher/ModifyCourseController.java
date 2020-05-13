package com.thesis.classmgmtsystem.controller.teacher;

import com.thesis.classmgmtsystem.controller.common.MainWindowController;
import com.thesis.classmgmtsystem.exception.InvalidCourseException;
import com.thesis.classmgmtsystem.main.MainWindow;
import com.thesis.classmgmtsystem.model.Course;
import com.thesis.classmgmtsystem.model.Teacher;
import com.thesis.classmgmtsystem.util.ClassMgmtSysUtils;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.persistence.EntityManager;

public class ModifyCourseController implements Initializable {

  private EntityManager em = MainWindow.entityManager;
  private Teacher teacher = (Teacher) MainWindowController.getLoggedInUser();
  @FXML
  private TextField nameModify;
  @FXML
  private TextField codeModify;
  @FXML
  private TextField creditModify;
  @FXML
  private TextField startTimeModify;
  @FXML
  private TextField timeoutMinutes;

  private Course selectedCourse;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    selectedCourse = TeacherPanelController.getSelectedCourse();

    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    nameModify.setText(selectedCourse.getName());
    codeModify.setText(selectedCourse.getCourseCode());
    creditModify.setText(String.valueOf(selectedCourse.getCredit()));
    startTimeModify.setText(selectedCourse.getStartTime());
    timeoutMinutes.setText(String.valueOf(selectedCourse.getTimeoutMinutes()));
  }

  public void handleSaveButtonAction() {
    try {
      Course modifiedCourse = CreateCourseController
          .constructCourse(em, nameModify.getText(), codeModify.getText(), teacher,
              creditModify.getText(), startTimeModify.getText(), timeoutMinutes.getText(), true);
      modifyCourse(em, selectedCourse, modifiedCourse);
    } catch (InvalidCourseException e) {
      ClassMgmtSysUtils.displayMessage(e.getMessage());
    }

    ClassMgmtSysUtils.displayMessage("Changes saved.");
    ((Stage) nameModify.getScene().getWindow()).close();
    ClassMgmtSysUtils.loadWindow("/fxml/teacher/TeacherPanel.fxml", "Teacher Panel");
  }

  private void modifyCourse(EntityManager em, Course original, Course modified) {
    em.getTransaction().begin();
    original.setName(modified.getName());
    original.setCourseCode(modified.getCourseCode());
    original.setTeacher(modified.getTeacher());
    original.setCredit(modified.getCredit());
    original.setStartTime(modified.getStartTime());
    original.setTimeoutMinutes(modified.getTimeoutMinutes());
    em.getTransaction().commit();
  }

  @FXML
  public void handleCancelButtonAction() {
    ClassMgmtSysUtils.loadWindow("/fxml/teacher/TeacherPanel.fxml", "Teacher Panel");
    ((Stage) nameModify.getScene().getWindow()).close();
  }
}
