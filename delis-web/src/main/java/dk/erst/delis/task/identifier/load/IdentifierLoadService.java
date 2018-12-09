package dk.erst.delis.task.identifier.load;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;

import dk.erst.delis.dao.IdentifierRepository;
import dk.erst.delis.dao.OrganisationRepository;
import dk.erst.delis.data.Organisation;
import dk.erst.delis.task.identifier.load.csv.CSVIdentifierStreamReader;

public class IdentifierLoadService {

	@Autowired
	private IdentifierRepository identifierRepository;
	
	@Autowired
	private OrganisationRepository organisationRepository;
	
	
	public void loadCSV(Organisation organisation, InputStream inputStream) {
		CSVIdentifierStreamReader r = new CSVIdentifierStreamReader(inputStream, StandardCharsets.ISO_8859_1, ';');
		r.start();
	}
	
}
