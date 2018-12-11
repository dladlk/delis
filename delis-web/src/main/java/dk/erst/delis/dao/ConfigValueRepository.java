package dk.erst.delis.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.ConfigValue;
import dk.erst.delis.data.ConfigValueType;

public interface ConfigValueRepository extends PagingAndSortingRepository<ConfigValue, Long> {

	public ConfigValue findByConfigValueType(ConfigValueType configValueType);

}
