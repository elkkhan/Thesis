package com.thesis.classmgmtsystem.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thesis.classmgmtsystem.main.DatabaseFiller;
import com.thesis.classmgmtsystem.main.TestMain;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.junit.jupiter.api.Test;

public class TestAuthManager {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;
  private static AuthManager am = new AuthManager();

  @Test
  void authenticateTeacher() throws InvalidKeySpecException, NoSuchAlgorithmException {
    assertTrue(am.authenticateTeacher(db.T_BOB_TOMAS.getCode(), "bobthomas@1A".toCharArray()));
    assertFalse(am.authenticateTeacher(db.T_PHIL_LEOTARDO.getCode(), "wronpassword".toCharArray()));
  }

  @Test
  void authenticateStudent() throws InvalidKeySpecException, NoSuchAlgorithmException {
    assertTrue(
        am.authenticateStudent(db.S_CRIS_MOLTESANTI.getCode(), "crismoltesanti@1A".toCharArray()));
    assertFalse(am.authenticateStudent(db.S_JACKY_JUNIOR.getCode(), "wronpassword".toCharArray()));
  }
}
