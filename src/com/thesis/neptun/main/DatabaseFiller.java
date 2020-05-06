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

public class DatabaseFiller {

  public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
    MainWindow.entityManager.getTransaction().begin();
    AuthManager auth = new AuthManager();

    List<Teacher> teachers =
        Arrays.asList(
            auth.registerTeacher(
                "ROBA01",
                "robertaaron@1A".toCharArray(),
                "Robert Aaron",
                "robert@elte.hu",
                "Senior Lecturer"),
            auth.registerTeacher(
                "BOBT01",
                "bobthomas@1A".toCharArray(),
                "Bob Thomas",
                "bob@elte.hu",
                "Practice teacher"),
            auth.registerTeacher(
                "PHIL01",
                "philleotardo@1A".toCharArray(),
                "Phil Leotardo",
                "phili@inf.elte.hu",
                "Senior Practice teacher"));
    List<Student> students =
        Arrays.asList(
            auth.registerStudent(
                "CRIS01", "crismoltesanti@1A".toCharArray(), "Cristopher Moltesanti", "crissy@elte.hu"),
            auth.registerStudent(
                "JACKY01",
                "jackyjunior@1A".toCharArray(),
                "Jacky Junior",
                "jacky@elte.hu"));
    List<Course> courses =
        Arrays.asList(
            new Course("Analysis 1", "IP-ANAL1", teachers.get(0), 5, "12:00", 120),
            new Course("Discrete Math 1", "IP-DISCR1", teachers.get(1), 7, "15:00", 120),
            new Course(
                "Algorithms and Data Structures II", "IP-ALGO2", teachers.get(2), 5, "18:00", 120),
            new Course("C++", "IP-CPPL2", teachers.get(1), 7, "13:00", 120),
            new Course("Numerical Methods 1", "IP-NUMETH45", teachers.get(0), 5, "09:00", 120),
            new Course("Linear Algebra", "IP-LINALG01", teachers.get(0), 3, "08:30", 120),
            new Course("Distributed Systems", "IP-DISTSYS", teachers.get(0), 8, "15:17", 120));
    List<Result> results =
        Arrays.asList(
            new Result("CRIS01", "IP-ANAL1", 2),
            new Result("CRIS01", "IP-CPPL2", 5),
            new Result("CRIS01", "IP-NUMETH45", 3),
            new Result("CRIS01", "IP-DISTSYS", 5),
            new Result("JACKY01", "IP-DISCR1", 5),
            new Result("JACKY01", "IP-ALGO2", 4),
            new Result("JACKY01", "IP-NUMETH45", 3),
            new Result("JACKY01", "IP-DISTSYS", 2));

    teachers
        .get(0)
        .getCourses()
        .addAll(Arrays.asList(courses.get(0), courses.get(4), courses.get(6)));
    teachers.get(1).getCourses().addAll(Arrays.asList(courses.get(1), courses.get(5)));
    teachers.get(2).getCourses().addAll(Arrays.asList(courses.get(2), courses.get(3)));
    students
        .get(0)
        .addCourses(courses.get(0), courses.get(3), courses.get(4), courses.get(5), courses.get(6));
    students
        .get(1)
        .addCourses(courses.get(1), courses.get(2), courses.get(4), courses.get(5), courses.get(6));

    persist(Teacher.class, teachers);
    persist(Student.class, students);
    persist(Course.class, courses);
    persist(Result.class, results);
    MainWindow.entityManager.getTransaction().commit();
  }

  private static <T> void persist(Class<T> type, List<T> objects) {
    for (Object object : objects) {
      MainWindow.entityManager.persist(type.cast(object));
    }
  }
}
