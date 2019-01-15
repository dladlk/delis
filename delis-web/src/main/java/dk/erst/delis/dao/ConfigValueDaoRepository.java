package dk.erst.delis.dao;

import dk.erst.delis.data.entities.config.ConfigValue;
import dk.erst.delis.data.enums.config.ConfigValueType;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ConfigValueDaoRepository extends PagingAndSortingRepository<ConfigValue, Long> {

	public ConfigValue findByConfigValueType(ConfigValueType configValueType);

}
