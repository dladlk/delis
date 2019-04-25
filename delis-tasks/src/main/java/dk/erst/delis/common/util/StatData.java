package dk.erst.delis.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatData {

	private Map<String, int[]> statMap;

	private long startMs;
	
	private Object result;

	public StatData() {
		this.startMs = System.currentTimeMillis();
		this.statMap = new HashMap<>();
	}

	public void increment(String key) {
		String code = key == null ? "UNDEFINED" : key;
		int[] c = statMap.get(code);
		if (c == null) {
			statMap.put(code, new int[] { 1 });
		} else {
			c[0]++;
		}
	}
	
	@Override
	public String toString() {
		return toStatString();
	}

	public String toStatString() {
		if (isEmpty()) {
			return "Nothing";
		}

		StringBuilder sb = new StringBuilder();
		
		List<String> keyList = new ArrayList<>(statMap.keySet());
		Collections.sort(keyList);
		for (String key : keyList) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(key);
			sb.append(": ");
			sb.append(statMap.get(key)[0]);
		}
		String loadStatStr = sb.toString();
		return loadStatStr;
	}

	public boolean isEmpty() {
		return statMap.isEmpty();
	}

	public long getStartMs() {
		return startMs;
	}

	public String toDurationString() {
		StringBuilder sb = new StringBuilder();
		sb.append(System.currentTimeMillis() - startMs);
		sb.append(" ms");
		return sb.toString();
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
