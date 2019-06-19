package dk.erst.delis.service.info;

import com.google.common.collect.Lists;
import dk.erst.delis.data.enums.Named;
import dk.erst.delis.persistence.repository.organization.OrganizationRepository;
import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.info.EnumInfo;
import dk.erst.delis.rest.data.response.info.TableInfoData;
import dk.erst.delis.rest.data.response.info.UniqueOrganizationNameData;
import dk.erst.delis.util.ClassLoaderUtil;
import dk.erst.delis.util.SecurityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TableInfoService {

    private final OrganizationRepository organizationRepository;
    private String locale;

    @Autowired
    public TableInfoService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public ListContainer<TableInfoData> getTableInfoByAllEntities(String locale) {
        this.locale = locale;
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

    public DataContainer<UniqueOrganizationNameData> getUniqueOrganizationNameData(String locale) {
        String organisation = SecurityUtil.getOrganisation();
        if (organisation == null) {
            List<String> organisations = organizationRepository.findDistinctName();
            if (CollectionUtils.isNotEmpty(organisations)) {
                String all;
                if (StringUtils.equals(locale, "en")) {
                    all = "All";
                } else {
                    all = "Alle";
                }
                organisations.add(all);
                organisations = new ArrayList<>(Lists.reverse(organisations));
                return new DataContainer<>(new UniqueOrganizationNameData(organisations));
            } else {
                return new DataContainer<>(new UniqueOrganizationNameData(Collections.emptyList()));
            }
        }

        return new DataContainer<>(new UniqueOrganizationNameData(Collections.singletonList(organisation)));
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
                        boolean currentLocaleEN = StringUtils.equals(this.locale, "en");
                        List<EnumInfo> enumInfoList = Arrays
                                .stream(((Class<Enum>) field.getType()).getEnumConstants())
                                .map(en -> {
                                            EnumInfo enumInfo = new EnumInfo();
                                            enumInfo.setName(en.name());

                                            if (currentLocaleEN) {
                                                enumInfo.setViewName(((Named) en).getName());
                                            } else {
                                                enumInfo.setViewName(((Named) en).getNameDa());
                                            }

                                            return enumInfo;
                                        }
                                )
                                .collect(Collectors.toList());

                        EnumInfo enumInfo = new EnumInfo();
                        enumInfo.setName("ALL");
                        if (currentLocaleEN) {
                            enumInfo.setViewName("All");
                        } else {
                            enumInfo.setViewName("Alle");
                        }
                        enumInfoList.add(enumInfo);
                        enumInfoList = new ArrayList<>(Lists.reverse(enumInfoList));

                        entityEnumInfo.put(field.getName(), enumInfoList);
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
