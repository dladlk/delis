package dk.erst.delis.persistence.stat;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Transactional
public interface StatDao {

	List<KeyValue> loadStat(StatRange range, boolean groupHourNotDate, int addHours, Long organisationId);

	StatRange loadFullRange(Long organisationId);

	Date loadDbTimeNow();
	
	@Getter
	@Setter
	public static class StatRange implements Serializable {
		private static final long serialVersionUID = 493729743470091098L;

		private Date from;
		private Date to;

		public static StatRange of(Date from, Date to) {
			StatRange sr = new StatRange();
			sr.from = from;
			sr.to = to;
			return sr;
		}

		public static StatRange of(ZonedDateTime startDate, ZonedDateTime endDate) {
			return of(toDate(startDate), toDate(endDate));
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
		
		private static Date toDate(ZonedDateTime dt) {
			if (dt != null) {
				return Date.from(dt.toInstant());
			}
			return null;
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
