package dk.erst.delis.web.error;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.web.datatables.service.EasyDatatablesListService;
import dk.erst.delis.web.datatables.service.EasyDatatablesListServiceImpl;
import dk.erst.delis.web.document.DocumentStatusBachUdpateInfo;
import dk.erst.delis.web.list.AbstractEasyListController;

@Controller
public class ErrorDictionaryController extends AbstractEasyListController<ErrorDictionary> {

    @Autowired
    private ErrorDictionaryService service;

    @RequestMapping("/errordict/view/{id}")
    public String list(@PathVariable Long id, Model model) {
        ErrorDictionaryData errorDictionaryWithStats = service.getErrorDictionaryWithStats(id);
        model.addAttribute("header", "Back to list");
        model.addAttribute("errorDictionary", errorDictionaryWithStats);
        return "/errordict/view";
    }

    @RequestMapping("/errordict/listdocument/{id}")
    public String listDocument(@PathVariable Long id, Model model) {
        List<Document> list = service.documentList(id);
        model.addAttribute("documentList", list);
        model.addAttribute("selectedIdList", new DocumentStatusBachUdpateInfo());
        model.addAttribute("statusList", DocumentStatus.values());
        return "/document/list";
    }

    
	/*
	 * START EasyDatatables block
	 */
	@Autowired
	private ErrorDictionaryDataTableRepository errorDictionaryDataTableRepository;
	@Autowired
	private EasyDatatablesListServiceImpl<ErrorDictionary> errorDictionaryEasyDatatablesListService;
	
	@Override
	protected String getListCode() {
		return "errordict";
	}
	@Override
	protected DataTablesRepository<ErrorDictionary, Long> getDataTableRepository() {
		return this.errorDictionaryDataTableRepository;
	}
	@Override
	protected EasyDatatablesListService<ErrorDictionary> getEasyDatatablesListService() {
		return this.errorDictionaryEasyDatatablesListService;
	}

	@RequestMapping("/errordict/list")
	public String list(Model model, WebRequest webRequest) {
		model.addAttribute("errorTypeList", DocumentErrorCode.values());
		return super.list(model, webRequest);
	}
	/*
	 * END EasyDatatables block
	 */	
}
