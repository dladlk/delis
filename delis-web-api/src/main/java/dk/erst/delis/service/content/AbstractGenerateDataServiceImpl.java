package dk.erst.delis.service.content;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.*;
import dk.erst.delis.rest.data.request.param.PageAndSizeModel;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author funtusthan, created by 13.01.19
 */

@Service
public class AbstractGenerateDataServiceImpl<
        R extends AbstractRepository,
        E extends AbstractEntity>
        implements AbstractGenerateDataService<R, E> {

    @Override
    public PageContainer<E> generateDataPageContainer(Class<E> entityClass, WebRequest request, R repository) {
        PageAndSizeModel pageAndSizeModel = WebRequestUtil.generatePageAndSizeModel(request);
        long collectionSize = repository.count();
        if (collectionSize == 0) {
            return getDefaultDataPageContainer(pageAndSizeModel.getPage(), pageAndSizeModel.getSize(), collectionSize, repository);
        }

        String sort = WebRequestUtil.existSortParameter(request);
        if (StringUtils.isNotBlank(sort)) {
            return sortProcess(entityClass, sort, request, pageAndSizeModel.getPage(), pageAndSizeModel.getSize(), collectionSize, repository);
        } else {
            return getDefaultDataPageContainerWithoutSorting(entityClass, request, pageAndSizeModel.getPage(), pageAndSizeModel.getSize(), collectionSize, repository);
        }
    }

    @Override
    public E getOneById(long id, Class<E> entityClass, R repository) {
        E entity = (E) repository.findById(id).orElse(null);
        if (Objects.isNull(entity)) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), entityClass.getName() + " not found by id")));
        }
        return entity;
    }

    private PageContainer<E> sortProcess(
            Class<E> entityClass,
            String sort,
            WebRequest request,
            int page, int size, long collectionSize,
            R repository) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(entityClass.getDeclaredFields()));
        fields.addAll(Arrays.asList(entityClass.getSuperclass().getDeclaredFields()));
        for ( Field field : fields ) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (sort.toUpperCase().endsWith(field.getName().toUpperCase())) {
                    int count = Integer.parseInt(Objects.requireNonNull(request.getParameter(sort)));
                    if (count == 1) {
                        return getDescendingDataPageContainer(entityClass, page, size, collectionSize, field.getName(), repository, request);
                    } else if (count == 2) {
                        return getAscendingDataPageContainer(entityClass, page, size, collectionSize, field.getName(), repository, request);
                    }
                }
            }
        }
        return getDefaultDataPageContainerWithoutSorting(entityClass, request, page, size, collectionSize, repository);
    }

    private PageContainer<E> getDefaultDataPageContainer(int page, int size, long collectionSize, R repository) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(
                PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent());
    }

    private PageContainer<E> getDefaultDataPageContainerWithoutSorting(Class<E> entityClass, WebRequest request, int page, int size, long collectionSize, R repository) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(
                new AbstractSpecificationProcess<E>().generateCriteriaPredicate(request, entityClass),
                PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent());
    }

    private PageContainer<E> getDescendingDataPageContainer(Class<E> entityClass, int page, int size, long collectionSize, String param, R repository, WebRequest request) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(
                        new AbstractSpecificationProcess<E>().generateCriteriaPredicate(request, entityClass),
                        PageRequest.of(page - 1, size, Sort.by(param).descending())).getContent());
    }

    private PageContainer<E> getAscendingDataPageContainer(Class<E> entityClass, int page, int size, long collectionSize, String param, R repository, WebRequest request) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(
                        new AbstractSpecificationProcess<E>().generateCriteriaPredicate(request, entityClass),
                        PageRequest.of(page - 1, size, Sort.by(param).ascending())).getContent());
    }
}
