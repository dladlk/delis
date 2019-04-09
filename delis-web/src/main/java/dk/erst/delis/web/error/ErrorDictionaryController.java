package dk.erst.delis.web.error;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.web.document.DocumentStatusBachUdpateInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/errordict")
public class ErrorDictionaryController {

    @Autowired
    private ErrorDictionaryService service;

    @RequestMapping("list")
    public String list(Model model) {
        return listFilter(model);
    }

    @PostMapping("list/filter")
    public String listFilter(Model model) {
        Iterable<ErrorDictionary> errorDictionaries = service.errorDictionaryList();
        model.addAttribute("errorDictList", errorDictionaries);
        return "/error/list";
    }

    @RequestMapping("view/{id}")
    public String list(@PathVariable Long id, Model model) {
        ErrorDictionaryData errorDictionaryWithStats = service.getErrorDictionaryWithStats(id);
        model.addAttribute("header", "Back to list");
        model.addAttribute("errorDictionary", errorDictionaryWithStats);
        return "/error/view";
    }

    @RequestMapping("/listdocument/{id}")
    public String listDocument(@PathVariable Long id, Model model) {
        List<Document> list = service.documentList(id);
        model.addAttribute("documentList", list);
        model.addAttribute("selectedIdList", new DocumentStatusBachUdpateInfo());
        model.addAttribute("statusList", DocumentStatus.values());
        return "/document/list";
    }
}
