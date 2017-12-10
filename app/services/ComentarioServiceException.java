package services;

public class ComentarioServiceException extends RuntimeException{

  public ComentarioServiceException() {}

  public ComentarioServiceException(String message){
    super(message);
  }
}
