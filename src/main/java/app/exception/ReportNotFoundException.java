package app.exception;

public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException() {
    }

    public ReportNotFoundException(String message) {
        super(message);
    }
}
