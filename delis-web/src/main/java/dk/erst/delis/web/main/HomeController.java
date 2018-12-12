package dk.erst.delis.web.main;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.erst.delis.dao.DocumentRepository;
import dk.erst.delis.dao.IdentifierRepository;
import dk.erst.delis.dao.OrganisationRepository;
import dk.erst.delis.data.DocumentStatus;
import dk.erst.delis.data.IdentifierPublishingStatus;
import lombok.Data;

@Controller
public class HomeController {

	@Autowired
	private OrganisationRepository organisationRepository;
	@Autowired
	private IdentifierRepository identifierRepository;
	@Autowired
	private DocumentRepository documentRepository;

	@RequestMapping("/home")
	public String index(Model model, Authentication authentication) {
		
		model.addAttribute("organisationCount", organisationRepository.count());
		
		model.addAttribute("identifierFailedCount", identifierRepository.countByPublishingStatus(IdentifierPublishingStatus.FAILED));
		model.addAttribute("identifierPendingCount", identifierRepository.countByPublishingStatus(IdentifierPublishingStatus.PENDING));

		DocumentStat documentStat = new DocumentStat();
		List<Map<String,Object>> statusStat = documentRepository.loadDocumentStatusStat();
		for (Map<String, Object> map : statusStat) {
			DocumentStatus st = (DocumentStatus) map.get("documentStatus");
			Long count = (Long) map.get("documentCount");
			
			if (st == DocumentStatus.DELIVER_OK) {
				documentStat.setDocumentDeliveredCount(count);
			} else if (st == DocumentStatus.VALIDATE_OK) {
				documentStat.setDocumentValidatedCount(count);
			} else if (st == DocumentStatus.LOAD_OK) {
				documentStat.setDocumentLoadedCount(count);
			} else if (st == DocumentStatus.VALIDATE_ERROR || st == DocumentStatus.UNKNOWN_RECEIVER || st ==DocumentStatus.LOAD_ERROR) {
				documentStat.setDocumentDeliveredCount(documentStat.getDocumentDeliveredCount() + count);
			}
			
		}
		model.addAttribute("documentStat", documentStat);
		
		return "home";
	}
	
	@Data
	private static class DocumentStat {
		long documentDeliveredCount;
		long documentValidatedCount;
		long documentLoadedCount;
		long documentFailedCount;

	}

}
