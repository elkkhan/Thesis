package com.thesis.neptun.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thesis.neptun.controller.common.ComposeMessageWindowController;
import com.thesis.neptun.controller.student.StudentAttendancePanelController;
import com.thesis.neptun.controller.teacher.ClassLogsController;
import com.thesis.neptun.controller.teacher.ModifyGradeController;
import com.thesis.neptun.main.DatabaseFiller;
import com.thesis.neptun.main.TestMain;
import com.thesis.neptun.model.ClassLog;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Result;
import com.thesis.neptun.model.Student;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;

public class TestNeptunUtils {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;


  @Test
  void countUnreadEmails() {
    ComposeMessageWindowController
        .sendMessage(TestMain.entityManager, db.S_CRIS_MOLTESANTI, db.T_PHIL_LEOTARDO,
            "1_TEST_COUNT_UNREAD_EMAIL", "QWERTY");
    ComposeMessageWindowController
        .sendMessage(TestMain.entityManager, db.T_BOB_TOMAS, db.T_PHIL_LEOTARDO,
            "2_TEST_COUNT_UNREAD_EMAIL", "YTREWQ");
    assertEquals(2, NeptunUtils.countUnreadEmails(db.T_PHIL_LEOTARDO));
  }

  @Test
  void isEmailUnique() {
    assertFalse(NeptunUtils.isEmailUnique(TestMain.entityManager, db.T_BOB_TOMAS.getEmail()));
    assertFalse(NeptunUtils.isEmailUnique(TestMain.entityManager, db.S_CRIS_MOLTESANTI.getEmail()));
    assertTrue(NeptunUtils.isEmailUnique(TestMain.entityManager, "abc.dbe@gmail.com"));
  }

  @Test
  void isNeptunUnique() {
    assertFalse(NeptunUtils.isNeptunUnique(TestMain.entityManager, db.T_BOB_TOMAS.getCode()));
    assertFalse(NeptunUtils.isNeptunUnique(TestMain.entityManager, db.S_CRIS_MOLTESANTI.getCode()));
    assertTrue(NeptunUtils.isNeptunUnique(TestMain.entityManager, "MIAR09"));
  }

  @Test
  void getAttendancePercentage()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    ClassLogsController clc = new ClassLogsController();
    Method startClass =
        ClassLogsController.class
            .getDeclaredMethod("startClasslog", EntityManager.class, Course.class);
    startClass.setAccessible(true);
    ClassLog cl = (ClassLog) startClass.invoke(clc, TestMain.entityManager, db.C_ANALYSIS);

    StudentAttendancePanelController sapc = new StudentAttendancePanelController();
    Method attendClass = StudentAttendancePanelController.class
        .getDeclaredMethod("attendClass", EntityManager.class, ClassLog.class,
            Student.class);
    attendClass.setAccessible(true);
    attendClass.invoke(sapc, TestMain.entityManager, cl, db.S_CRIS_MOLTESANTI);

    assertEquals(100, NeptunUtils.getAttendancePercentage(db.C_ANALYSIS, db.S_CRIS_MOLTESANTI));
  }

  @Test
  void getCourseResult()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    ModifyGradeController mgc = new ModifyGradeController();
    Method changeGrade = ModifyGradeController.class
        .getDeclaredMethod("changeGrade", EntityManager.class, String.class, Result.class);
    changeGrade.setAccessible(true);
    Result result = new Result(db.S_JADON_SANCHO, db.C_NUMERICAL, "None");
    changeGrade.invoke(mgc, TestMain.entityManager, "5", result);
    assertEquals(result, NeptunUtils.getCourseResult(db.S_JADON_SANCHO, db.C_NUMERICAL));
  }

  @Test
  void isValidPassword() {
    assertFalse(NeptunUtils.isValidPassword("simplepassword"));
    assertTrue(NeptunUtils.isValidPassword("complicatedPASSWORD6@5"));
  }

  @Test
  void isValidNeptunCode() {
    assertTrue(NeptunUtils.isValidNeptunCode("MVRO14"));
    assertFalse(NeptunUtils.isValidNeptunCode("QWERTY123"));
  }

  @Test
  void isInvalidCourseStartTime() {
    assertTrue(NeptunUtils.isInvalidCourseStartTime("123:15"));
    assertFalse(NeptunUtils.isInvalidCourseStartTime("21:15"));
  }

  @Test
  void isArgumentListEmptyString() {
    assertTrue(NeptunUtils.isArgumentListEmptyStrings("", "", "", ""));
    assertFalse(NeptunUtils.isArgumentListEmptyStrings("one", "two", "three"));
  }
}
