package dk.erst.delis.task.codelist;

import dk.erst.delis.config.codelist.DocumentTypeIdentifier;
import dk.erst.delis.config.codelist.ParticipantIdentifierScheme;
import dk.erst.delis.config.codelist.ProcessScheme;
import dk.erst.delis.config.codelist.TransportProfile;

public enum CodeList {

	PEPPOL_PARTICIPANT_IDENTIFIER_SCHEME(ParticipantIdentifierScheme.class, "openpeppol/PEPPOL Code Lists - Participant identifier schemes v4 Removed Deprecated.csv"),

	PEPPOL_DOCUMENT_TYPE_IDENTIFIER(DocumentTypeIdentifier.class, "openpeppol/PEPPOL Code Lists - Document types v3.csv"),

	PEPPOL_PROCESS_SCHEME(ProcessScheme.class, "openpeppol/PEPPOL Code Lists - Processes v3.csv"),

	PEPPOL_TRANSPORT_PROFILE(TransportProfile.class, "openpeppol/PEPPOL Code Lists - Transport profiles v3.csv"),

	;

	private final Class<?> dataClass;

	private final String path;

	private CodeList(Class<?> cls, String path) {
		this.dataClass = cls;
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public Class<?> getDataClass() {
		return dataClass;
	}

}
