package dk.erst.delis.task.document.parse;

import dk.erst.delis.data.constants.DocumentFormatConst;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.task.document.parse.data.DocumentInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentFormatDetectService {

	public DocumentFormat defineDocumentFormat(DocumentInfo info) {
		if (info == null || info.getRoot() == null) {
			log.warn("defineDocumentFormat: null info or null root passed, return UNSUPPORTED format: "+info);
			return DocumentFormat.UNSUPPORTED;
		}

		String rootTag = info.getRoot().getRootTag();
		String namespace = info.getRoot().getNamespace();

		DocumentFormat[] values = DocumentFormat.values();
		for (DocumentFormat f : values) {
			if (!f.isUnsupported()) {
				if (f.getRootTag().equals(rootTag) && f.getNamespace().equals(namespace)) {
					if (f.isOIOUBL()) {
						if (info.getCustomizationID() != null && info.getCustomizationID().startsWith(DocumentFormatConst.CUSTOMIZATION_OIOUBL_PREFIX)) {
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
					} else if (f.isBIS3IR()) {
						if (info.getCustomizationID() != null && info.getCustomizationID().startsWith(DocumentFormatConst.CUSTOMIZATION_BIS3_IR_STARTS_WITH)) {
							return f;
						}
						if (info.getCustomizationID() != null && info.getCustomizationID().startsWith(DocumentFormatConst.CUSTOMIZATION_BIS3_MLR_STARTS_WITH)) {
							return DocumentFormat.BIS3_MESSAGE_LEVEL_RESPONSE;
						}
					} else if (f.isBIS3Order()) {
						if (info.getCustomizationID() != null && info.getCustomizationID().startsWith(DocumentFormatConst.CUSTOMIZATION_BIS3_ORDER_STARTS_WITH)) {
							if (info.getProfile() != null && info.getProfile().getId() != null) {
								if (info.getProfile().getId().startsWith(DocumentFormatConst.PROFILE_BIS3_ORDER_ONLY)) {
									return DocumentFormat.BIS3_ORDER_ONLY;
								}
								if (info.getProfile().getId().startsWith(DocumentFormatConst.PROFILE_BIS3_ORDERING)) {
									return DocumentFormat.BIS3_ORDER;
								}
							}
						}
					} else if (f.isBIS3Catalogue()) {
						if (info.getProfile() != null && info.getProfile().getId() != null) {
							if (info.getProfile().getId().equals(DocumentFormatConst.PROFILE_BIS3_CATALOGUE_ONLY)) {
								return DocumentFormat.BIS3_CATALOGUE_ONLY;
							}
							if (info.getProfile().getId().equals(DocumentFormatConst.PROFILE_BIS3_CATALOGUE_WITHOUT_RESPONSE)) {
								return DocumentFormat.BIS3_CATALOGUE_WITHOUT_RESPONSE;
							}
						}
					} else if (f.isBIS3OR() || f.isBIS3CatalogueResponse()) {
						return f;
					}
				}
			}
		}
		
		return DocumentFormat.UNSUPPORTED;
	}

	
}
