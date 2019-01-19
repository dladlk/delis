package dk.erst.delis.rest.controller.info;

import dk.erst.delis.service.info.TableInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author funtusthan, created by 19.01.19
 */

@RestController
@RequestMapping("/rest/table-info")
public class TableInfoController {

    private final TableInfoService tableInfoService;

    @Autowired
    public TableInfoController(TableInfoService tableInfoService) {
        this.tableInfoService = tableInfoService;
    }

    @GetMapping
    public ResponseEntity getTableInfoByAllEntities() {
        return ResponseEntity.ok(tableInfoService.getTableInfoByAllEntities());
    }
}
