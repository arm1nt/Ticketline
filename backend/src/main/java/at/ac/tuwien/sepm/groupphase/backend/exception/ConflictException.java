package at.ac.tuwien.sepm.groupphase.backend.exception;

public class ConflictException extends RuntimeException {

    public ConflictException() {
    }

    public ConflictException(String msg) {
        super(msg);
    }

    public ConflictException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ConflictException(Exception e) {
        super(e);
    }
}
