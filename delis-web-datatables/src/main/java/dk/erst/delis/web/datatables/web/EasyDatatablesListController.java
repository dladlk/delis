package dk.erst.delis.web.datatables.web;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.context.request.WebRequest;

import dk.erst.delis.web.datatables.dao.DataTablesRepository;
import dk.erst.delis.web.datatables.dao.ICriteriaCustomizer;
import dk.erst.delis.web.datatables.data.DataTablesOutput;
import dk.erst.delis.web.datatables.data.PageData;
import dk.erst.delis.web.datatables.data.SessionData;
import dk.erst.delis.web.datatables.service.EasyDatatablesListService;
import dk.erst.delis.web.datatables.util.DataTablesUtil;

public abstract class EasyDatatablesListController<T> implements PageDataBuilder {

	private static final String FILTER_PARAM_PREFIX = "filter_";
	private static final int DEFAULT_PAGE_SIZE = 10;

	@Autowired
	protected SessionData sessionData;

	protected abstract String getListCode();

	protected abstract DataTablesRepository<T, Long> getDataTableRepository();

	protected abstract EasyDatatablesListService<T> getEasyDatatablesListService();
	
	protected ICriteriaCustomizer<T> getCriteriaCustomizer() {
		return null;
	}

	protected String list(Model model, WebRequest webRequest) {
		PageData pageData = updatePageData(webRequest);

		DataTablesOutput<T> dto;
		if (getDataTableRepository() != null) {
			dto = getEasyDatatablesListService().getDataTablesOutput(pageData, getDataTableRepository(), getCriteriaCustomizer());
		} else {
			dto = getEasyDatatablesListService().getDataTablesOutput(pageData);
		}

		if (StringUtils.isNotBlank(dto.getError())) {
			model.addAttribute("errorMessage", dto.getError());
		}
		
		DataTablesUtil.updatePageData(pageData, dto.getRecordsTotal());

		model.addAttribute(getListCode() + "List", dto.getData());
		model.addAttribute(getListCode() + "Page", pageData);

		return getListCode() + "/list";
	}

	protected PageData updatePageData(WebRequest webRequest) {
		PageData pd = getPageData();
		if (webRequest.getParameter("clear") != null) {
			pd.clear();
		}

		String pageParam = webRequest.getParameter("page");
		if (StringUtils.isNotBlank(pageParam)) {
			int page = Integer.parseInt(pageParam);
			pd.setPage(page);
		}
		String sizeParam = webRequest.getParameter("size");
		if (StringUtils.isNotBlank(sizeParam)) {
			int size = Integer.parseInt(sizeParam);
			pd.setSize(size);
		}

		String orderParam = webRequest.getParameter("order");
		if (StringUtils.isNotBlank(sizeParam)) {
			pd.setOrder(orderParam);
		}

		Map<String, String[]> parameterMap = webRequest.getParameterMap();
		for (String paramName : parameterMap.keySet()) {
			if (paramName.startsWith(FILTER_PARAM_PREFIX)) {
				pd.addFilterValue(paramName.substring(FILTER_PARAM_PREFIX.length()), parameterMap.get(paramName));
			}
		}
		return pd;
	}

	protected PageData getPageData() {
		return sessionData.getOrCreatePageData(this.getClass(), this);
	}
	
	public PageData buildInitialPageData() {
		PageData pd = new PageData();
		pd.setSize(DEFAULT_PAGE_SIZE);
		return pd;
	}
}
