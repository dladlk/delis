package dk.erst.delis.service.info;

import com.google.common.reflect.ClassPath;

import dk.erst.delis.data.annotations.WebApiContent;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.info.TableInfoData;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import org.springframework.stereotype.Service;

import java.io.IOException;
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

    private static final String BASE_ENTITY_PACKAGE = "dk.erst.delis.data.entities";

    public ListContainer<TableInfoData> getTableInfoByAllEntities() {

        List<String> packages = Arrays.stream(
                Package.getPackages())
                .map(Package::getName)
                .filter(pack -> pack.startsWith(BASE_ENTITY_PACKAGE))
                .filter(pack -> ObjectUtils.notEqual(pack, BASE_ENTITY_PACKAGE))
                .collect(Collectors.toList());

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        List<Class> entityClasses = new ArrayList<>();

        packages.forEach(pack -> {
            try {
                for (ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                    if (info.getName().startsWith(pack)) {
                        Class<?> entity = info.load();
                        if (Objects.nonNull(entity.getAnnotation(WebApiContent.class))) {
                            entityClasses.add(entity);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("problem scan package: " + e.getMessage());
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
