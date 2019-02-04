package dk.erst.delis.data.entities.journal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
//@Setter
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode (callSuper = false)
@Deprecated
public class JournalDocumentErrorDictionaryId implements Serializable {
    @Column (name="journalDocumentId")
    private Long journalDocumentId;

    @Column (name="errorDictionaryId")
    private Long errorDictionaryId;

    public Long getJournalDocumentId() {
        return journalDocumentId;
    }

    public void setJournalDocumentId(Long journalDocumentId) {
        this.journalDocumentId = journalDocumentId;
    }

    public Long getErrorDictionaryId() {
        return errorDictionaryId;
    }

    public void setErrorDictionaryId(Long errorDictionaryId) {
        this.errorDictionaryId = errorDictionaryId;
    }

    public JournalDocumentErrorDictionaryId(Long journalDocumentId, Long errorDictionaryId) {
        this.journalDocumentId = journalDocumentId;
        this.errorDictionaryId = errorDictionaryId;
    }

    public JournalDocumentErrorDictionaryId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JournalDocumentErrorDictionaryId that = (JournalDocumentErrorDictionaryId) o;
        return Objects.equals(journalDocumentId, that.journalDocumentId) &&
                Objects.equals(errorDictionaryId, that.errorDictionaryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(journalDocumentId, errorDictionaryId);
    }
}
