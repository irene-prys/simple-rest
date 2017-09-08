package demo.simplerest;

public class InvalidDataException extends Exception {
    public InvalidDataException() {
        super();
    }

    public InvalidDataException(String msg) {
        super(msg);
    }
}
