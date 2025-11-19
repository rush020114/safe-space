package com.safespace.content_filter_backend.infra.exception;

public class ContentFilterException extends RuntimeException{
  public ContentFilterException(String message, Throwable cause){
    super(message, cause);
  }
}
