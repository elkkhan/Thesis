package com.thesis.classmgmtsystem.controller.student;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thesis.classmgmtsystem.controller.teacher.ClassLogsController;
import com.thesis.classmgmtsystem.main.DatabaseFiller;
import com.thesis.classmgmtsystem.main.TestMain;
import com.thesis.classmgmtsystem.model.AttendanceLog;
import com.thesis.classmgmtsystem.model.ClassLog;
import com.thesis.classmgmtsystem.model.Course;
import com.thesis.classmgmtsystem.model.Student;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestStudentAttendancePanelController {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;

  @Test
  @Order(1)
  void attendClass()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    StudentAttendancePanelController sapc = new StudentAttendancePanelController();
    Method attendClass = StudentAttendancePanelController.class
        .getDeclaredMethod("attendClass", EntityManager.class, ClassLog.class, Student.class);
    attendClass.setAccessible(true);

    ClassLogsController clc = new ClassLogsController();
    Method startClassLog = ClassLogsController.class
        .getDeclaredMethod("startClasslog", EntityManager.class, Course.class);
    startClassLog.setAccessible(true);

    ClassLog cl = (ClassLog) startClassLog.invoke(clc, TestMain.entityManager, db.C_NUMERICAL);
    AttendanceLog al = (AttendanceLog) attendClass
        .invoke(sapc, TestMain.entityManager, cl, db.S_JADON_SANCHO);
    assertTrue(cl.getAttendanceLogs().contains(al));
  }

  @Test
  @Order(2)
  void hasAttended()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    StudentAttendancePanelController sapc = new StudentAttendancePanelController();
    Method hasAttended = StudentAttendancePanelController.class
        .getDeclaredMethod("hasAttended", ClassLog.class, Student.class);
    hasAttended.setAccessible(true);
    boolean g = (boolean) hasAttended
        .invoke(sapc, db.C_NUMERICAL.getClassLogs().get(0), db.S_JADON_SANCHO);
    assertTrue(g);
  }
}
