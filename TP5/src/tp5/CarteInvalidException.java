package tp5;

public class CarteInvalidException extends Exception {

    /**
     * Creates a new instance of CarteInvalidException without
     * detail message.
     */
    public CarteInvalidException() {
    }

    /**
     * Constructs an instance of CarteInvalidException with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CarteInvalidException(String msg) {
        super(msg);
    }
}
