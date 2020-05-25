package dk.erst.delis.dao;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RuleDocumentTransformationDaoRepository extends PagingAndSortingRepository<RuleDocumentTransformation, Long> {

	Iterable<RuleDocumentTransformation> findAllByActive(boolean active);

	@Query("select s from RuleDocumentTransformation s order by s.documentFormatFamilyFrom, s.documentFormatFamilyTo")
	List<RuleDocumentTransformation> loadAllSorted();

}
