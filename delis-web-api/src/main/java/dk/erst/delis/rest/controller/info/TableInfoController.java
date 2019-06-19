package dk.erst.delis.rest.controller.info;

import dk.erst.delis.service.info.TableInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/rest/table-info")
public class TableInfoController {

    private final TableInfoService tableInfoService;

    @Autowired
    public TableInfoController(TableInfoService tableInfoService) {
        this.tableInfoService = tableInfoService;
    }

    @GetMapping("/enums")
    public ResponseEntity getTableInfoByAllEntities(HttpServletRequest httpServletRequest) {
        String locale = httpServletRequest.getParameter("locale_lang");
        return ResponseEntity.ok(tableInfoService.getTableInfoByAllEntities((locale != null) ? locale : "da"));
    }

    @GetMapping("/organizations")
    public ResponseEntity getUniqueOrganizationNameData(HttpServletRequest httpServletRequest) {
        String locale = httpServletRequest.getParameter("locale_lang");
        return ResponseEntity.ok(tableInfoService.getUniqueOrganizationNameData((locale != null) ? locale : "da"));
    }
}
