package com.thesis.classmgmtsystem.auth;

import com.thesis.classmgmtsystem.main.MainWindow;
import com.thesis.classmgmtsystem.model.PasswordPair;
import com.thesis.classmgmtsystem.model.Student;
import com.thesis.classmgmtsystem.model.Student_;
import com.thesis.classmgmtsystem.model.Teacher;
import com.thesis.classmgmtsystem.model.Teacher_;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class AuthManager extends MainWindow {

  private static final SecureRandom RAND = new SecureRandom();
  private EntityManager em;

  public AuthManager(EntityManager em) {
    this.em = em;
  }

  public boolean authenticateTeacher(String code, char[] password)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Teacher> teacherQuery = cb.createQuery(Teacher.class);
    Root<Teacher> teacher = teacherQuery.from(Teacher.class);
    teacherQuery.where(cb.equal(teacher.get(Teacher_.code), code));
    teacherQuery.select(teacher);
    Teacher t = em.createQuery(teacherQuery).getSingleResult();
    byte[] hash = hashPassword(password, t.getSalt());
    return constantTimeEquals(hash, t.getPassword());
  }

  public boolean authenticateStudent(String code, char[] password)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Student> studentQuery = cb.createQuery(Student.class);
    Root<Student> student = studentQuery.from(Student.class);
    studentQuery.where(cb.equal(student.get(Student_.code), code));
    studentQuery.select(student);
    Student st = em.createQuery(studentQuery).getSingleResult();
    byte[] hash = hashPassword(password, st.getSalt());
    return constantTimeEquals(hash, st.getPassword());
  }

  public Teacher registerTeacher(
      String code, char[] password, String name, String email, String role)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    PasswordPair p = hashPassword(password);
    return new Teacher(code, p.getPassword(), p.getSalt(), name, email, role);
  }

  public Student registerStudent(String code, char[] password, String name, String email)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    PasswordPair p = hashPassword(password);
    return new Student(code, p.getPassword(), p.getSalt(), name, email);
  }

  private byte[] hashPassword(char[] password, byte[] salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    PBEKeySpec spec = new PBEKeySpec(password, salt, 2000, 256);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    return skf.generateSecret(spec).getEncoded();
  }

  private boolean constantTimeEquals(byte[] a, byte[] b) {
    if (a.length != b.length) {
      return false;
    }
    int result = 0;
    for (int i = 0; i < a.length; i++) {
      result |= a[i] ^ b[i];
    }
    return result == 0;
  }

  private PasswordPair hashPassword(char[] password)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] salt = new byte[256];
    RAND.nextBytes(salt);
    return new PasswordPair(hashPassword(password, salt), salt);
  }
}
