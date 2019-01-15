package dk.erst.delis.persistence.journal.document;

import dk.erst.delis.data.enums.document.*;
import dk.erst.delis.persistence.AbstractFilterModel;

import lombok.Getter;
import lombok.Setter;

/**
 * @author funtusthan, created by 13.01.19
 */

@Getter
@Setter
public class JournalDocumentFilterModel extends AbstractFilterModel {

    private String organisation;
    private String document;
    private DocumentProcessStepType type;
    private String success;
    private String message;
    private Long durationMs;
}
