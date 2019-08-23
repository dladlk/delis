package dk.erst.delis.service.content;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.persistence.*;
import dk.erst.delis.persistence.specification.EntitySpecificationFactory;
import dk.erst.delis.persistence.specification.EntitySpecification;
import dk.erst.delis.rest.data.request.param.PageAndSizeModel;
import dk.erst.delis.rest.data.request.param.SortModel;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.ClassLoaderUtil;
import dk.erst.delis.util.SecurityUtil;
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Service
public class AbstractGenerateDataServiceImpl<R extends AbstractRepository, E extends AbstractEntity> implements AbstractGenerateDataService<R, E> {

    private final SecurityService securityService;

    public AbstractGenerateDataServiceImpl(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public PageContainer<E> generateDataPageContainer(Class<E> entityClass, WebRequest request, R repository) {
        Long orgId = null;
        if (SecurityUtil.hasRole("ROLE_USER")) {
            orgId = securityService.getOrganisation().getId();
        }
        PageAndSizeModel pageAndSizeModel = WebRequestUtil.generatePageAndSizeModel(request);
        String errorStatus = WebRequestUtil.existFlagErrorParameter(request);
        EntitySpecification entitySpecification = EntitySpecification.DEFAULT;
        Specification<E> specification;
        if (StringUtils.isNotBlank(errorStatus)) {
            if (Boolean.parseBoolean(request.getParameter(errorStatus))) {
                entitySpecification = EntitySpecification.FLAG_ERRORS_DOCUMENT;
                specification = new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request, orgId);

            } else {
                specification = new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request, entityClass, orgId);
            }
        } else {
            specification = new EntitySpecificationFactory().generateSpecification(entitySpecification).generateCriteriaPredicate(request, entityClass, orgId);
        }
        long collectionSize = repository.count(specification);
        if (collectionSize == 0) {
            return new PageContainer<>();
        }
        return sortProcess(entityClass, request, pageAndSizeModel.getPage(), pageAndSizeModel.getSize(), collectionSize, repository, specification);
    }

    private PageContainer<E> sortProcess(
            Class<E> entityClass,
            WebRequest request,
            int page, int size, long collectionSize,
            R repository,
            Specification<E> specification) {
        SortModel sortModel = WebRequestUtil.generateSortModel(request);
        for ( Field field : ClassLoaderUtil.getAllFieldsByEntity(entityClass) ) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (StringUtils.equalsIgnoreCase(sortModel.getSort(), field.getName())) {
                    if (StringUtils.equalsIgnoreCase(sortModel.getOrder(), "asc")) {
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
                .findAll(specification, PageRequest.of(page, size, Sort.by("id").descending())).getContent());
    }

    private PageContainer<E> getDescendingDataPageContainer(int page, int size, long collectionSize, String param, R repository, Specification<E> specification) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(specification, PageRequest.of(page, size, Sort.by(param).descending())).getContent());
    }

    private PageContainer<E> getAscendingDataPageContainer(int page, int size, long collectionSize, String param, R repository, Specification<E> specification) {
        return new PageContainer<E>(page, size, collectionSize, repository
                .findAll(specification, PageRequest.of(page, size, Sort.by(param).ascending())).getContent());
    }
}
