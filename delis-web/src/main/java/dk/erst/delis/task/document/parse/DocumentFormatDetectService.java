package dk.erst.delis.task.document.parse;

import dk.erst.delis.data.DocumentFormat;
import dk.erst.delis.data.DocumentFormatConst;
import dk.erst.delis.task.document.parse.data.DocumentInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentFormatDetectService {

	public DocumentFormat defineDocumentFormat(DocumentInfo info) {
		if (info == null || info.getRoot() == null) {
			log.warn("defineDocumentFormat: null info or null root passed, return UNSUPPORTED format: "+info);
			return DocumentFormat.UNSUPPORTED;
		}
		
		DocumentFormat[] values = DocumentFormat.values();
		for (DocumentFormat f : values) {
			if (!f.isUnsupported()) {
				String rootTag = info.getRoot().getRootTag();
				String namespace = info.getRoot().getNamespace();
				
				if (f.getRootTag().equals(rootTag) && f.getNamespace().equals(namespace)) {
					if (f.isOIOUBL()) {
						if (DocumentFormatConst.CUSTOMIZATION_OIOUBL.equals(info.getCustomizationID())) {
							return f;
						}
					} else if (f.isBIS3()) {
						// Consider document as BIS3 if its customizationID starts with configured (agreed on a meeting with Ole 2018.12.12
						// Reason - BIS3 customizationID will get subversion later
						if (info.getCustomizationID() != null && info.getCustomizationID().startsWith(DocumentFormatConst.CUSTOMIZATION_BIS3_STARTS_WITH)) {
							return f;
						}
					} else if (f.isCII()) {
						return f;
					}
				}
			}
		}
		
		return DocumentFormat.UNSUPPORTED;
	}

	
}
