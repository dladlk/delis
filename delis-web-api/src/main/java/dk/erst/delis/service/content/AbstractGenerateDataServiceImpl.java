package dk.erst.delis.service.content;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.*;
import dk.erst.delis.persistence.specification.EntitySpecificationFactory;
import dk.erst.delis.persistence.specification.EntitySpecification;
import dk.erst.delis.rest.data.request.param.PageAndSizeModel;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.util.ClassLoaderUtil;
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
            return new PageContainer<>();
        }
        String specificFlag = WebRequestUtil.existFlagParameter(request);
        EntitySpecification entitySpecification;
        if (StringUtils.isNotBlank(specificFlag)) {
            entitySpecification = EntitySpecification.valueOf(request.getParameter(specificFlag));
        } else {
            entitySpecification = EntitySpecification.DEFAULT;
        }

        return sortProcess(entityClass, request, pageAndSizeModel.getPage(), pageAndSizeModel.getSize(), collectionSize, repository, entitySpecification);
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
            WebRequest request,
            int page, int size, long collectionSize,
            R repository,
            EntitySpecification entitySpecification) {
        String[] strings = Objects.requireNonNull(request.getParameter("sort")).split("_");
        for ( Field field : ClassLoaderUtil.getAllFieldsByEntity(entityClass) ) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (Objects.equals(strings[1].toUpperCase(), field.getName().toUpperCase())) {
                    if (Objects.equals(strings[2], "Asc")) {
                        return getAscendingDataPageContainer(entityClass, page, size, collectionSize, field.getName(), repository, request, entitySpecification);
                    } else {
                        return getDescendingDataPageContainer(entityClass, page, size, collectionSize, field.getName(), repository, request, entitySpecification);
                    }
                }
            }
        }
        return getDefaultDataPageContainerWithoutSorting(entityClass, request, page, size, collectionSize, repository, entitySpecification);
    }

    private PageContainer<E> getDefaultDataPageContainerWithoutSorting(Class<E> entityClass, WebRequest request, int page, int size, long collectionSize,
                                                                       R repository, EntitySpecification entitySpecification) {
        if (Objects.equals(entitySpecification, EntitySpecification.DEFAULT)) {
            return new PageContainer<E>(page, size, collectionSize, repository
                    .findAll(
                            new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request, entityClass),
                            PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent());
        } else {
            return new PageContainer<E>(page, size, collectionSize, repository
                    .findAll(
                            new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request),
                            PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent());
        }
    }

    private PageContainer<E> getDescendingDataPageContainer(Class<E> entityClass, int page, int size, long collectionSize, String param,
                                                            R repository, WebRequest request, EntitySpecification entitySpecification) {
        if (Objects.equals(entitySpecification, EntitySpecification.DEFAULT)) {
            return new PageContainer<E>(page, size, collectionSize, repository
                    .findAll(
                            new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request, entityClass),
                            PageRequest.of(page - 1, size, Sort.by(param).descending())).getContent());
        } else {
            return new PageContainer<E>(page, size, collectionSize, repository
                    .findAll(
                            new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request),
                            PageRequest.of(page - 1, size, Sort.by(param).descending())).getContent());
        }
    }

    private PageContainer<E> getAscendingDataPageContainer(Class<E> entityClass, int page, int size, long collectionSize, String param,
                                                           R repository, WebRequest request, EntitySpecification entitySpecification) {
        if (Objects.equals(entitySpecification, EntitySpecification.DEFAULT)) {
            return new PageContainer<E>(page, size, collectionSize, repository
                    .findAll(
                            new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request, entityClass),
                            PageRequest.of(page - 1, size, Sort.by(param).ascending())).getContent());
        } else {
            return new PageContainer<E>(page, size, collectionSize, repository
                    .findAll(
                            new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request),
                            PageRequest.of(page - 1, size, Sort.by(param).ascending())).getContent());
        }
    }
}
