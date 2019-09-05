package dk.erst.delis.web.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.web.datatables.service.EasyDatatablesListService;
import dk.erst.delis.web.datatables.service.EasyDatatablesListServiceImpl;
import dk.erst.delis.web.document.DocumentStatusBachUdpateInfo;
import dk.erst.delis.web.error.ErrorDictionaryStatRepository.ErrorDictionaryStat;
import dk.erst.delis.web.list.AbstractEasyListController;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Controller
public class ErrorDictionaryController extends AbstractEasyListController<ErrorDictionary> {

    @Autowired
    private ErrorDictionaryService service;
    @Autowired
    private ErrorDictionaryStatRepository errorDictionaryStatRepository;

    @RequestMapping("/errordict/view/{id}")
    public String view(@PathVariable Long id, Model model, RedirectAttributes ra) {
        ErrorDictionary error = service.getErrorDictionary(id);
        if (error == null) {
			ra.addFlashAttribute("errorMessage", "Error is not found");
        	return "redirect:/errordict/list";
        }
    	
        model.addAttribute("errorDictionary", error);

    	ErrorDictionaryStat errorStatTotal = errorDictionaryStatRepository.findErrorStatByErrorId(id);
    	if (errorStatTotal != null) {
    		List<ErrorStatTypeGroup> groupList = new ArrayList<ErrorDictionaryController.ErrorStatTypeGroup>();
    		
			groupList.add(new ErrorStatTypeGroup(errorStatTotal));
	    	groupList.add(new ErrorStatTypeGroup("Sender country", errorDictionaryStatRepository.loadErrorStatBySenderCountry(id)));
	    	groupList.add(new ErrorStatTypeGroup("Sender name", errorDictionaryStatRepository.loadErrorStatBySenderName(id)));
	    	model.addAttribute("errorStatGroupList", groupList);
    	}
    	
    	return "/errordict/view";
    }
    
    @Getter
    @AllArgsConstructor
    private static class ErrorStatTypeGroup {
    	private String name;
    	private List<ErrorDictionaryStat> list;
    	
    	public ErrorStatTypeGroup(ErrorDictionaryStat stat) {
    		this.list = new ArrayList<ErrorDictionaryStatRepository.ErrorDictionaryStat>();
    		this.list.add(stat);
    	}
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
