package dk.erst.delis.web.datatables.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageData implements Serializable {

	private static final long serialVersionUID = -2954553322587783857L;
	private static final PageData DEFAULT = new PageData();

	private int page;
	private int size;
	private long totalElements;
	private long displayStart;
	private long displayEnd;

	/*
	 * "title asc", "id desc"
	 */
	private String order;

	/*
	 * "title" -> "My name", "createDate" -> "20.02.2019 - 30.05.2019"
	 */
	private Map<String, String> filterMap;

	public PageData() {
		this.clear();
	}

	public static PageData getDefault() {
		return DEFAULT;
	}

	public void addFilterValue(String fieldName, String singleValue) {
		String values[];
		if (singleValue == null) {
			values = null;
		} else {
			values = new String[] { singleValue };
		}
		this.addFilterValue(fieldName, values);
	}

	public void addFilterValue(String fieldName, String[] values) {
		if (values == null || values.length == 0) {
			this.filterMap.remove(fieldName);
		} else {
			if (values.length == 1) {
				if (values[0] != null) {
					filterMap.put(fieldName, values[0].trim());
				} else {
					filterMap.remove(fieldName);
				}
			} else {
				StringBuilder sb = new StringBuilder();
				for (String string : values) {
					if (string != null) {
						if (sb.length() > 0) {
							sb.append(",");
						}
						sb.append(string);
					}
				}
				if (sb.length() > 0) {
					filterMap.put(fieldName, sb.toString());
				} else {
					filterMap.remove(fieldName);
				}
			}
		}
	}

	public void clear() {
		this.page = 1;
		this.size = 10;
		this.totalElements = 0;
		this.displayStart = 1;
		this.displayEnd = this.size;
		this.order = null;
		this.filterMap = new HashMap<String, String>();
	}

	public void clearFilter() {
		this.filterMap.clear();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(page);
		sb.append(" ");
		sb.append(size);
		sb.append(" ");
		sb.append(order);
		sb.append(" ");
		sb.append(filterMap);
		return sb.toString();
	}
}
