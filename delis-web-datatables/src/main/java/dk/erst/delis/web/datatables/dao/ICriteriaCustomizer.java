package dk.erst.delis.web.datatables.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import dk.erst.delis.web.datatables.data.PageData;

public interface ICriteriaCustomizer<T> {

	Predicate customPredicates(PageData pageData, CriteriaBuilder cb, CriteriaQuery<?> cqCount, Root<T> from);

}
