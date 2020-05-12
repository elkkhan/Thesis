package com.thesis.neptun.controller.teacher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thesis.neptun.main.DatabaseFiller;
import com.thesis.neptun.main.TestMain;
import com.thesis.neptun.model.Result;
import com.thesis.neptun.util.NeptunUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;

public class TestModifyGradeController {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;

  @Test
  void modifyGrade()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    //current - new Result(S_JACKY_JUNIOR, C_NUMERICAL, 3),
    Result result = NeptunUtils.getCourseResult(db.S_JACKY_JUNIOR, db.C_NUMERICAL);
    assert (result != null);
    assertEquals("3", result.getGrade());
    ModifyGradeController mgc = new ModifyGradeController();
    Method modifyGrade = ModifyGradeController.class
        .getDeclaredMethod("changeGrade", EntityManager.class, String.class, Result.class);
    modifyGrade.setAccessible(true);
    modifyGrade.invoke(mgc, TestMain.entityManager, "5", result);
    result = NeptunUtils.getCourseResult(db.S_JACKY_JUNIOR, db.C_NUMERICAL);
    assert (result != null);
    assertEquals("5", result.getGrade());
  }
}
