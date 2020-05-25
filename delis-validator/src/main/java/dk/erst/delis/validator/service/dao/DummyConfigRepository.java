package dk.erst.delis.validator.service.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import dk.erst.delis.dao.ConfigValueDaoRepository;
import dk.erst.delis.data.entities.config.ConfigValue;
import dk.erst.delis.data.enums.config.ConfigValueType;

public class DummyConfigRepository implements ConfigValueDaoRepository {

	private List<ConfigValue> configValueList;

	public DummyConfigRepository(List<ConfigValue> configValueList) {
		this.configValueList = configValueList;
	}

	@Override
	public Iterable<ConfigValue> findAll() {
		return this.configValueList;
	}

	@Override
	public Iterable<ConfigValue> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<ConfigValue> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends ConfigValue> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends ConfigValue> Iterable<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<ConfigValue> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<ConfigValue> findAllById(Iterable<Long> ids) {
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
	public void delete(ConfigValue entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(Iterable<? extends ConfigValue> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public ConfigValue findByConfigValueType(ConfigValueType configValueType) {
		// TODO Auto-generated method stub
		return null;
	}

}
