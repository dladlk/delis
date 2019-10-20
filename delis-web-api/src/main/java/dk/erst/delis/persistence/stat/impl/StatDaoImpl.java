package dk.erst.delis.persistence.stat.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.persistence.stat.StatDao;

@Repository
public class StatDaoImpl implements StatDao {

	@Autowired
	private EntityManager entityManager;

	public Date loadDbTimeNow() {
		return (Date) entityManager.createNativeQuery("select now()").getSingleResult();
	}

	public int loadDeliveryAlertCount(Long organisationId) {
		String jql = "select count(s) from Document s where s.documentStatus = :status";
		if (organisationId != null) {
			jql += " and s.organisationId = :organisationId";
		}
		Query q = entityManager.createQuery(jql);
		q.setParameter("status", DocumentStatus.DELIVER_PENDING);
		if (organisationId != null) {
			q.setParameter("organisationId", organisationId);
		}
		Number result = (Number) q.getSingleResult();
		if (result != null) {
			return result.intValue();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<KeyValue> loadStat(StatType statType, StatRange range, boolean groupHourNotDate, int addHours, Long organisationId) {
		String keyExpression = buildKeyExpression(groupHourNotDate, addHours);

		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append("		" + keyExpression + ", ");
		sb.append("		count(*) ");
		sb.append("	from ");
		sb.append(statType.getTableName());
		sb.append(" d ");

		StringBuilder where = new StringBuilder();

		if (range.getFrom() != null) {
			appendAndIf(where);
			where.append("	d.create_time >= :from ");
		}
		if (range.getTo() != null) {
			appendAndIf(where);
			where.append(" d.create_time <= :to ");
		}
		if (organisationId != null) {
			appendAndIf(where);
			where.append(" d.organisation_id = :organisationId ");
		}
		if (statType.isLimitError()) {
			appendAndIf(where);
			where.append(" d.document_status in (");
			DocumentStatus[] values = DocumentStatus.values();
			boolean first = true;
			for (DocumentStatus documentStatus : values) {
				if (documentStatus.isError()) {
					if (!first) {
						where.append(",");
					}
					where.append("'");
					where.append(documentStatus.name());
					where.append("'");
					first = false;
				}
			}
			where.append(")");
		}

		if (where.length() > 0) {
			sb.append("	where ");
			sb.append(where);
		}

		sb.append("	group by " + keyExpression);
		sb.append("	order by " + keyExpression);
		String query = sb.toString();

		Query q = entityManager.createNativeQuery(query);
		if (organisationId != null) {
			q.setParameter("organisationId", organisationId);
		}

		if (range.getFrom() != null) {
			q.setParameter("from", range.getFrom() + " 00:00:00");
		}

		if (range.getTo() != null) {
			q.setParameter("to", range.getTo() + " 23:59:59");
		}

		List<Object[]> list = q.getResultList();

		List<KeyValue> r = new ArrayList<StatDao.KeyValue>();
		for (Object[] objects : list) {
			String key = (objects[0]).toString();
			long value = ((Number) objects[1]).longValue();
			r.add(new KeyValue(key, value));
		}

		return r;
	}

	private void appendAndIf(StringBuilder sbWhere) {
		if (sbWhere.length() > 0) {
			sbWhere.append(" and ");
		}
	}

	private String buildKeyExpression(boolean groupHourNotDate, int addHours) {
		StringBuilder sb = new StringBuilder();
		sb.append("DATE_FORMAT(");
		if (addHours != 0) {
			sb.append(" DATE_SUB(d.create_time, INTERVAL ");
			sb.append(addHours * -1);
			sb.append(" HOUR) ");
		} else {
			sb.append(" d.create_time ");
		}
		sb.append(", '%Y-%m-%d ");
		if (groupHourNotDate) {
			sb.append("%H");
		} else {
			sb.append("00");
		}
		sb.append(":00:00')");
		return sb.toString();

	}

	@Override
	public StatRange loadFullRange(Long organisationId) {
		return null;
	}

}
