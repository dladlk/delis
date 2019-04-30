package dk.erst.delis.pagefiltering.service;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.pagefiltering.exception.model.FieldErrorModel;
import dk.erst.delis.pagefiltering.exception.statuses.RestNotFoundException;
import dk.erst.delis.pagefiltering.persistence.AbstractRepository;
import dk.erst.delis.pagefiltering.persistence.specification.EntitySpecification;
import dk.erst.delis.pagefiltering.persistence.specification.EntitySpecificationFactory;
import dk.erst.delis.pagefiltering.request.param.PageAndSizeModel;
import dk.erst.delis.pagefiltering.response.PageContainer;
import dk.erst.delis.pagefiltering.response.SortingDirection;
import dk.erst.delis.pagefiltering.util.ClassLoaderUtil;
import dk.erst.delis.pagefiltering.util.WebRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Objects;

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
        String specificFlag = WebRequestUtil.existFlagParameter(request);
        EntitySpecification entitySpecification;
        Specification<E> specification;
        if (StringUtils.isNotBlank(specificFlag)) {
            entitySpecification = EntitySpecification.valueOf(request.getParameter(specificFlag));
            specification = new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request);
        } else {
            entitySpecification = EntitySpecification.DEFAULT;
            specification = new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request, entityClass);
        }
        long collectionSize = repository.count(specification);
        if (collectionSize == 0) {
            return new PageContainer<>();
        }
        return sortProcess(entityClass, request, pageAndSizeModel.getPage(), pageAndSizeModel.getSize(), collectionSize, repository, specification);
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
            Specification<E> specification) {
        String sort = request.getParameter("sort");
        if (sort != null) {
            String[] strings = sort.split("_");
            for ( Field field : ClassLoaderUtil.getAllFieldsByEntity(entityClass) ) {
                if (Modifier.isPrivate(field.getModifiers())) {
                    if (Objects.equals(strings[1].toUpperCase(), field.getName().toUpperCase())) {
                        if (Objects.equals(strings[2], "Asc")) {
                            return getAscendingDataPageContainer(page, size, collectionSize, field.getName(), repository, specification);
                        } else {
                            return getDescendingDataPageContainer(page, size, collectionSize, field.getName(), repository, specification);
                        }
                    }
                }
            }
        }
        return getDefaultDataPageContainerWithoutSorting(page, size, collectionSize, repository, specification);
    }

    private PageContainer<E> getDefaultDataPageContainerWithoutSorting(int page, int size, long collectionSize, R repository, Specification<E> specification) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(specification, PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent(), "id", SortingDirection.Desc);
    }

    private PageContainer<E> getDescendingDataPageContainer(int page, int size, long collectionSize, String param, R repository, Specification<E> specification) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(specification, PageRequest.of(page - 1, size, Sort.by(param).descending())).getContent(), param, SortingDirection.Desc);
    }

    private PageContainer<E> getAscendingDataPageContainer(int page, int size, long collectionSize, String param, R repository, Specification<E> specification) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(specification, PageRequest.of(page - 1, size, Sort.by(param).ascending())).getContent(), param, SortingDirection.Asc);
    }
}
