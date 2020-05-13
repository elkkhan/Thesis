package com.thesis.classmgmtsystem.controller.common;

import com.thesis.classmgmtsystem.auth.AuthManager;
import com.thesis.classmgmtsystem.exception.RegistrationException;
import com.thesis.classmgmtsystem.model.Student;
import com.thesis.classmgmtsystem.model.User;
import com.thesis.classmgmtsystem.main.MainWindow;
import com.thesis.classmgmtsystem.model.Student_;
import com.thesis.classmgmtsystem.model.Teacher;
import com.thesis.classmgmtsystem.model.Teacher_;
import com.thesis.classmgmtsystem.util.ClassMgmtSysUtils;
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

  private static userType loggedInUserType = null;
  private static User loggedInUser = null;
  private static AuthManager auth = new AuthManager();
  public static final String alertTitle = "Class Management System";
  private EntityManager em = MainWindow.entityManager;
  @FXML
  private TextField veneraId;
  @FXML
  private PasswordField password;
  @FXML
  private TextField registerStudentFullName,
      registerTeacherFullName,
      registerStudentVenera,
      registerTeacherVenera,
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

  public static void resetLoggedInUser() {
    loggedInUser = null;
    loggedInUserType = null;
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"));
  }

  @FXML
  public void handleRegisterButtonAction()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    if (studentTab.isSelected()) {
      registerHandleTeacherStudent(
          registerStudentFullName,
          registerStudentEmail,
          registerStudentVenera,
          passwordR,
          passwordR_confirm,
          registerTeacherRole,
          true);
    } else if (teacherTab.isSelected()) {
      registerHandleTeacherStudent(
          registerTeacherFullName,
          registerTeacherEmail,
          registerTeacherVenera,
          passwordRT,
          passwordRT_confirm,
          registerTeacherRole,
          false);
    }
  }

  private Teacher registerTeacher(EntityManager em, String venera, char[] password, String fullName,
      String email, String role)
      throws RegistrationException, InvalidKeySpecException, NoSuchAlgorithmException {
    @SuppressWarnings("unused") boolean goodToGo = ClassMgmtSysUtils
        .isRegistrationDataValid(em, venera, password, email);
    em.getTransaction().begin();
    Teacher teacher =
        auth.registerTeacher(
            venera.toUpperCase(),
            password,
            fullName,
            email,
            role);
    em.persist(teacher);
    em.getTransaction().commit();
    return teacher;
  }

  private Student registerStudent(EntityManager em, String venera, char[] password, String fullName,
      String email)
      throws RegistrationException, InvalidKeySpecException, NoSuchAlgorithmException {
    @SuppressWarnings("unused") boolean goodToGo = ClassMgmtSysUtils
        .isRegistrationDataValid(em, venera, password, email);
    em.getTransaction().begin();
    Student student =
        auth.registerStudent(
            venera.toUpperCase(),
            password,
            fullName,
            email);
    em.persist(student);
    em.getTransaction().commit();
    return student;
  }

  private void registerHandleTeacherStudent(
      TextField fullname,
      TextField email,
      TextField venera,
      PasswordField password,
      PasswordField password_confirm,
      TextField role,
      boolean isStudent)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    User newUser;
    if (!password.getText().equals(password_confirm.getText())) {
      ClassMgmtSysUtils.displayMessage("Passwords don't match, please re-enter\n");
      password.clear();
      password_confirm.clear();
    } else {
      try {
        if (isStudent) {
          newUser = registerStudent(em, venera.getText(), password.getText().toCharArray(),
              fullname.getText(), email.getText());
        } else {
          newUser = registerTeacher(em, venera.getText(), password.getText().toCharArray(),
              fullname.getText(), email.getText(), role.getText());
        }
      } catch (RegistrationException e) {
        ClassMgmtSysUtils.displayMessage(e.getMessage());
        return;
      }
      ClassMgmtSysUtils.displayMessage(
          "User " + newUser.getCode() + "," + newUser.getName() + " " + "succesfully registered.");

      registerStudentFullName.clear();
      registerStudentVenera.clear();
      registerTeacherVenera.clear();
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
      if (auth.authenticateStudent(veneraId.getText(), password.getText().toCharArray())) {
        CriteriaBuilder cb = MainWindow.entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> studentQuery = cb.createQuery(Student.class);
        Root<Student> student = studentQuery.from(Student.class);
        Path<String> studentVenera = student.get(Student_.code);
        studentQuery.where(cb.equal(studentVenera, veneraId.getText()));
        studentQuery.select(student);
        loggedInUser = em.createQuery(studentQuery).getSingleResult();
        loggedInUserType = userType.student;
        ClassMgmtSysUtils.displayMessage(
            "Succesfull login.\nPress OK to go to the student panel.");
        closeWindow();
        ClassMgmtSysUtils.loadWindow("/fxml/student/StudentPanel.fxml", "Student Panel");
        return;
      }
    } catch (NoResultException ignore) {
    }

    try {
      if (auth.authenticateTeacher(veneraId.getText(), password.getText().toCharArray())) {
        CriteriaBuilder cb = MainWindow.entityManager.getCriteriaBuilder();
        CriteriaQuery<Teacher> teacherQuery = cb.createQuery(Teacher.class);
        Root<Teacher> teacher = teacherQuery.from(Teacher.class);
        Path<String> teacherVenera = teacher.get(Teacher_.code);
        teacherQuery.where(cb.equal(teacherVenera, veneraId.getText()));
        teacherQuery.select(teacher);
        loggedInUser = MainWindow.entityManager.createQuery(teacherQuery).getSingleResult();
        loggedInUserType = userType.teacher;
        ClassMgmtSysUtils.displayMessage(
            "Succesfull login.\nPress OK to go to the teacher panel.");
        closeWindow();
        ClassMgmtSysUtils.loadWindow("/fxml/teacher/TeacherPanel.fxml", "Teacher Panel");
        return;
      }
    } catch (NoResultException ignore) {
    }
    ClassMgmtSysUtils
        .displayMessage("Login failed.\nPlease enter your credentials again.");
    password.clear();
  }

  private void closeWindow() {
    ((Stage) veneraId.getScene().getWindow()).close();
  }

  enum userType {
    teacher,
    student
  }
}
