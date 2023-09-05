package at.ac.tuwien.sepm.groupphase.backend.exception;

public class SendingMailFailedException extends RuntimeException {

    public SendingMailFailedException() {
    }

    public SendingMailFailedException(String message) {
        super(message);
    }

    public SendingMailFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendingMailFailedException(Exception e) {
        super(e);
    }

}
