package dk.erst.delis.rest.controller.info;

import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.info.TableInfoData;
import dk.erst.delis.rest.data.response.info.UniqueOrganizationNameData;
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
    public ResponseEntity<ListContainer<TableInfoData>> getTableInfoByAllEntities(HttpServletRequest httpServletRequest) {
    	
        ListContainer<TableInfoData> list = tableInfoService.getTableInfoByAllEntities(getLocale(httpServletRequest));
        System.out.println(list);
        
        String str = DocumentStatus.LOAD_OK.getNameDa();
        System.out.println(str);
        
		return ResponseEntity.ok(list);
    }

    @GetMapping("/organizations")
    public ResponseEntity<DataContainer<UniqueOrganizationNameData>> getUniqueOrganizationNameData(HttpServletRequest httpServletRequest) {
        DataContainer<UniqueOrganizationNameData> list = tableInfoService.getUniqueOrganizationNameData(getLocale(httpServletRequest));
		return ResponseEntity.ok(list);
    }

	private String getLocale(HttpServletRequest httpServletRequest) {
		String locale = httpServletRequest.getParameter("locale_lang");
        String useLocale = (locale != null) ? locale : "da";
		return useLocale;
	}
}
