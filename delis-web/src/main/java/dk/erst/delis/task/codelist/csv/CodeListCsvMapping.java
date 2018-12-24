package dk.erst.delis.task.codelist.csv;

import dk.erst.delis.task.codelist.CodeList;

public class CodeListCsvMapping {

	private static String[][] COLUMN_MAP_PEPPOL_PARTICIPANT_IDENTIFIER_SCHEME = new String[][] {

		new String[] { "schemeID", "Scheme ID" },

		new String[] { "icdValue", "ICD value" },

		new String[] { "issuingOrganization", "Issuing Organization" },
	
	};
	
	private static String[][] COLUMN_MAP_PEPPOL_DOCUMENT_TYPE_IDENTIFIER = new String[][] {
	
		new String[] { "profileCode", "Profile code" },
	
		new String[] { "documentTypeIdentifierScheme", "PEPPOL Identifier Scheme" },
	
		new String[] { "documentTypeIdentifier", "PEPPOL Document Type Identifier" },
	
	};
	
	private static String[][] COLUMN_MAP_PEPPOL_PROCESS_SCHEME = new String[][] {
		
		new String[] { "profileCode", "Profile code" },
	
		new String[] { "processScheme", "Process Scheme" },
	
		new String[] { "profileID", "Profile ID" },
	
	};
	
	private static String[][] COLUMN_MAP_PEPPOL_TRANSPORT_PROFILE = new String[][] {
		
		new String[] { "protocolName", "Protocol name" },
	
		new String[] { "profileID", "Profile ID" },
	
	};
	
	
	public static String[][] getColumnMapping(CodeList codeList) {
		switch (codeList) {
		case PEPPOL_PARTICIPANT_IDENTIFIER_SCHEME:
			return COLUMN_MAP_PEPPOL_PARTICIPANT_IDENTIFIER_SCHEME;
		case PEPPOL_DOCUMENT_TYPE_IDENTIFIER:
			return COLUMN_MAP_PEPPOL_DOCUMENT_TYPE_IDENTIFIER;
		case PEPPOL_PROCESS_SCHEME:
			return COLUMN_MAP_PEPPOL_PROCESS_SCHEME;
		case PEPPOL_TRANSPORT_PROFILE:
			return COLUMN_MAP_PEPPOL_TRANSPORT_PROFILE;
		}
		return null;
	}
}
