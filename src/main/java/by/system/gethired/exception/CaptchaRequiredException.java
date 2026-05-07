package by.system.gethired.exception;

public class CaptchaRequiredException extends RuntimeException {
    public CaptchaRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
