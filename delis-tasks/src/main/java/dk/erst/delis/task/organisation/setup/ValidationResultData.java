package dk.erst.delis.task.organisation.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationResultData {

	private Map<String, List<String>> errorMap;
	
	public ValidationResultData() {
		this.errorMap = new HashMap<String, List<String>>();
	}
	
	public void addError(String key, String message) {
		List<String> list = this.errorMap.get(key);
		if (list == null) {
			list = new ArrayList<String>();
			this.errorMap.put(key, list);
		}
		list.add(message);
	}
	
	public boolean isInvalid(String key) {
		return errorMap.containsKey(key);
	}
	
	public List<String> getErrorList(String key) {
		return errorMap.get(key);
	}

	public boolean isAllValid() {
		return this.errorMap.isEmpty();
	}
}
