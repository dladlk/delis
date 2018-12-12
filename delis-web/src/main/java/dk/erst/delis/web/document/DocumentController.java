package dk.erst.delis.web.document;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.dao.DocumentRepository;
import dk.erst.delis.dao.JournalDocumentRepository;
import dk.erst.delis.data.Document;

@Controller
public class DocumentController {

	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private JournalDocumentRepository journalDocumentRepository;

	@RequestMapping("/document/list")
	public String list(Model model) {
		return listFilter(model);
	}
	
	@PostMapping("/document/list/filter")
	public String listFilter(Model model) {
		List<Document> list;
		list = documentRepository.findAll(PageRequest.of(0, 10, Sort.by("id").descending())).getContent();
		model.addAttribute("documentList", list);
		return "/document/list";
	}
	
	@GetMapping("/document/view/{id}")
	public String view(@PathVariable long id, Model model, RedirectAttributes ra) {
		Document document = documentRepository.findById(id).get();
		if (document == null) {
			ra.addFlashAttribute("errorMessage", "Document is not found");
			return "redirect:/home";
		}
		
		model.addAttribute("document", document);
		model.addAttribute("lastJournalList", journalDocumentRepository.findTop5ByDocumentOrderByIdDesc(document));
		
		return "/document/view";
	}

}
