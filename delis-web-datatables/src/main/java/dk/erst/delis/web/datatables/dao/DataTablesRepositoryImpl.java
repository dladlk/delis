package dk.erst.delis.web.datatables.dao;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import dk.erst.delis.web.datatables.data.DataTablesOutput;
import dk.erst.delis.web.datatables.data.PageData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataTablesRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>  implements DataTablesRepository<T, ID>  {

	protected JpaEntityInformation<T, ?> entityInformation;
	protected EntityManager entityManager;

	public DataTablesRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityInformation = entityInformation;
		this.entityManager = entityManager;
	}

	public DataTablesOutput<T> findAll(PageData pageData, ICriteriaCustomizer<T> customizer) {
		log.debug(pageData.toString());
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Long> queryCount = builder.createQuery(Long.class);
		Root<T> from = queryCount.from(entityInformation.getJavaType());
		queryCount.select(builder.count(from));

		Predicate restrictions = buildRestrictions(pageData, customizer, builder, queryCount, from);
		
		if (restrictions != null) {
			queryCount.where(restrictions);
		}
		Long count = entityManager.createQuery(queryCount).getSingleResult();
		if (count.intValue() < (pageData.getPage() - 1) * pageData.getSize()) {
			pageData.setPage(1);
		}
		
		CriteriaQuery<T> queryList = builder.createQuery(entityInformation.getJavaType());
		Root<T> root = queryList.from(entityInformation.getJavaType());
		queryList = queryList.select(root);
		
		restrictions = buildRestrictions(pageData, customizer, builder, queryList, root);
		
		if (restrictions != null) {
			queryList.where(restrictions);
		}
		if (StringUtils.isNotBlank(pageData.getOrder())) {
			String orderData[] = pageData.getOrder().split("_");
			String orderField = orderData[0];
			boolean orderDesc = "desc".equals(orderData[1]);
			
			Order orderBy = builder.asc(buildFieldPath(root, orderField));
			if (orderDesc) {
				orderBy = orderBy.reverse();
			}
			queryList.orderBy(orderBy);
		}
		
		TypedQuery<T> q = entityManager.createQuery(queryList);
		q.setMaxResults(pageData.getSize());
		q.setFirstResult((pageData.getPage() - 1) * pageData.getSize());
		
		List<T> list = q.getResultList();

		DataTablesOutput<T> dto = new DataTablesOutput<T>();
		dto.setData(list);
		dto.setRecordsTotal(count);
		return dto;
	}

	private Predicate buildRestrictions(PageData pageData, ICriteriaCustomizer<T> customizer, CriteriaBuilder builder, CriteriaQuery<?> query, Root<T> from) {
		Predicate restrictions = null;
		Map<String, String> filterMap = pageData.getFilterMap();
		Set<String> filterKeySet = filterMap.keySet();
		for (String filterKey : filterKeySet) {
			Predicate filterPredicate = null;
			String filterValue = filterMap.get(filterKey);
			if (StringUtils.isEmpty(filterValue)) {
				continue;
			}
			
			Path<Object> fieldPath = buildFieldPath(from, filterKey);
			filterPredicate = buildPredicate(builder, fieldPath, filterValue);
			
			if (filterPredicate == null) {
				throw new RuntimeException("Cannot build predicate for filter key "+filterKey);
			}
			restrictions = joinPredicates(builder, restrictions, filterPredicate);
		}

		if (customizer != null) {
			Predicate customPredicates = customizer.customPredicates(pageData, builder, query, from);
			if (customPredicates != null) {
				restrictions = joinPredicates(builder, restrictions, customPredicates);
			}
		}
		return restrictions;
	}

	protected Predicate joinPredicates(CriteriaBuilder cb, Predicate current, Predicate additional) {
		if (current == null) {
			current = cb.and(additional);
		} else {
			current = cb.and(current, additional);
		}
		return current;
	}

	private Path<Object> buildFieldPath(Root<T> from, String filterKey) {
		Path<Object> fieldPath;
		if (isLocalField(filterKey)) {
			fieldPath = from.get(filterKey);
		} else {
			int subEntityFieldStart = filterKey.indexOf('.');
			String subEntity = filterKey.substring(0, subEntityFieldStart);
			String subEntityField = filterKey.substring(subEntityFieldStart + 1);
			Join<Object, Object> join = from.join(subEntity, JoinType.LEFT);
			fieldPath = join.get(subEntityField);
		}
		return fieldPath;
	}

	protected Predicate buildPredicate(CriteriaBuilder cb, Path<Object> fieldPath, String filterValue) {
		Predicate filterPredicate = null;
		Class<?> fieldJavaType = fieldPath.getJavaType();
		if (fieldJavaType.equals(String.class)) {
			filterPredicate = cb.like(cb.lower(fieldPath.as(String.class)), "%" + filterValue.toLowerCase() + "%");
		} else if (fieldJavaType.isEnum()) {
			filterPredicate = cb.equal(fieldPath.as(String.class), filterValue);
		} else if (fieldJavaType.equals(Boolean.class) || fieldJavaType.equals(boolean.class)) {
			filterPredicate = cb.equal(fieldPath, Boolean.valueOf(filterValue));
		} else if (fieldJavaType.equals(Long.class)) {
			long parseLong = Long.parseLong(filterValue);
			if (parseLong == 0) {
				filterPredicate = cb.isNull(fieldPath);
			} else {
				filterPredicate = cb.equal(fieldPath.as(Long.class), parseLong);
			}
		} else if (fieldJavaType.equals(Date.class)) {
			String fromTo[] = filterValue.split("_");
			filterPredicate = cb.between(fieldPath.as(Date.class), date(fromTo[0]), date(fromTo[1]));
		}
		return filterPredicate;
	}
	
	protected boolean isLocalField(String filterKey) {
		return !filterKey.contains(".");
	}

	public static Date date(String s) {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd-HHmmss");
		try {
			return f.parse(s);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
