package dk.erst.delis.web.list;

import dk.erst.delis.web.datatables.data.PageData;
import dk.erst.delis.web.datatables.web.EasyDatatablesListController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.WebRequest;

import dk.erst.delis.config.web.security.CustomUserDetails;

public abstract class AbstractEasyListController<T> extends EasyDatatablesListController<T> {

	@Override
	protected PageData updatePageData(WebRequest webRequest) {
		PageData pageData = super.updatePageData(webRequest);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if (principal instanceof CustomUserDetails) {
			CustomUserDetails cud = (CustomUserDetails) principal;
			
			if (cud.getOrganisation() != null) {
				pageData.addFilterValue("organisation.id", String.valueOf(cud.getOrganisation().getId()));
			}
		}
		
		return pageData;
	}	
}
