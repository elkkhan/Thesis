package com.thesis.neptun.main;

import com.thesis.neptun.model.ClassLog;
import com.thesis.neptun.model.Course;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Test {
  public static EntityManager entityManager = MainWindow.entityManager;

  public static void main(String[] args) {
    Course course = new Course("Test", "TEST-12", null, 5, "12:00", 5);
    entityManager.getTransaction().begin();
    entityManager.persist(course);
    ClassLog cl = new ClassLog(course,null,true);
    entityManager.persist(cl);
    course.getClassLogs().add(cl);
    entityManager.remove(cl);
    entityManager.getTransaction().commit();
    System.out.println(course.getClassLogs().get(0));
  }

}
