package com.thesis.classmgmtsystem.controller.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thesis.classmgmtsystem.main.DatabaseFiller;
import com.thesis.classmgmtsystem.main.TestMain;
import com.thesis.classmgmtsystem.model.Message;
import com.thesis.classmgmtsystem.model.User;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;

public class TestComposeMessageWindowController {

  private static DatabaseFiller db = TestMain.TEST_DATABASE_REF;

  @Test
  void sendMessage()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    ComposeMessageWindowController cmc = new ComposeMessageWindowController();
    Method sendMessage = ComposeMessageWindowController.class.getDeclaredMethod("sendMessage",
        EntityManager.class, User.class, User.class, String.class, String.class);
    sendMessage.setAccessible(true);
    User sender = db.T_ROBERT_AARON;
    User receiver = db.S_JACKY_JUNIOR;
    assertEquals(0, sender.getOutbound().size());
    assertEquals(0, receiver.getInbound().size());
    sendMessage.invoke(cmc, TestMain.entityManager, sender, receiver, "subject", "content");
    Message message = TestMain.entityManager
        .find(Message.class, receiver.getInbound().get(0).getId());
    assertEquals("subject", message.getSubject());
  }
}
