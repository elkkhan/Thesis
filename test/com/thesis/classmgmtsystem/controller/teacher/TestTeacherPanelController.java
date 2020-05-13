package com.thesis.classmgmtsystem.controller.teacher;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.thesis.classmgmtsystem.main.DatabaseFiller;
import com.thesis.classmgmtsystem.main.TestMain;
import com.thesis.classmgmtsystem.model.Course;
import com.thesis.classmgmtsystem.model.Student;
import com.thesis.classmgmtsystem.model.Teacher;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;

public class TestTeacherPanelController {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;

  @Test
  void deleteCourse()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    TeacherPanelController tpc = new TeacherPanelController();
    Method deleteCourse = TeacherPanelController.class
        .getDeclaredMethod("deleteCourse", EntityManager.class, Course.class);
    deleteCourse.setAccessible(true);

    deleteCourse.invoke(tpc, TestMain.entityManager, db.C_DISCRETE);
    List<Course> allCourses = TestMain.entityManager
        .createNativeQuery("select * from course", Course.class).getResultList();
    assertFalse(allCourses.contains(db.C_DISCRETE));
    Teacher teacher = TestMain.entityManager
        .find(Teacher.class, db.C_DISCRETE.getTeacher().getId());
    assertFalse(teacher.getCourses().contains(db.C_DISCRETE));
    List<Student> students = TestMain.entityManager
        .createNativeQuery("select * from user where USER_TYPE=\"STUDENT\"", Student.class)
        .getResultList();
    for (Student student : students) {
      assertFalse(student.getCourses().contains(db.C_DISCRETE));
    }
  }
}
