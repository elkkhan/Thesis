package com.thesis.classmgmtsystem.model;

public class PasswordPair {

  private final byte[] password;
  private final byte[] salt;

  public PasswordPair(byte[] password, byte[] salt) {
    this.password = password;
    this.salt = salt;
  }

  public byte[] getPassword() {
    return password;
  }

  public byte[] getSalt() {
    return salt;
  }
}
