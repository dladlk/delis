package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.entities.rule.RuleDocumentValidation;

public interface RuleDocumentValidationDaoRepository extends PagingAndSortingRepository<RuleDocumentValidation, Long> {

	Iterable<RuleDocumentValidation> findAllByActive(boolean active);

	@Query("select s from RuleDocumentValidation s order by s.documentFormat asc, s.validationType desc, s.priority asc")
	List<RuleDocumentValidation> loadAllSorted();
}
