package dk.erst.delis.pagefiltering.util;

import dk.erst.delis.data.annotations.WebApiContent;
import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.pagefiltering.exception.model.FieldErrorModel;
import dk.erst.delis.pagefiltering.exception.statuses.RestBadRequestException;
import dk.erst.delis.pagefiltering.persistence.AbstractRepository;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpStatus;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author funtusthan, created by 25.01.19
 */

@Slf4j
@UtilityClass
public class ClassLoaderUtil {

    private static final String BASE_ENTITY_PACKAGE = AbstractEntity.class.getPackage().getName();

    public List<Class<?>> findAllWebApiContentEntityClasses() {
        ClassLoader loader = AbstractEntity.class.getClassLoader();
        List<Class<?>> entityClasses = new ArrayList<>();
        try {
            Resource[] resources = scan(loader);
            for (Resource resource : resources) {
                MetadataReader reader = new CachingMetadataReaderFactory(loader).getMetadataReader(resource);
                Class<?> entity = ClassUtils.forName(reader.getClassMetadata().getClassName(), loader);
                if (Objects.nonNull(entity.getAnnotation(WebApiContent.class))) {
                    entityClasses.add(entity);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("problem reading from entity classes" + e.getMessage());
            return Collections.emptyList();
        }
        return entityClasses;
    }

    public String generateEntityByFullNameClass(Class<?> entityName) {
        return Arrays.stream(entityName.getName().split("\\.")).reduce((first, last) -> last).orElse(null);
    }

    public List<Field> getAllFieldsByEntity(Class<?> entityClass) {
        List<Field> fields = new ArrayList<>();
        Class<?> nextClass = entityClass;
        do {
            Field[] innerFields = nextClass.getDeclaredFields();
            fields.addAll(Arrays.asList(innerFields));
            nextClass = nextClass.getSuperclass();
        } while (Objects.nonNull(nextClass));

        return fields;
    }

    public Field getFieldsByEntity(Class<?> entityClass, String fieldName) {
        Field field = null;
        Class<?> nextClass = entityClass;
        do {
            field = Arrays.stream(nextClass.getDeclaredFields()).filter(f -> f.getName().equals(fieldName)).findFirst().get();
            nextClass = nextClass.getSuperclass();
        } while (Objects.isNull(field) || Objects.nonNull(nextClass));

        return field;
    }

    public Class<?> findRepositoryByEntityMapper(String entityClass, ListableBeanFactory listableBeanFactory) {
        Repositories repositories = new Repositories(listableBeanFactory);
        for (Class<?> domainClass : repositories) {
            RepositoryInformation repositoryInformation = repositories.getRepositoryInformationFor(domainClass).orElse(null);
            if (Objects.nonNull(repositoryInformation)) {
                Class<?> domainType = repositoryInformation.getDomainType();
                String domainName = generateEntityByFullNameClass(domainType);
                if (Objects.isNull(domainName)) {
                    throwError(entityClass);
                }
                if (Objects.equals(domainName, entityClass)) {
                    if (repositoryInformation.getRepositoryInterface().isAssignableFrom(AbstractRepository.class)) {
                        return repositoryInformation.getRepositoryInterface();
                    }
                }
            }
        }
        return throwError(entityClass);
    }

    private Resource[] scan(ClassLoader loader) throws IOException {
        return new PathMatchingResourcePatternResolver(loader)
                .getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX.concat(ClassUtils.convertClassNameToResourcePath(BASE_ENTITY_PACKAGE) + "/**/*.class"));
    }

    private Class<?> throwError(String entityClass) {
        log.error("invalid entity name : " + entityClass + " from request");
        throw new RestBadRequestException(Collections.singletonList(
                new FieldErrorModel("WebRequest", HttpStatus.BAD_REQUEST.getReasonPhrase(), "invalid entity name from request")));
    }
}
