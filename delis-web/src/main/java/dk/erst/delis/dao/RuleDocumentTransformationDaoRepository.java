package dk.erst.delis.dao;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RuleDocumentTransformationDaoRepository extends PagingAndSortingRepository<RuleDocumentTransformation, Long> {

}
