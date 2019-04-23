package dk.erst.delis.oxalis.sender;

/**
 * 
 * Thrown when there is a problem related to the actual transmission protocol.
 * 
 * Suppose new attempt to send.
 * 
 */
public class TransmissionException extends Exception {

	private static final long serialVersionUID = -6354985967617959627L;

	public TransmissionException(String message) {
        super(message);
    }

    public TransmissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
