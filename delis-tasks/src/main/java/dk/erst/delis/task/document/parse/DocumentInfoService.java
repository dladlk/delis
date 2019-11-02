package dk.erst.delis.task.document.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.task.document.parse.data.DocumentInfo;
import dk.erst.delis.task.document.parse.data.DocumentSBDHeader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.peppol.sbdh.SbdReader;
import no.difi.vefa.peppol.sbdh.SbdReader.Type;
import no.difi.vefa.peppol.sbdh.util.XMLStreamUtils;

@Service
@Slf4j
public class DocumentInfoService {

	private DocumentParseService documentParseService;

	@Autowired
	public DocumentInfoService(DocumentParseService documentParseService) {
		this.documentParseService = documentParseService;
	}

	@Getter
	@Setter
	public static class DocumentInfoData {
		File file;
		File fileSbd;
		DocumentInfo documentInfo;
	}

	public DocumentInfoData documentInfoData(Path xmlFilePath, File file) {
		DocumentInfoData pdid = new DocumentInfoData();

		DocumentInfo info;
		info = parseDocumentInfo(file);

		File fileSbd = null;
		if (info != null && "StandardBusinessDocument".equals(info.getRoot().getRootTag())) {
			log.info("Root tag is SBD: " + info.getRoot());
			try (SbdReader sbdReader = SbdReader.newInstance(new FileInputStream(file))) {
				Type type = sbdReader.getType();
				log.info("SbdReader defined type as " + type);

				DocumentSBDHeader documentSBDHeader = new DocumentSBDHeader(sbdReader.getHeader());

				fileSbd = file;
				file = Paths.get(xmlFilePath.toString() + ".load_sbdh_payload").toFile();

				log.info("Save payload to " + file);
				try (FileOutputStream fos = new FileOutputStream(file)) {
					switch (type) {
					case XML:
						XMLStreamUtils.copy(sbdReader.xmlReader(), fos);
						break;
					case BINARY:
						StreamUtils.copy(sbdReader.binaryReader(), fos);
						break;
					case TEXT:
						StreamUtils.copy(sbdReader.textReader(), fos);
						break;
					}
				}
				info = parseDocumentInfo(file);
				info.setSbdh(documentSBDHeader);
			} catch (Exception e) {
				log.error("Failed to read SBDH and extract payload from " + file, e);
			}
		}
		pdid.setDocumentInfo(info);
		pdid.setFile(file);
		pdid.setFileSbd(fileSbd);

		return pdid;
	}

	private DocumentInfo parseDocumentInfo(File file) {
		InputStream is = null;
		DocumentInfo header = null;
		try {
			is = new FileInputStream(file);
			header = documentParseService.parseDocumentInfo(is);
			log.info("Parsed " + header);
		} catch (Exception e) {
			log.error("Failed to parse document info on file " + file, e);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}
		return header;
	}

	public DocumentFormat defineDocumentFormat(DocumentInfo info) {
		return this.documentParseService.defineDocumentFormat(info);
	}
}
