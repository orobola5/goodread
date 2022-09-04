package africa.semicolon.goodreads.exceptions;
public class GoodReadException extends Exception {
    private int statusCode;
    public GoodReadException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
