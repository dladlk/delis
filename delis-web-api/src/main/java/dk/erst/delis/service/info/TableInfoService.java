package dk.erst.delis.service.info;

import dk.erst.delis.data.annotations.WebApiContent;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.info.TableInfoData;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;

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

    public ListContainer<TableInfoData> getTableInfoByAllEntities() {

        List<Class> entityClasses = new ArrayList<>();

        Arrays.asList(Document.class, JournalDocument.class).forEach(entityClass -> {
            if (Objects.nonNull(entityClass.getAnnotation(WebApiContent.class))) {
                entityClasses.add(entityClass);
            }
        });

        return new ListContainer(generateTableInfoDataList(entityClasses).stream().filter(entity -> CollectionUtils.isNotEmpty(entity.getEntityEnumInfo().keySet())).collect(Collectors.toList()));
    }

    private List<TableInfoData> generateTableInfoDataList(List<Class> enities) {
        return enities.stream().map(this::generateTableInfoData).collect(Collectors.toList());
    }

    private TableInfoData generateTableInfoData(Class entity) {

        Map<String, List<String>> entityEnumInfo = new HashMap<>();
        List<Field> fields = new ArrayList<>(Arrays.asList(entity.getDeclaredFields()));
        for ( Field field : fields ) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (Enum.class.isAssignableFrom(field.getType())) {
                    entityEnumInfo.put(field.getName(), Arrays.stream(((Class<Enum>) field.getType()).getEnumConstants()).map(Enum::name).collect(Collectors.toList()));
                }
            }
        }

        TableInfoData data = new TableInfoData();
        data.setEntityName(Arrays.stream(entity.getName().split("\\.")).reduce((first, last) -> last).get());
        data.setEntityEnumInfo(entityEnumInfo);

        return data;
    }
}
