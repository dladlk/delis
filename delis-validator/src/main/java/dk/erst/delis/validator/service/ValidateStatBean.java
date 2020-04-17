package dk.erst.delis.validator.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import dk.erst.delis.data.enums.document.DocumentFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Component
@Data
public class ValidateStatBean {

	private static final String PROCESS_DURATION = "PROCESS_DURATION";
	private static final String FILE_SIZE = "FILE_SIZE";

	private Map<String, Counter> counterMap = new LinkedHashMap<String, Counter>();

	@Getter
	private long totalCount;

	public ValidateStatBean() {
		/*
		 * Initialize map with all possible values to avoid map modifications in future
		 */
		DocumentFormat[] formats = DocumentFormat.values();
		for (DocumentFormat format : formats) {
			getCounterFormat(format);
		}
		getCounterFormat(null);
		ValidateResultStatus[] statuses = ValidateResultStatus.values();
		for (ValidateResultStatus status : statuses) {
			getCounterStatus(status);
		}
		getCounterStatus(null);

		getCounter(FILE_SIZE, true);
		getCounter(PROCESS_DURATION, true);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Total ");
		sb.append(this.totalCount);
		for (String name : counterMap.keySet()) {
			Counter counter = counterMap.get(name);
			if (counter.count > 0) {
				sb.append("\n ");
				sb.append(counter);
			}
		}
		return sb.toString();
	}

	private Counter getCounterStatus(ValidateResultStatus status) {
		return getCounter("Status_" + status, false);
	}

	private Counter getCounterFormat(DocumentFormat format) {
		return getCounter("Format_" + format, false);
	}

	public void increment(ValidateResultStatus status, DocumentFormat documentFormat, long fileSize, long duration) {
		this.totalCount++;
		getCounterStatus(status).increment(1);
		getCounterFormat(documentFormat).increment(1);
		getCounter(FILE_SIZE, true).increment(fileSize);
		getCounter(PROCESS_DURATION, true).increment(duration);
	}

	private Counter getCounter(String name, boolean minMax) {
		Counter c = counterMap.get(name);
		if (c == null) {
			if (minMax) {
				c = new CounterMinMax(name);
			} else {
				c = new Counter(name);
			}
			counterMap.put(name, c);
		}
		return c;
	}

	@Data
	static class Counter {
		String name;

		long count;
		long total;

		public Counter(String name) {
			this.name = name;
		}

		void increment(long value) {
			this.count++;
			this.total += value;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(name);
			sb.append(": ");
			sb.append(this.count);
			return sb.toString();
		}
	}

	@Getter
	@Setter
	static class CounterMinMax extends Counter {
		long min = -1;
		long max;

		public CounterMinMax(String name) {
			super(name);
		}

		void increment(long value) {
			super.increment(value);
			if (this.max < value) {
				this.max = value;
			}
			if (this.min < 0 || this.min > value) {
				this.min = value;
			}
		}

		public long getAverage() {
			if (this.count > 0) {
				return this.total / this.count;
			}
			return -1;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append(", total = ");
			sb.append(this.total);
			sb.append(", min = ");
			sb.append(this.min);
			sb.append(", max = ");
			sb.append(this.max);
			return sb.toString();
		}
	}

	public void increment(ValidateResult result) {
		ValidateResultStatus status = result.getRestResult() != null ? result.getRestResult().getStatus() : null;
		this.increment(status, result.getDocumentFormat(), result.getFileSize(), result.getDuration());
	}

}
