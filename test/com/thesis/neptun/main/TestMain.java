package com.thesis.neptun.main;

import com.thesis.neptun.util.NeptunUtils;
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
    NeptunUtils.setEntityManager(entityManager);
    TEST_DATABASE_REF = TestMain.fillTestDb();
  }

  private static DatabaseFiller fillTestDb() {
    try {
      DatabaseFiller testDatabaseFiller = new DatabaseFiller();
      testDatabaseFiller.fill(entityManager);
      return testDatabaseFiller;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
    }
    return null;
  }
}
