package com.thesis.neptun.controller;

import com.thesis.neptun.auth.AuthManager;
import com.thesis.neptun.exception.RegistrationException;
import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Student;
import com.thesis.neptun.model.Student_;
import com.thesis.neptun.model.Teacher;
import com.thesis.neptun.model.Teacher_;
import com.thesis.neptun.model.User;
import com.thesis.neptun.util.NeptunUtils;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

public class MainWindowController implements Initializable {

  enum userType {
    teacher,
    student
  }
  private static userType loggedInUserType = null;
  private static User loggedInUser = null;
  private final String alertTitle = "Neptun System";
  private EntityManager em = MainWindow.entityManager;
  private AuthManager auth;
  @FXML
  private TextField neptun;
  @FXML
  private PasswordField password;
  @FXML
  private TextField registerStudentFullName,
      registerTeacherFullName,
      registerStudentNeptun,
      registerTeacherNeptun,
      registerStudentEmail,
      registerTeacherEmail,
      registerTeacherRole;
  @FXML
  private PasswordField passwordR, passwordRT, passwordR_confirm, passwordRT_confirm;
  @FXML
  private Tab studentTab, teacherTab, logInTab;
  @FXML
  private TabPane tabPane;

  static userType getLoggedInUserType() {
    return loggedInUserType;
  }

  public static User getLoggedInUser() {
    return loggedInUser;
  }

  static void resetLoggedInUser() {
    loggedInUser = null;
    loggedInUserType = null;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
    auth = new AuthManager();
  }
  @FXML
  public void handleRegisterButtonAction()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    if (studentTab.isSelected()) {
      registerHandleTeacherStudent(
          registerStudentFullName,
          registerStudentEmail,
          registerStudentNeptun,
          passwordR,
          passwordR_confirm,
          registerTeacherRole,
          true);
    } else if (teacherTab.isSelected()) {
      registerHandleTeacherStudent(
          registerTeacherFullName,
          registerTeacherEmail,
          registerTeacherNeptun,
          passwordRT,
          passwordRT_confirm,
          registerTeacherRole,
          false);
    }
  }




  private void registerTeacher(EntityManager em, String neptun, char[] password, String fullName,
      String email, String role)
      throws RegistrationException, InvalidKeySpecException, NoSuchAlgorithmException {
    @SuppressWarnings("unused") boolean goodToGo = NeptunUtils.isRegistrationDataValid(em, neptun, password, email);
    em.getTransaction().begin();
    Teacher teacher =
        auth.registerTeacher(
            neptun.toUpperCase(),
            password,
            fullName,
            email,
            role);
    em.persist(teacher);
    em.getTransaction().commit();
  }

  private void registerStudent(EntityManager em, String neptun, char[] password, String fullName,
      String email)
      throws RegistrationException, InvalidKeySpecException, NoSuchAlgorithmException {
    @SuppressWarnings("unused") boolean goodToGo = NeptunUtils.isRegistrationDataValid(em, neptun, password, email);
    em.getTransaction().begin();
    Student student =
        auth.registerStudent(
            neptun.toUpperCase(),
            password,
            fullName,
            email);
    em.persist(student);
    em.getTransaction().commit();
  }

  private void registerHandleTeacherStudent(
      TextField fullname,
      TextField email,
      TextField neptun,
      PasswordField password,
      PasswordField password_confirm,
      TextField role,
      boolean isStudent)
      throws NoSuchAlgorithmException, InvalidKeySpecException {

    if (!password.getText().equals(password_confirm.getText())) {
      NeptunUtils.displayMessage(alertTitle, "Passwords don't match, please re-enter\n");
      password.clear();
      password_confirm.clear();
    } else {
      try {
        if (isStudent) {
          registerStudent(em, neptun.getText(), password.getText().toCharArray(),
              fullname.getText(), email.getText());
        } else {
          registerTeacher(em, neptun.getText(), password.getText().toCharArray(),
              fullname.getText(), email.getText(), role.getText());
        }
      } catch (RegistrationException e) {
        NeptunUtils.displayMessage(alertTitle, e.getMessage());
        return;
      }
      NeptunUtils.displayMessage(
          alertTitle,
          "User " + neptun.getText() + "," + fullname.getText() + " " + "succesfully registered.");

      registerStudentFullName.clear();
      registerStudentNeptun.clear();
      registerTeacherNeptun.clear();
      registerStudentEmail.clear();
      passwordR_confirm.clear();
      passwordR.clear();
      registerTeacherEmail.clear();
      registerTeacherFullName.clear();
      registerTeacherRole.clear();
      passwordRT.clear();
      passwordRT_confirm.clear();
      tabPane.getSelectionModel().select(logInTab);
    }
  }

  @FXML
  public void handleLoginButtonAction() throws NoSuchAlgorithmException, InvalidKeySpecException {
    try {
      if (auth.authenticateStudent(neptun.getText(), password.getText().toCharArray())) {
        CriteriaBuilder cb = MainWindow.entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> studentQuery = cb.createQuery(Student.class);
        Root<Student> student = studentQuery.from(Student.class);
        Path<String> studentNeptun = student.get(Student_.code);
        studentQuery.where(cb.equal(studentNeptun, neptun.getText()));
        studentQuery.select(student);
        loggedInUser = em.createQuery(studentQuery).getSingleResult();
        loggedInUserType = userType.student;
        NeptunUtils.displayMessage(
            alertTitle, "Succesfull login.\nPress OK to go to the student panel.");
        closeWindow();
        NeptunUtils.loadWindow("/StudentPanel.fxml", "Student Panel");
        return;
      }
    } catch (NoResultException ignore) {
    }

    try {
      if (auth.authenticateTeacher(neptun.getText(), password.getText().toCharArray())) {
        CriteriaBuilder cb = MainWindow.entityManager.getCriteriaBuilder();
        CriteriaQuery<Teacher> teacherQuery = cb.createQuery(Teacher.class);
        Root<Teacher> teacher = teacherQuery.from(Teacher.class);
        Path<String> teacherNeptun = teacher.get(Teacher_.code);
        teacherQuery.where(cb.equal(teacherNeptun, neptun.getText()));
        teacherQuery.select(teacher);
        loggedInUser = MainWindow.entityManager.createQuery(teacherQuery).getSingleResult();
        loggedInUserType = userType.teacher;
        NeptunUtils.displayMessage(
            alertTitle, "Succesfull login.\nPress OK to go to the teacher panel.");
        closeWindow();
        NeptunUtils.loadWindow("/TeacherPanel.fxml", "Teacher Panel");
        return;
      }
    } catch (NoResultException ignore) {
    }
    NeptunUtils.displayMessage(alertTitle, "Login failed.\nPlease enter your credentials again.");
    password.clear();
  }

  private void closeWindow() {
    ((Stage) neptun.getScene().getWindow()).close();
  }


}
