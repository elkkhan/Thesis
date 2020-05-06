package com.thesis.neptun.controller;

import com.thesis.neptun.auth.AuthManager;
import com.thesis.neptun.main.MainWindow;
import com.thesis.neptun.model.Student;
import com.thesis.neptun.model.Student_;
import com.thesis.neptun.model.Teacher;
import com.thesis.neptun.model.Teacher_;
import com.thesis.neptun.model.User;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

public class MainWindowController implements Initializable {

  private static userType loggedInUserType = null;
  private static User loggedInUser = null;
  private final String alertTitle = "Neptun System";
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

  private boolean isValidPassword(String password) {
    if (password.length() < 8) {
      return false;
    }
    Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(password);
    if (!m.find()) {
      return false;
    }
    if (password.equals(password.toLowerCase()) || password.equals(password.toUpperCase())) {
      return false;
    }
    p = Pattern.compile("([0-9])");
    m = p.matcher(password);
    return m.find();
  }

  private boolean isValidNeptunCode(String neptun) {
    if (neptun.length() != 6) {
      return false;
    }
    Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(neptun);
    if (m.find()) {
      return false;
    }
    p = Pattern.compile("([0-9])");
    m = p.matcher(neptun);
    return m.find();
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

  private void registerHandleTeacherStudent(
      TextField fullname,
      TextField email,
      TextField neptun,
      PasswordField password,
      PasswordField password_confirm,
      TextField role,
      boolean isStudent)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    String neptunStudent = "\"" + neptun.getText().toUpperCase() + "\"";
    String neptunTeacher = "\"" + neptun.getText().toUpperCase() + "\"";
    String emailStudent = "\"" + email.getText().toUpperCase() + "\"";
    String emailTeacher = "\"" + email.getText().toUpperCase() + "\"";

    String sameNeptunStudents = "select count(code) from student where code=" + neptunStudent;
    String sameNeptunTeachers = "select count(code) from teacher where code=" + neptunTeacher;
    String sameEmailStudents = "select count(email) from student where email=" + emailStudent;
    String sameEmailTeachers = "select count(email) from teacher where email=" + emailTeacher;

    long sameNeptunStudentsCount,
        sameNeptunTeachersCount,
        sameEmailStudentsCount,
        sameEmailTeachersCount;
    sameNeptunStudentsCount =
        (long) MainWindow.entityManager.createNativeQuery(sameNeptunStudents).getSingleResult();
    sameNeptunTeachersCount =
        (long) MainWindow.entityManager.createNativeQuery(sameNeptunTeachers).getSingleResult();
    sameEmailStudentsCount =
        (long) MainWindow.entityManager.createNativeQuery(sameEmailStudents).getSingleResult();
    sameEmailTeachersCount =
        (long) MainWindow.entityManager.createNativeQuery(sameEmailTeachers).getSingleResult();

    if (!isValidNeptunCode(neptun.getText())) {
      MainWindow.displayMessage(
          alertTitle,
          "Neptun code should be 6 characters long, contain at least 1 digin "
              + "and not contain any special characters.\n"
              + "Please re-enter.");
      neptun.clear();
    } else if (!isValidPassword(password.getText())) {
      MainWindow.displayMessage(
          alertTitle,
          "Password should be at least 8 characters long, contain at least 1 "
              + "digit, lower and upper-case letter.\nPlease re-enter.");
      password.clear();
      password_confirm.clear();
    } else if (!password.getText().equals(password_confirm.getText())) {
      MainWindow.displayMessage(alertTitle, "Passwords don't match, please re-enter\n");
      password.clear();
      password_confirm.clear();
    } else if (sameNeptunStudentsCount > 0 || sameNeptunTeachersCount > 0) {
      MainWindow.displayMessage(
          alertTitle, "User with Neptun code " + neptun.getText() + " already exists.");
      neptun.clear();
    } else if (sameEmailStudentsCount > 0 || sameEmailTeachersCount > 0) {
      MainWindow.displayMessage(
          alertTitle, "User with email " + email.getText() + " already exists.");
      email.clear();
    } else {
      MainWindow.entityManager.getTransaction().begin();
      if (isStudent) {
        Student student =
            auth.registerStudent(
                neptun.getText(),
                password.getText().toCharArray(),
                fullname.getText(),
                email.getText());
        MainWindow.entityManager.persist(student);
      } else {
        Teacher teacher =
            auth.registerTeacher(
                neptun.getText().toUpperCase(),
                password.getText().toCharArray(),
                fullname.getText(),
                email.getText(),
                role.getText());
        MainWindow.entityManager.persist(teacher);
      }
      MainWindow.entityManager.getTransaction().commit();
      MainWindow.displayMessage(
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

        loggedInUser = MainWindow.entityManager.createQuery(studentQuery).getSingleResult();
        loggedInUserType = userType.student;

        MainWindow.displayMessage(
            alertTitle, "Succesfull login.\nPress OK to go to the student panel.");
        closeWindow();
        MainWindow.loadWindow("/StudentPanel.fxml", "Student Panel");
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
        MainWindow.displayMessage(
            alertTitle, "Succesfull login.\nPress OK to go to the teacher panel.");
        closeWindow();
        MainWindow.loadWindow("/TeacherPanel.fxml", "Teacher Panel");
        return;
      }
    } catch (NoResultException ignore) {
    }
    MainWindow.displayMessage(alertTitle, "Login failed.\nPlease enter your credentials again.");
    password.clear();
  }

  private void closeWindow() {
    ((Stage) neptun.getScene().getWindow()).close();
  }

  enum userType {
    teacher,
    student
  }
}
