package at.ac.tuwien.sepm.groupphase.backend.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String msg) {
        super(msg);
    }
}
