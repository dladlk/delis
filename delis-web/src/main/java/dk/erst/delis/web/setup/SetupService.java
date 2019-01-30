package dk.erst.delis.web.setup;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.ConfigValueDaoRepository;
import dk.erst.delis.data.entities.config.ConfigValue;
import dk.erst.delis.data.enums.config.ConfigValueType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupService {
    private ConfigValueDaoRepository configValueDaoRepository;

    @Autowired
    public SetupService(ConfigValueDaoRepository configValueDaoRepository) {
        this.configValueDaoRepository = configValueDaoRepository;
    }

    public Iterable<ConfigValue> getAllTypesFromDB() {
        Iterable<ConfigValue> valueTypes = configValueDaoRepository.findAll(Sort.by("configValueType"));
        return valueTypes;
    }

    public ConfigValue findById(long id) {
        ConfigValue value = configValueDaoRepository.findById(id).get();
        return value;
    }

    public ConfigValue getConfigValueForType(String typeName) {
        ConfigValue value = null;

        ConfigValueType configValueType = ConfigValueType.valueOf(typeName);
        if (configValueType != null) {
            value = configValueDaoRepository.findByConfigValueType(configValueType);
            if (value == null) {
                value = new ConfigValue();
                value.setConfigValueType(configValueType);
                String defaultValue = System.getenv(configValueType.getKey());
                if (defaultValue == null) {
                    defaultValue = configValueType.getDefaultValue();
                }
                value.setValue(defaultValue);
            }
        }

        return value;
    }

    public void save (ConfigValue configValue) {
        configValueDaoRepository.save(configValue);;
    }

    public List<ConfigValueData> createConfigValuesList (ConfigBean configBean) {
        ArrayList<ConfigValueData> configValueData = new ArrayList<>();
        configValueData.add(new ConfigValueData("Storage for Documents input", "config.storageDocumentInput",configBean.getStorageInputPath(), ConfigValueType.STORAGE_INPUT_ROOT.name()));
//        configValueData.add(new ConfigValueData("Root Storage for Documents", "config.storageDocumentRoot",configBean.getDocumentRootPath(), ConfigValueType.STORAGE_DOCUMENT_ROOT.name()));
        configValueData.add(new ConfigValueData("Storage for Validation files", "config.storageValidationRoot",configBean.getStorageValidationPath(), ConfigValueType.STORAGE_VALIDATION_ROOT.name()));
        configValueData.add(new ConfigValueData("Storage of Transformation files", "config.storageTransformationRoot",configBean.getStorageTransformationPath(), ConfigValueType.STORAGE_TRANSFORMATION_ROOT.name()));
        configValueData.add(new ConfigValueData("Storage of Code lists files", "config.storageCodeListsRoot",configBean.getStorageCodeListPath(), ConfigValueType.CODE_LISTS_PATH.name()));
        configValueData.add(new ConfigValueData("SMP url", "config.smp.url",configBean.getSmpEndpointConfig().getUrl(), ConfigValueType.ENDPOINT_URL.name()));
        configValueData.add(new ConfigValueData("SMP user", "config.smp.userName",configBean.getSmpEndpointConfig().getUserName(), ConfigValueType.ENDPOINT_USER_NAME.name()));
        configValueData.add(new ConfigValueData("SMP password", "config.smp.password",configBean.getSmpEndpointConfig().getPassword(), ConfigValueType.ENDPOINT_PASSWORD.name()));
        configValueData.add(new ConfigValueData("SMP format", "config.smp.format",configBean.getSmpEndpointConfig().getFormat(), ConfigValueType.ENDPOINT_FORMAT.name()));
        return configValueData;
    }

    public void updateFromDB (ConfigBean configBean) {

    }
}
