package dk.erst.delis.validator.service.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import dk.erst.delis.dao.RuleDocumentValidationDaoRepository;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;

public class DummyValidationRepository implements RuleDocumentValidationDaoRepository {

	private List<RuleDocumentValidation> list;

	public DummyValidationRepository(List<RuleDocumentValidation> list) {
		this.list = list;
	}

	@Override
	public Iterable<RuleDocumentValidation> findAll() {
		return this.list;
	}

	@Override
	public Iterable<RuleDocumentValidation> findAllByActive(boolean active) {
		return this.findAll();
	}

	@Override
	public Iterable<RuleDocumentValidation> findAll(Sort sort) {
		return this.findAll();
	}

	@Override
	public Page<RuleDocumentValidation> findAll(Pageable pageable) {
		return new PageImpl<RuleDocumentValidation>(this.list);
	}

	@Override
	public <S extends RuleDocumentValidation> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends RuleDocumentValidation> Iterable<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<RuleDocumentValidation> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<RuleDocumentValidation> findAllById(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(RuleDocumentValidation entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(Iterable<? extends RuleDocumentValidation> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<RuleDocumentValidation> loadAllSorted() {
		// TODO Auto-generated method stub
		return null;
	}

}
