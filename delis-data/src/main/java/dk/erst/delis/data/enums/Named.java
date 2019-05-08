package dk.erst.delis.data.enums;


/**
 * Marker interface for enums, which implement getName, and should be replaced on the fly with their name in JSON responses.
 */
public interface Named {

    String getName();

}
