package dk.erst.delis.web.main;

import java.util.List;
import java.util.Map;

import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.IdentifierDaoRepository;
import dk.erst.delis.dao.OrganisationDaoRepository;

import lombok.Data;

@Controller
public class HomeController {

	@Autowired
	private OrganisationDaoRepository organisationDaoRepository;
	@Autowired
	private IdentifierDaoRepository identifierDaoRepository;
	@Autowired
	private DocumentDaoRepository documentDaoRepository;

	@RequestMapping("/home")
	public String index(Model model, Authentication authentication) {
		
		model.addAttribute("organisationCount", organisationDaoRepository.count());
		
		model.addAttribute("identifierFailedCount", identifierDaoRepository.countByPublishingStatus(IdentifierPublishingStatus.FAILED));
		model.addAttribute("identifierPendingCount", identifierDaoRepository.countByPublishingStatus(IdentifierPublishingStatus.PENDING));

		DocumentStat documentStat = new DocumentStat();
		List<Map<String,Object>> statusStat = documentDaoRepository.loadDocumentStatusStat();
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
