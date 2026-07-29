package application.tracker.service.exceptions;

public class ApplicationNotFoundException extends RuntimeException {
     public ApplicationNotFoundException(String message){
         super(message);
     }
}
