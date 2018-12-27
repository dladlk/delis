package dk.erst.delis.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StatData {

	private Map<String, int[]> statMap;

	private long startMs;

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

	public String toStatString() {
		if (statMap.isEmpty()) {
			return "Nothing";
		}

		String loadStatStr = statMap.keySet().stream().sorted()

				.map(s -> s + ": " + statMap.get(s)[0])

				.collect(Collectors.joining(", "));

		return loadStatStr;
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
}
