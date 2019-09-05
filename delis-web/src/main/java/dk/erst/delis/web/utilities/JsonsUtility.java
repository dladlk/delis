package dk.erst.delis.web.utilities;

import org.springframework.stereotype.Component;

import dk.erst.delis.web.datatables.util.JsonUtil;

@Component("jsons")
public class JsonsUtility {

	public String json(Object o) {
		return JsonUtil.json(o);
	}
	
}
