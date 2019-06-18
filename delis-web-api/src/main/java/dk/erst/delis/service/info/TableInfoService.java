package dk.erst.delis.service.info;

import dk.erst.delis.data.enums.Named;
import dk.erst.delis.persistence.repository.organization.OrganizationRepository;
import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.info.EnumInfo;
import dk.erst.delis.rest.data.response.info.TableInfoData;
import dk.erst.delis.rest.data.response.info.UniqueOrganizationNameData;
import dk.erst.delis.util.ClassLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TableInfoService {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public TableInfoService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public ListContainer<TableInfoData> getTableInfoByAllEntities() {
        List<Class> entityClasses = ClassLoaderUtil.findAllWebApiContentEntityClasses();
        if (CollectionUtils.isNotEmpty(entityClasses)) {
            return new ListContainer<>(
                    generateTableInfoDataList(entityClasses)
                            .stream()
                            .filter(entity -> CollectionUtils.isNotEmpty(entity.getEntityEnumInfo().keySet()))
                            .collect(Collectors.toList()));
        } else {
            return new ListContainer<>(Collections.emptyList());
        }
    }

    public DataContainer<UniqueOrganizationNameData> getUniqueOrganizationNameData() {
        List<String> organisations = organizationRepository.findDistinctName();
        if (CollectionUtils.isNotEmpty(organisations)) {
            return new DataContainer<>(new UniqueOrganizationNameData(organisations));
        } else {
            return new DataContainer<>(new UniqueOrganizationNameData(Collections.emptyList()));
        }
    }

    private List<TableInfoData> generateTableInfoDataList(List<Class> entities) {
        return entities
                .stream()
                .map(this::generateTableInfoData)
                .collect(Collectors.toList());
    }

    private TableInfoData generateTableInfoData(Class entity) {
        Map<String, List<EnumInfo>> entityEnumInfo = new HashMap<>();
        List<Field> fields = new ArrayList<>(Arrays.asList(entity.getDeclaredFields()));
        for (Field field : fields) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (Enum.class.isAssignableFrom(field.getType())) {
                    Class<?>[] enumSuperClassList = field.getType().getInterfaces();
                    Class<?> enumSuperClass = Arrays.stream(enumSuperClassList).filter(aClass -> aClass.isAssignableFrom(Named.class)).getClass();
                    if (enumSuperClass == null) {
                        entityEnumInfo.put(
                                field.getName(),
                                Arrays
                                        .stream(((Class<Enum>) field.getType()).getEnumConstants())
                                        .map(en -> new EnumInfo(en.name(), en.name()))
                                        .collect(Collectors.toList()));
                    } else {
                        entityEnumInfo.put(
                                field.getName(),
                                Arrays
                                        .stream(((Class<Enum>) field.getType()).getEnumConstants())
                                        .map(en -> {
                                                    EnumInfo enumInfo = new EnumInfo();
                                                    enumInfo.setName(en.name());
                                                    enumInfo.setViewName(((Named) en).getName());
                                                    return enumInfo;
                                                }
                                        )
                                        .collect(Collectors.toList()));
                    }
                }
            }
        }

        TableInfoData data = new TableInfoData();
        data.setEntityName(ClassLoaderUtil.generateEntityByFullNameClass(entity));
        data.setEntityEnumInfo(entityEnumInfo);

        return data;
    }
}
