package com.thesis.neptun.controller.student;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thesis.neptun.controller.teacher.ModifyGradeController;
import com.thesis.neptun.main.DatabaseFiller;
import com.thesis.neptun.main.TestMain;
import com.thesis.neptun.model.Result;
import com.thesis.neptun.model.Student;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;

public class TestCheckGradesWinowController {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;

  @Test
  void calculateGpa()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    CheckGradesWindowController cgwc = new CheckGradesWindowController();
    Method calculateGpa = CheckGradesWindowController.class
        .getDeclaredMethod("calculateGpa", Student.class);
    calculateGpa.setAccessible(true);

    ModifyGradeController mgc = new ModifyGradeController();
    Method changeGrade = ModifyGradeController.class
        .getDeclaredMethod("changeGrade", EntityManager.class, String.class, Result.class);
    changeGrade.setAccessible(true);

    Result analysis = new Result(db.S_CRIS_MOLTESANTI, db.C_ANALYSIS, "None");
    Result distrib = new Result(db.S_CRIS_MOLTESANTI, db.C_DISTRIB, "None");
    Result cpp = new Result(db.S_CRIS_MOLTESANTI, db.C_CPP, "None");
    changeGrade.invoke(mgc, TestMain.entityManager, "5", analysis);
    changeGrade.invoke(mgc, TestMain.entityManager, "4", distrib);
    changeGrade.invoke(mgc, TestMain.entityManager, "3", cpp);
    assertEquals((long) 4, calculateGpa.invoke(cgwc, db.S_CRIS_MOLTESANTI));
  }
}
