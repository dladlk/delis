package dk.erst.delis.validator.service.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import dk.erst.delis.dao.RuleDocumentTransformationDaoRepository;
import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;

public class DummyTransformationRepository implements RuleDocumentTransformationDaoRepository {

	private List<RuleDocumentTransformation> list;

	public DummyTransformationRepository(List<RuleDocumentTransformation> list) {
		this.list = list;
	}

	@Override
	public Iterable<RuleDocumentTransformation> findAll() {
		return this.list;
	}

	@Override
	public Iterable<RuleDocumentTransformation> findAllByActive(boolean active) {
		return this.findAll();
	}

	@Override
	public Iterable<RuleDocumentTransformation> findAll(Sort sort) {
		return this.findAll();
	}

	@Override
	public Page<RuleDocumentTransformation> findAll(Pageable pageable) {
		return new PageImpl<RuleDocumentTransformation>(this.list);
	}

	@Override
	public <S extends RuleDocumentTransformation> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends RuleDocumentTransformation> Iterable<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<RuleDocumentTransformation> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<RuleDocumentTransformation> findAllById(Iterable<Long> ids) {
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
	public void delete(RuleDocumentTransformation entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(Iterable<? extends RuleDocumentTransformation> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<RuleDocumentTransformation> loadAllSorted() {
		// TODO Auto-generated method stub
		return null;
	}

}
