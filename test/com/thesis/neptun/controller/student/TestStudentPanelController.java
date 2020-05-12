package com.thesis.neptun.controller.student;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.thesis.neptun.main.DatabaseFiller;
import com.thesis.neptun.main.TestMain;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Student;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;

public class TestStudentPanelController {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;

  @Test
  void dropCourse()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    StudentPanelController spc = new StudentPanelController();
    Method dropCourse = StudentPanelController.class
        .getDeclaredMethod("dropCourse", EntityManager.class, Student.class, Course.class);
    dropCourse.setAccessible(true);
    dropCourse.invoke(spc, TestMain.entityManager, db.S_CRIS_MOLTESANTI, db.C_ANALYSIS);
    assertFalse(db.S_CRIS_MOLTESANTI.getCourses().contains(db.C_ANALYSIS));
    assertFalse(db.C_ANALYSIS.getStudents().contains(db.S_CRIS_MOLTESANTI));
  }
}
