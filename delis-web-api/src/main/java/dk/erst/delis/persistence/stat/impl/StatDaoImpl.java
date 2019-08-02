package dk.erst.delis.persistence.stat.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dk.erst.delis.persistence.stat.StatDao;

@Repository
public class StatDaoImpl implements StatDao {

	@Autowired
	private EntityManager entityManager;

	public Date loadDbTimeNow() {
		return (Date) entityManager.createNativeQuery("select now()").getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<KeyValue> loadStat(StatRange range, boolean groupHourNotDate, int addHours, Long organisationId) {
		String keyExpression = buildKeyExpression(groupHourNotDate, addHours);

		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append("		" + keyExpression + ", ");
		sb.append("		count(*) ");
		sb.append("	from document d ");

		if (range.isAnyDefined()) {
			sb.append("	where ");
		}
		if (range.getFrom() != null) {
			sb.append("	d.create_time >= :from ");
		}
		if (range.isBothDefined()) {
			sb.append(" and ");
		}
		if (range.getTo() != null) {
			sb.append(" d.create_time <= :to ");
		}

		if (organisationId != null) {
			sb.append(" AND	 d.organisation_id = :organisationId ");
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
