package dk.erst.delis.task.document.parse;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import dk.erst.delis.task.document.parse.XmlRootTagUtil.XmlElementData;
import lombok.extern.slf4j.Slf4j;
import no.difi.oxalis.api.lang.OxalisContentException;
import no.difi.oxalis.sniffer.document.NoSbdhParser;
import no.difi.vefa.peppol.common.model.Header;

@Slf4j
public class DocumentParseService {

	public Header parsePeppolHeader(InputStream is) throws Exception {
		XmlElementData d = new XmlElementData();
		is = XmlRootTagUtil.fillRootTagData(is, d);
		
		String namespace = d.getNamespaceUri();
		String localname = d.getLocalName();

		log.info("Resolved root tag in namespace " + namespace + ", local name " + localname);

		
		Header header = parseByOxalisSniffer(is);

		StringBuilder sb = new StringBuilder();

		sb.append("\t Sender: \t" + header.getSender() + "\n");
		sb.append("\t Receiver: \t" + header.getReceiver() + "\n");
		sb.append("\t Process: \t" + header.getProcess() + "\n");
		sb.append("\t DocumentType: \t" + header.getDocumentType() + "\n");
		sb.append("\t InstanceType: \t" + header.getInstanceType() + "\n");
		sb.append("\t Identifier: \t" + header.getIdentifier() + "\n");
		String created = "";
		if (header.getCreationTimestamp() != null) {
			created = new SimpleDateFormat("yyyy-MM-dd").format(header.getCreationTimestamp());
		}
		sb.append("\t Creation: \t" + created + "\n");

		log.info("Resolved values: \n" + sb.toString());

		return header;
	}

	private Header parseByOxalisSniffer(InputStream is) throws OxalisContentException {
		NoSbdhParser oxalisSniffer = new NoSbdhParser();
		Header header = oxalisSniffer.parse(is);
		return header;
	}

}
