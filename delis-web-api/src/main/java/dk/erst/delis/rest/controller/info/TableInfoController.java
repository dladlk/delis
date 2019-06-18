package dk.erst.delis.rest.controller.info;

import dk.erst.delis.service.info.TableInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/open/table-info")
public class TableInfoController {

    private final TableInfoService tableInfoService;

    @Autowired
    public TableInfoController(TableInfoService tableInfoService) {
        this.tableInfoService = tableInfoService;
    }

    @GetMapping("/enums")
    public ResponseEntity getTableInfoByAllEntities() {
        return ResponseEntity.ok(tableInfoService.getTableInfoByAllEntities());
    }

    @GetMapping("/organizations")
    public ResponseEntity getUniqueOrganizationNameData() {
        return ResponseEntity.ok(tableInfoService.getUniqueOrganizationNameData());
    }
}
