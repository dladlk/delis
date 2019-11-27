package dk.erst.delis.dao;

import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RuleDocumentValidationDaoRepository extends PagingAndSortingRepository<RuleDocumentValidation, Long> {

	Iterable<RuleDocumentValidation> findAllByActive(boolean active);

}
