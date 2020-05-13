package com.thesis.classmgmtsystem.controller.student;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thesis.classmgmtsystem.main.DatabaseFiller;
import com.thesis.classmgmtsystem.main.TestMain;
import com.thesis.classmgmtsystem.model.Course;
import com.thesis.classmgmtsystem.model.Student;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;

public class TestTakeNewCourseWindowController {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;

  @Test
  void takeCourse()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    TakeNewCourseWindowController tnwc = new TakeNewCourseWindowController();
    Method takeCourse = TakeNewCourseWindowController.class
        .getDeclaredMethod("takeCourse", EntityManager.class, Student.class, Course.class);
    takeCourse.setAccessible(true);
    takeCourse.invoke(tnwc, TestMain.entityManager, db.S_JADON_SANCHO, db.C_ALGANDDS);
    assertTrue(db.C_ALGANDDS.getStudents().contains(db.S_JADON_SANCHO));
    assertTrue(db.S_JADON_SANCHO.getCourses().contains(db.C_ALGANDDS));
  }
}
