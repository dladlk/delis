package dk.erst.delis.task.document.parse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import dk.erst.delis.data.enums.document.DocumentFormat;
import org.springframework.stereotype.Service;

import dk.erst.delis.task.document.parse.data.DocumentInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentParseService {
	
	private static final String DOCUMENT_INFO_XSLT_PATH = "parse-document-info.xslt";
	
	private DocumentFormatDetectService documentFormatDetectService = new DocumentFormatDetectService();

	public DocumentInfo parseDocumentInfo(InputStream is) throws Exception {
		DocumentInfo documentHeader = parseByXSLT(is);
		return documentHeader;
	}

	private DocumentInfo parseByXSLT(InputStream is) {
		long start = System.currentTimeMillis();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			XSLTUtil.apply(this.getClass().getResourceAsStream(DOCUMENT_INFO_XSLT_PATH), null, is, baos);
		} catch (Exception e) {
			log.error("Failed to extract document header", e);
			return null;
		}
		byte[] headerBytes = baos.toByteArray();
		log.info("Parsed header in " + (System.currentTimeMillis() - start) + "ms");

		if (log.isDebugEnabled()) {
			log.debug("Result of parsing: \n" + new String(headerBytes, StandardCharsets.UTF_8));
		}

		try {
			start = System.currentTimeMillis();
			DocumentInfo documentHeader = parseDocumentInfoBytes(headerBytes);
			if (log.isDebugEnabled()) {
				log.debug("Result of reading document header: " + documentHeader);
			}
			log.info("Read header to java in " + (System.currentTimeMillis() - start) + " ms");
			return documentHeader;
		} catch (JAXBException e) {
			log.error("Failed to read parsed document header: \n" + new String(headerBytes, StandardCharsets.UTF_8), e);
			return null;
		}
	}

	private DocumentInfo parseDocumentInfoBytes(byte[] headerBytes) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(DocumentInfo.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		DocumentInfo docHeader = (DocumentInfo) unmarshaller.unmarshal(new ByteArrayInputStream(headerBytes));
		return docHeader;
	}

	public DocumentFormat defineDocumentFormat(InputStream inputStream) throws Exception {
		DocumentInfo documentInfo = this.parseDocumentInfo(inputStream);
		return defineDocumentFormat(documentInfo);
	}
	
	public DocumentFormat defineDocumentFormat(DocumentInfo header) {
		return this.documentFormatDetectService.defineDocumentFormat(header);
	}

}
