package dk.erst.delis.persistence.journal.identifier;

import dk.erst.delis.persistence.AbstractFilterModel;

import lombok.Getter;
import lombok.Setter;

/**
 * @author funtusthan, created by 14.01.19
 */

@Getter
@Setter
public class JournalIdentifierFilterModel extends AbstractFilterModel {

    private String organisation;
    private String identifier;
    private String message;
    private Long durationMs;
}
