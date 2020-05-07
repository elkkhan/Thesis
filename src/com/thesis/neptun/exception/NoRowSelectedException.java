package com.thesis.neptun.exception;

public class NoRowSelectedException extends Exception {

  public NoRowSelectedException(String errorMessage) {
    super(errorMessage);
  }
}
