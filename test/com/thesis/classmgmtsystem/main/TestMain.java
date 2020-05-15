package com.thesis.classmgmtsystem.main;

import com.thesis.classmgmtsystem.util.ClassMgmtSysUtils;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TestMain {

  public static EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("test");
  public static EntityManager entityManager = emfactory.createEntityManager();
  public static DatabaseFiller TEST_DATABASE_REF;

  static {
    ClassMgmtSysUtils.setEntityManager(entityManager);
    TEST_DATABASE_REF = TestMain.fillTestDb();
  }

  private static DatabaseFiller fillTestDb() {
    try {
      DatabaseFiller testDatabaseFiller = new DatabaseFiller(entityManager);
      testDatabaseFiller.fill();
      return testDatabaseFiller;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
    }
    return null;
  }
}
