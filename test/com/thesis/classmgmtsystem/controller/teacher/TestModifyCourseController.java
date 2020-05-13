package com.thesis.classmgmtsystem.controller.teacher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thesis.classmgmtsystem.main.DatabaseFiller;
import com.thesis.classmgmtsystem.main.TestMain;
import com.thesis.classmgmtsystem.model.Course;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;

public class TestModifyCourseController {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;

  @Test
  void modifyCourse()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    ModifyCourseController mcc = new ModifyCourseController();
    Method modifyCourse = ModifyCourseController.class
        .getDeclaredMethod("modifyCourse", EntityManager.class, Course.class, Course.class);
    modifyCourse.setAccessible(true);
    Course original = db.C_ALGANDDS;
    Course modified = new Course("New Name", original.getCourseCode(), original.getTeacher(), 7,
        original.getStartTime(), 54);
    modifyCourse.invoke(mcc, TestMain.entityManager, original, modified);
    Course retrieved = TestMain.entityManager.find(Course.class, db.C_ALGANDDS.getId());
    assertEquals(modified.getName(), retrieved.getName());
    assertEquals(original.getCourseCode(), retrieved.getCourseCode());
    assertEquals(original.getTeacher(), retrieved.getTeacher());
    assertEquals(modified.getCredit(), retrieved.getCredit());
    assertEquals(original.getStartTime(), retrieved.getStartTime());
    assertEquals(modified.getTimeoutMinutes(), retrieved.getTimeoutMinutes());
  }
}
