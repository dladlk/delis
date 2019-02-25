package dk.erst.delis.task.codelist;

import java.nio.file.Path;
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

	public List<ParticipantIdentifierScheme> readParticipantIdentifierSchemeList() {
		CodeList codeList = CodeList.PEPPOL_PARTICIPANT_IDENTIFIER_SCHEME;
		CodeListCsvReader<ParticipantIdentifierScheme> reader = new CodeListCsvReader<ParticipantIdentifierScheme>();
		return reader.readCodeList(codeList, resourcePath(codeList), ParticipantIdentifierScheme.class);
	}

	public List<DocumentTypeIdentifier> readDocumentTypeIdentifierList() {
		CodeList codeList = CodeList.PEPPOL_DOCUMENT_TYPE_IDENTIFIER;
		CodeListCsvReader<DocumentTypeIdentifier> reader = new CodeListCsvReader<DocumentTypeIdentifier>();
		return reader.readCodeList(codeList, resourcePath(codeList), DocumentTypeIdentifier.class);
	}
	
	public List<ProcessScheme> readProcessSchemeList() {
		CodeList codeList = CodeList.PEPPOL_PROCESS_SCHEME;
		CodeListCsvReader<ProcessScheme> reader = new CodeListCsvReader<ProcessScheme>();
		return reader.readCodeList(codeList, resourcePath(codeList), ProcessScheme.class);
	}

	public List<TransportProfile> readTransportProfileList() {
		CodeList codeList = CodeList.PEPPOL_TRANSPORT_PROFILE;
		CodeListCsvReader<TransportProfile> reader = new CodeListCsvReader<TransportProfile>();
		return reader.readCodeList(codeList, resourcePath(codeList), TransportProfile.class);
	}
	
	private Path resourcePath(CodeList codeList) {
		Path storageCodeListPath = configBean.getStorageCodeListPath();
		String codeListPath = codeList.getPath();
		return storageCodeListPath.resolve(codeListPath);
	}
}
