package dk.erst.delis.task.codelist;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.codelist.DocumentTypeIdentifier;
import dk.erst.delis.config.codelist.ParticipantIdentifierScheme;
import dk.erst.delis.config.codelist.ProcessScheme;
import dk.erst.delis.config.codelist.TransportProfile;
import dk.erst.delis.task.codelist.csv.CodeListCsvReader;

@Service
public class CodeListReaderService {

	private ConfigBean configBean;

	@Autowired
	public CodeListReaderService(ConfigBean configBean) {
		this.configBean = configBean;
	}

	public List<ParticipantIdentifierScheme> readParticipantIdentifierSchemeList() throws Exception {
		CodeList codeList = CodeList.PEPPOL_PARTICIPANT_IDENTIFIER_SCHEME;
		CodeListCsvReader<ParticipantIdentifierScheme> reader = new CodeListCsvReader<ParticipantIdentifierScheme>();
		return reader.readCodeList(codeList, configBean.getStorageCodeListPath().resolve(codeList.getPath()), ParticipantIdentifierScheme.class);
	}
	
	public List<DocumentTypeIdentifier> readDocumentTypeIdentifierList() throws Exception {
		CodeList codeList = CodeList.PEPPOL_DOCUMENT_TYPE_IDENTIFIER;
		CodeListCsvReader<DocumentTypeIdentifier> reader = new CodeListCsvReader<DocumentTypeIdentifier>();
		return reader.readCodeList(codeList, configBean.getStorageCodeListPath().resolve(codeList.getPath()), DocumentTypeIdentifier.class);
	}
	
	public List<ProcessScheme> readProcessSchemeList() throws Exception {
		CodeList codeList = CodeList.PEPPOL_PROCESS_SCHEME;
		CodeListCsvReader<ProcessScheme> reader = new CodeListCsvReader<ProcessScheme>();
		return reader.readCodeList(codeList, configBean.getStorageCodeListPath().resolve(codeList.getPath()), ProcessScheme.class);
	}

	public List<TransportProfile> readTransportProfileList() throws Exception {
		CodeList codeList = CodeList.PEPPOL_TRANSPORT_PROFILE;
		CodeListCsvReader<TransportProfile> reader = new CodeListCsvReader<TransportProfile>();
		return reader.readCodeList(codeList, configBean.getStorageCodeListPath().resolve(codeList.getPath()), TransportProfile.class);
	}
}
