package dk.erst.delis.task.document.parse;

import dk.erst.delis.data.DocumentFormat;
import dk.erst.delis.data.DocumentFormatConst;
import dk.erst.delis.task.document.parse.data.DocumentInfo;

public class DocumentFormatDetectService {

	public DocumentFormat defineDocumentFormat(DocumentInfo info) {
		if (info == null || info.getRoot() == null) {
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
						// If it is not OIOUBL - we consider document as BIS3
						if (!DocumentFormatConst.CUSTOMIZATION_OIOUBL.equals(info.getCustomizationID())) {
							return f;
						}
					}
				}
			}
		}
		
		return null;
	}

	
}
