package dk.erst.delis.persistence.journal.organisation;

import dk.erst.delis.persistence.AbstractFilterModel;

import lombok.Getter;
import lombok.Setter;

/**
 * @author funtusthan, created by 14.01.19
 */

@Getter
@Setter
public class JournalOrganisationFilterModel extends AbstractFilterModel {

    private String organisation;
    private String message;
    private Long durationMs;
}
