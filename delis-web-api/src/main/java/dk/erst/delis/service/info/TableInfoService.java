package dk.erst.delis.service.info;

import dk.erst.delis.persistence.repository.organization.OrganizationRepository;
import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.ListContainer;
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

/**
 * @author funtusthan, created by 19.01.19
 */

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

        Map<String, List<String>> entityEnumInfo = new HashMap<>();
        List<Field> fields = new ArrayList<>(Arrays.asList(entity.getDeclaredFields()));
        for ( Field field : fields ) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (Enum.class.isAssignableFrom(field.getType())) {
                    entityEnumInfo.put(
                            field.getName(),
                            Arrays
                                    .stream(((Class<Enum>) field.getType()).getEnumConstants())
                                    .map(Enum::name)
                                    .collect(Collectors.toList()));
                }
            }
        }

        TableInfoData data = new TableInfoData();
        data.setEntityName(ClassLoaderUtil.generateEntityByFullNameClass(entity));
        data.setEntityEnumInfo(entityEnumInfo);

        return data;
    }
}
