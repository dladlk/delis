package dk.erst.delis;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import dk.erst.delis.dao.ConfigValueDaoRepository;
import dk.erst.delis.data.enums.config.ConfigValueType;

public class TestUtil {
	
	private static ConfigValueDaoRepository configValueDaoRepository;

	public static InputStream getResourceByClass(Class<?> cls, String suffix) {
		return cls.getResourceAsStream(cls.getSimpleName()+"_"+suffix);
	}
	
	public static ConfigValueDaoRepository getEmptyConfigValueDaoRepository() {
		if (configValueDaoRepository == null) {
			ConfigValueDaoRepository configRepository = mock(ConfigValueDaoRepository.class);
			when(configRepository.findByConfigValueType(any(ConfigValueType.class))).then(d -> {
				return null;
			});
			configValueDaoRepository = configRepository;
		}
		
		return configValueDaoRepository;
	}
	
}
