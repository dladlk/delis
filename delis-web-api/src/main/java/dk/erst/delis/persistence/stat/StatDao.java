package dk.erst.delis.persistence.stat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Transactional
public interface StatDao {

	public static enum StatType {
		
		RECEIVE ("document", "chart.receiving"), 
		
		SEND ("send_document", "chart.sending"), 
		
		RECEIVE_ERROR ("document", "chart.receiving_error");
		
		private String tableName;
		private String chartLabel;

		private StatType (String tableName, String chartLabel) {
			this.tableName = tableName;
			this.chartLabel = chartLabel;
		}
		
		public boolean isLimitError() {
			return this == RECEIVE_ERROR;
		}

		public String getTableName() {
			return tableName;
		}

		public String getChartLabel() {
			return chartLabel;
		}
	}
	
	List<KeyValue> loadStat(StatType statType, StatRange range, boolean groupHourNotDate, int addHours, Long organisationId);

	StatRange loadFullRange(Long organisationId);

	Date loadDbTimeNow();
	
	@Getter
	@Setter
	public static class StatRange implements Serializable {
		private static final long serialVersionUID = 493729743470091098L;

		private String from;
		private String to;

		public boolean isSingleDay() {
			return from != null && to != null && from.equals(to);
		}

		public static StatRange of(String from, String to) {
			StatRange sr = new StatRange();
			sr.from = cleanDate(from);
			sr.to = cleanDate(to);
			return sr;
		}

		private static String cleanDate(String str) {
			if (str != null) {
				str = str.trim();
				if (str.length() == 0) {
					return null;
				}
				return str;
			}
			return null;
		}

		public boolean isAnyDefined() {
			return this.from != null || this.to != null; 
		}
		public boolean isBothDefined() {
			return this.from != null && this.to != null; 
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(from);
			sb.append("-");
			sb.append(to);
			return sb.toString();
		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class KeyValue implements Serializable {
		private static final long serialVersionUID = 688609103092319163L;

		private String key;
		private long value;

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(key);
			sb.append("=");
			sb.append(value);
			return sb.toString();
		}
	}

}
