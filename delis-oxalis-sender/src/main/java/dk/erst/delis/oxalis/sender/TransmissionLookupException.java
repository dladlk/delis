package dk.erst.delis.oxalis.sender;

public class TransmissionLookupException extends Exception {

	private static final long serialVersionUID = -6354985967617959627L;

	public TransmissionLookupException(String message) {
        super(message);
    }

    public TransmissionLookupException(String message, Throwable cause) {
        super(message, cause);
    }
}
