package com.thesis.classmgmtsystem.controller.teacher;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thesis.classmgmtsystem.main.DatabaseFiller;
import com.thesis.classmgmtsystem.main.TestMain;
import com.thesis.classmgmtsystem.model.ClassLog;
import com.thesis.classmgmtsystem.model.Course;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;

public class TestClassLogsController {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;

  @Test
  void startClassLog()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    ClassLogsController clc = new ClassLogsController();
    Method startClassLog = ClassLogsController.class
        .getDeclaredMethod("startClasslog", EntityManager.class, Course.class);
    startClassLog.setAccessible(true);
    ClassLog cl = (ClassLog) startClassLog.invoke(clc, TestMain.entityManager, db.C_NUMERICAL);
    assertTrue(db.C_NUMERICAL.getClassLogs().contains(cl));
  }


}
