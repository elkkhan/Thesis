package com.thesis.classmgmtsystem.controller.common;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thesis.classmgmtsystem.main.TestMain;
import com.thesis.classmgmtsystem.model.Student;
import com.thesis.classmgmtsystem.model.Teacher;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;

public class TestMainWindowController {

  @Test
  void registerTeacher()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    MainWindowController mwc = new MainWindowController();
    Method registerTeacher = MainWindowController.class.getDeclaredMethod("registerTeacher",
        EntityManager.class, String.class, char[].class, String.class, String.class, String.class);
    registerTeacher.setAccessible(true);
    Teacher teacher = (Teacher) registerTeacher
        .invoke(mwc, TestMain.entityManager, "ABCD12", ("password@1A").toCharArray(),
            "Test Teacher", "test@teacher.com", "Lecturer");
    List<Teacher> allTeachers = TestMain.entityManager
        .createNativeQuery("select * from user where user_type=\"TEACHER\"", Teacher.class)
        .getResultList();
    assertTrue(allTeachers.contains(teacher));
  }

  @Test
  void registerStudent()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    MainWindowController mwc = new MainWindowController();
    Method registerStudent = MainWindowController.class
        .getDeclaredMethod("registerStudent", EntityManager.class, String.class, char[].class,
            String.class, String.class);
    registerStudent.setAccessible(true);
    Student student = (Student) registerStudent
        .invoke(mwc, TestMain.entityManager, "QWER55", ("password@1A").toCharArray(),
            "Test Student", "test@student.com");
    List<Student> allStudents = TestMain.entityManager
        .createNativeQuery("select * from user where user_type=\"STUDENT\"", Student.class)
        .getResultList();
    assertTrue(allStudents.contains(student));
  }
}
