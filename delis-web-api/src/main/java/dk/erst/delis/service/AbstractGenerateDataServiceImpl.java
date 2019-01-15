package dk.erst.delis.service;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.persistence.*;
import dk.erst.delis.rest.data.response.PageContainer;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author funtusthan, created by 13.01.19
 */

@Service
public class AbstractGenerateDataServiceImpl<
        R extends AbstractRepository,
        F extends AbstractFilterModel,
        S extends AbstractSpecification,
        E extends AbstractEntity>
        implements AbstractGenerateDataService<R, E, S, F> {

    @Override
    public PageContainer<E> sortProcess(
            Class<E> entityClass,
            String sort,
            WebRequest request,
            int page, int size, long collectionSize,
            R repository,
            F filterModel,
            S specification) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(entityClass.getDeclaredFields()));
        fields.addAll(Arrays.asList(entityClass.getSuperclass().getDeclaredFields()));
        for ( Field field : fields ) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (sort.toUpperCase().endsWith(field.getName().toUpperCase())) {
                    int count = Integer.parseInt(Objects.requireNonNull(request.getParameter(sort)));
                    if (count == 1) {
                        return getDescendingDataPageContainer(page, size, collectionSize, field.getName(), repository, filterModel, specification);
                    } else if (count == 2) {
                        return getAscendingDataPageContainer(page, size, collectionSize, field.getName(), repository, filterModel, specification);
                    }
                }
            }
        }
        return getDefaultDataPageContainerWithoutSorting(page, size, collectionSize, repository, filterModel, specification);
    }

    @Override
    public PageContainer<E> getDefaultDataPageContainer(int page, int size, long collectionSize, R repository) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(
                PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent());
    }

    @Override
    public PageContainer<E> getDefaultDataPageContainerWithoutSorting(int page, int size, long collectionSize, R repository, F filterModel, S specification) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(
                specification.generateCriteriaPredicate(filterModel),
                PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent());
    }

    private PageContainer<E> getDescendingDataPageContainer(int page, int size, long collectionSize, String param, R repository, F filterModel, S specification) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(
                        specification.generateCriteriaPredicate(filterModel),
                        PageRequest.of(page - 1, size, Sort.by(param).descending())).getContent());
    }

    private PageContainer<E> getAscendingDataPageContainer(int page, int size, long collectionSize, String param, R repository, F filterModel, S specification) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(
                        specification.generateCriteriaPredicate(filterModel),
                        PageRequest.of(page - 1, size, Sort.by(param).ascending())).getContent());
    }
}
