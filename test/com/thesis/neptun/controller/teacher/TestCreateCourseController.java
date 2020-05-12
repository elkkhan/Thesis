package com.thesis.neptun.controller.teacher;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thesis.neptun.exception.InvalidCourseException;
import com.thesis.neptun.main.DatabaseFiller;
import com.thesis.neptun.main.TestMain;
import com.thesis.neptun.model.Course;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestCreateCourseController {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;
  private static Course newCourse;

  @Test
  @Order(1)
  void constructCourse() throws InvalidCourseException {
    assertThrows(InvalidCourseException.class,
        () -> CreateCourseController
            .constructCourse(TestMain.entityManager, "New Course", "NEWCOURSE-99", db.T_BOB_TOMAS,
                "10", "abc", "500", false));
    newCourse = CreateCourseController
        .constructCourse(TestMain.entityManager, "New Course", "NEWCOURSE-99", db.T_BOB_TOMAS,
            "5", "15:00", "5", false);
  }

  @Test
  @Order(2)
  void createCourse()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    CreateCourseController ccc = new CreateCourseController();
    Method createCourse = CreateCourseController.class
        .getDeclaredMethod("createCourse", EntityManager.class, Course.class);
    createCourse.setAccessible(true);
    createCourse.invoke(ccc, TestMain.entityManager, newCourse);
    assertTrue(TestMain.entityManager.createNativeQuery("select * from course", Course.class)
        .getResultList().contains(newCourse));
  }
}
