package dk.erst.delis.task.codelist;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.erst.delis.config.codelist.ParticipantIdentifierScheme;

@Component
public class CodeListDict {

	private Map<String, ParticipantIdentifierScheme> pisBySchemeValueMap;
	private Map<String, ParticipantIdentifierScheme> pisByIcdValueMap;
	
	private static ParticipantIdentifierScheme EMPTY_PARTICIPANTIDENTIFIERSCHEME = new ParticipantIdentifierScheme();
	
	@Autowired
	public CodeListDict(CodeListReaderService codeListReaderService) {
		initMaps(codeListReaderService);
	}
	
	private void initMaps(CodeListReaderService codeListReaderService) {
		List<ParticipantIdentifierScheme> pList = codeListReaderService.readParticipantIdentifierSchemeList();
		pisBySchemeValueMap = pList.stream().collect(Collectors.toMap(t -> t.getSchemeID().toUpperCase(), Function.identity()));		
		pisByIcdValueMap = pList.stream().collect(Collectors.toMap(t -> t.getIcdValue().toUpperCase(), Function.identity()));		
	}

	private Optional<ParticipantIdentifierScheme> findParticipantIdentifierScheme(String lookupValue) {
		if (lookupValue == null) {
			return Optional.empty();
		}
		lookupValue = lookupValue.toUpperCase();
		ParticipantIdentifierScheme pis = pisByIcdValueMap.get(lookupValue);
		if (pis == null) {
			pis = pisBySchemeValueMap.get(lookupValue);
		}
		return Optional.ofNullable(pis);
	}
	
	public String getIdentifierTypeSchemeID(String lookupValue) {
		return findParticipantIdentifierScheme(lookupValue).orElse(EMPTY_PARTICIPANTIDENTIFIERSCHEME).getSchemeID();
	}
	
	public String getIdentifierTypeIcdValue(String lookupValue) {
		return findParticipantIdentifierScheme(lookupValue).orElse(EMPTY_PARTICIPANTIDENTIFIERSCHEME).getIcdValue();
	}
	
}
