package com.thesis.classmgmtsystem.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thesis.classmgmtsystem.controller.common.ComposeMessageWindowController;
import com.thesis.classmgmtsystem.controller.student.StudentAttendancePanelController;
import com.thesis.classmgmtsystem.controller.teacher.ClassLogsController;
import com.thesis.classmgmtsystem.controller.teacher.ModifyGradeController;
import com.thesis.classmgmtsystem.main.DatabaseFiller;
import com.thesis.classmgmtsystem.main.TestMain;
import com.thesis.classmgmtsystem.model.ClassLog;
import com.thesis.classmgmtsystem.model.Course;
import com.thesis.classmgmtsystem.model.Result;
import com.thesis.classmgmtsystem.model.Student;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;

public class TestClassMgmtSysUtils {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;

  @Test
  void countUnreadEmails() {
    ComposeMessageWindowController
        .sendMessage(TestMain.entityManager, db.S_CRIS_MOLTESANTI, db.T_PHIL_LEOTARDO,
            "1_TEST_COUNT_UNREAD_EMAIL", "QWERTY");
    ComposeMessageWindowController
        .sendMessage(TestMain.entityManager, db.T_BOB_TOMAS, db.T_PHIL_LEOTARDO,
            "2_TEST_COUNT_UNREAD_EMAIL", "YTREWQ");
    assertEquals(2, ClassMgmtSysUtils.countUnreadEmails(db.T_PHIL_LEOTARDO));
  }

  @Test
  void isEmailUnique() {
    assertFalse(ClassMgmtSysUtils.isEmailUnique(TestMain.entityManager, db.T_BOB_TOMAS.getEmail()));
    assertFalse(ClassMgmtSysUtils.isEmailUnique(TestMain.entityManager, db.S_CRIS_MOLTESANTI.getEmail()));
    assertTrue(ClassMgmtSysUtils.isEmailUnique(TestMain.entityManager, "abc.dbe@gmail.com"));
  }

  @Test
  void isVeneraUnique() {
    assertFalse(ClassMgmtSysUtils.isVeneraUnique(TestMain.entityManager, db.T_BOB_TOMAS.getCode()));
    assertFalse(ClassMgmtSysUtils.isVeneraUnique(TestMain.entityManager, db.S_CRIS_MOLTESANTI.getCode()));
    assertTrue(ClassMgmtSysUtils.isVeneraUnique(TestMain.entityManager, "MIAR09"));
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

    assertEquals(100, ClassMgmtSysUtils.getAttendancePercentage(db.C_ANALYSIS, db.S_CRIS_MOLTESANTI));
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
    assertEquals(result, ClassMgmtSysUtils.getCourseResult(db.S_JADON_SANCHO, db.C_NUMERICAL));
  }

  @Test
  void isValidPassword() {
    assertFalse(ClassMgmtSysUtils.isValidPassword("simplepassword"));
    assertTrue(ClassMgmtSysUtils.isValidPassword("complicatedPASSWORD6@5"));
  }

  @Test
  void isValidVeneraCode() {
    assertTrue(ClassMgmtSysUtils.isValidVeneraCode("MVRO14"));
    assertFalse(ClassMgmtSysUtils.isValidVeneraCode("QWERTY123"));
  }

  @Test
  void isInvalidCourseStartTime() {
    assertTrue(ClassMgmtSysUtils.isInvalidCourseStartTime("123:15"));
    assertFalse(ClassMgmtSysUtils.isInvalidCourseStartTime("21:15"));
  }

  @Test
  void isArgumentListEmptyString() {
    assertTrue(ClassMgmtSysUtils.isArgumentListEmptyStrings("", "", "", ""));
    assertFalse(ClassMgmtSysUtils.isArgumentListEmptyStrings("one", "two", "three"));
  }
}
