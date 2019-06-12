package dk.erst.delis.service.content;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestForbiddenException;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.*;
import dk.erst.delis.persistence.specification.EntitySpecificationFactory;
import dk.erst.delis.persistence.specification.EntitySpecification;
import dk.erst.delis.rest.data.request.param.PageAndSizeModel;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.ClassLoaderUtil;
import dk.erst.delis.util.SecurityUtil;
import dk.erst.delis.util.SpecificationUtil;
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Service
public class AbstractGenerateDataServiceImpl<R extends AbstractRepository, E extends AbstractEntity> implements AbstractGenerateDataService<R, E> {

    @Autowired
    private SecurityService securityService;

    @Override
    public PageContainer<E> generateDataPageContainer(Class<E> entityClass, WebRequest request, R repository) {
        Long orgId = null;
        if (SecurityUtil.hasRole("ROLE_USER")) {
            orgId = securityService.getOrganisation().getId();
        }
        PageAndSizeModel pageAndSizeModel = WebRequestUtil.generatePageAndSizeModel(request);
        String specificFlag = WebRequestUtil.existFlagParameter(request);
        EntitySpecification entitySpecification;
        Specification<E> specification;
        if (StringUtils.isNotBlank(specificFlag)) {
            entitySpecification = EntitySpecification.valueOf(request.getParameter(specificFlag));
            specification = new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request, orgId);
        } else {
            entitySpecification = EntitySpecification.DEFAULT;
            specification = new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request, entityClass, orgId);
        }
        long collectionSize = repository.count(specification);
        if (collectionSize == 0) {
            return new PageContainer<>();
        }
        return sortProcess(entityClass, request, pageAndSizeModel.getPage(), pageAndSizeModel.getSize(), collectionSize, repository, specification);
    }

    @Override
    public E getOneById(long id, Class<E> entityClass, R repository) {
        Long orgId = null;
        if (SecurityUtil.hasRole("ROLE_USER")) {
            orgId = securityService.getOrganisation().getId();
        }
        if (orgId == null) {
            E entity = (E) repository.findById(id).orElse(null);
            if (Objects.isNull(entity)) {
                throw new RestNotFoundException(Collections.singletonList(
                        new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), entityClass.getName() + " not found by id")));
            }
            return entity;
        } else {
            Long finalOrgId = orgId;
            Specification<E> specification = (Specification<E>) (root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = SpecificationUtil.generateSpecificationPredicatesByOrganisation(finalOrgId, entityClass, root, criteriaBuilder);
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            List<E> es = repository.findAll(specification);
            if (CollectionUtils.isNotEmpty(es)) {
                return es.get(0);
            } else {
                throw new RestForbiddenException(Collections.singletonList(
                        new FieldErrorModel("id", HttpStatus.FORBIDDEN.getReasonPhrase(), "you do not have permission to view this document.")));
            }
        }
    }

    private PageContainer<E> sortProcess(
            Class<E> entityClass,
            WebRequest request,
            int page, int size, long collectionSize,
            R repository,
            Specification<E> specification) {
        String[] strings = Objects.requireNonNull(request.getParameter("sort")).split("_");
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
        return getDefaultDataPageContainerWithoutSorting(page, size, collectionSize, repository, specification);
    }

    private PageContainer<E> getDefaultDataPageContainerWithoutSorting(int page, int size, long collectionSize, R repository, Specification<E> specification) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(specification, PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent());
    }

    private PageContainer<E> getDescendingDataPageContainer(int page, int size, long collectionSize, String param, R repository, Specification<E> specification) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(specification, PageRequest.of(page - 1, size, Sort.by(param).descending())).getContent());
    }

    private PageContainer<E> getAscendingDataPageContainer(int page, int size, long collectionSize, String param, R repository, Specification<E> specification) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(specification, PageRequest.of(page - 1, size, Sort.by(param).ascending())).getContent());
    }
}
