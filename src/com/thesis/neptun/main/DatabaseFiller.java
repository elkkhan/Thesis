package com.thesis.neptun.main;

import com.thesis.neptun.auth.AuthManager;
import com.thesis.neptun.model.Course;
import com.thesis.neptun.model.Result;
import com.thesis.neptun.model.Student;
import com.thesis.neptun.model.Teacher;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;

public class DatabaseFiller {

  public Student S_JADON_SANCHO;
  public Student S_JACKY_JUNIOR;
  public Student S_CRIS_MOLTESANTI;

  public Teacher T_PHIL_LEOTARDO;
  public Teacher T_BOB_TOMAS;
  public Teacher T_ROBERT_AARON;

  public Course C_ANALYSIS;
  public Course C_DISCRETE;
  public Course C_ALGANDDS;
  public Course C_CPP;
  public Course C_NUMERICAL;
  public Course C_LINALG;
  public Course C_DISTRIB;

  public List<Student> students;
  public List<Teacher> teachers;
  public List<Course> courses;
  List<Result> results;

  public DatabaseFiller() throws InvalidKeySpecException, NoSuchAlgorithmException {
    AuthManager auth = new AuthManager();
    T_ROBERT_AARON = auth.registerTeacher(
        "ROBA01",
        "robertaaron@1A".toCharArray(),
        "Robert Aaron",
        "robert@elte.hu",
        "Senior Lecturer");
    T_BOB_TOMAS = auth.registerTeacher(
        "BOBT01",
        "bobthomas@1A".toCharArray(),
        "Bob Thomas",
        "bob@elte.hu",
        "Practice teacher");
    T_PHIL_LEOTARDO =
        auth.registerTeacher(
            "PHIL01",
            "philleotardo@1A".toCharArray(),
            "Phil Leotardo",
            "phili@inf.elte.hu",
            "Senior Practice teacher");

    S_CRIS_MOLTESANTI =
        auth.registerStudent(
            "CRIS01", "crismoltesanti@1A".toCharArray(), "Cristopher Moltesanti",
            "crissy@elte.hu");
    S_JACKY_JUNIOR =
        auth.registerStudent(
            "JACKY01",
            "jackyjunior@1A".toCharArray(),
            "Jacky Junior",
            "jacky@elte.hu");
    S_JADON_SANCHO =
        auth.registerStudent(
            "JADO01",
            "jadonsancho@1A".toCharArray(),
            "Jadon Sancho",
            "jsancho@bvb.de");

    students = Arrays.asList(S_CRIS_MOLTESANTI, S_JACKY_JUNIOR, S_JADON_SANCHO);

    teachers = Arrays.asList(T_ROBERT_AARON, T_BOB_TOMAS, T_PHIL_LEOTARDO);

    C_ANALYSIS =
        new Course("Analysis", "IP-ANAL1", T_ROBERT_AARON, 5, "12:00", 120);
    C_DISCRETE =
        new Course("Discrete Math", "IP-DISCR1", T_BOB_TOMAS, 7, "15:00", 120);
    C_ALGANDDS =
        new Course("Algorithms & Structures", "IP-ALGO2", T_PHIL_LEOTARDO, 5, "18:00", 120);
    C_CPP =
        new Course("C++", "IP-CPPL2", teachers.get(1), 7, "13:00", 120);
    C_NUMERICAL =
        new Course("Numerical Methods", "IP-NUMETH45", T_ROBERT_AARON, 5, "09:00", 120);
    C_LINALG =
        new Course("Linear Algebra", "IP-LINALG01", T_ROBERT_AARON, 3, "08:30", 120);
    C_DISTRIB =
        new Course("Distributed Systems", "IP-DISTSYS", T_ROBERT_AARON, 8, "15:17", 120);

    courses = Arrays
        .asList(C_ANALYSIS, C_DISCRETE, C_ALGANDDS, C_CPP, C_NUMERICAL, C_LINALG, C_DISTRIB);

    results =
        Arrays.asList(
            new Result(S_CRIS_MOLTESANTI, C_ANALYSIS, 2),
            new Result(S_CRIS_MOLTESANTI, C_CPP, 5),
            new Result(S_CRIS_MOLTESANTI, C_NUMERICAL, 3),
            new Result(S_CRIS_MOLTESANTI, C_DISTRIB, 5),
            new Result(S_JACKY_JUNIOR, C_DISCRETE, 5),
            new Result(S_JACKY_JUNIOR, C_LINALG, 4),
            new Result(S_JACKY_JUNIOR, C_NUMERICAL, 3),
            new Result(S_JACKY_JUNIOR, C_DISTRIB, 2));

    T_ROBERT_AARON.getCourses().addAll(Arrays.asList(C_ANALYSIS, C_NUMERICAL, C_DISTRIB));
    T_BOB_TOMAS.getCourses().addAll(Arrays.asList(C_DISCRETE, C_LINALG));
    T_PHIL_LEOTARDO.getCourses().addAll(Arrays.asList(C_ALGANDDS, C_CPP));
    S_CRIS_MOLTESANTI.addCourses(C_ANALYSIS, C_CPP, C_NUMERICAL, C_LINALG, C_DISTRIB);
    S_JACKY_JUNIOR.addCourses(C_DISCRETE, C_ALGANDDS, C_NUMERICAL, C_LINALG, C_DISTRIB);
  }


  public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException {
    DatabaseFiller databaseFiller = new DatabaseFiller();
    databaseFiller.fill(MainWindow.entityManager);
  }

  private static <T> void persist(EntityManager em, Class<T> type, List<T> objects) {
    for (Object object : objects) {
      em.persist(type.cast(object));
    }
  }

  public void fill(EntityManager em) {
    em.getTransaction().begin();
    persist(em, Teacher.class, teachers);
    persist(em, Student.class, students);
    persist(em, Course.class, courses);
    persist(em, Result.class, results);
    em.getTransaction().commit();
  }
}
