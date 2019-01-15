package dk.erst.delis.service;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.persistence.AbstractFilterModel;
import dk.erst.delis.persistence.AbstractRepository;
import dk.erst.delis.persistence.AbstractSpecification;
import dk.erst.delis.rest.data.response.PageContainer;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 13.01.19
 */

public interface AbstractGenerateDataService<
        R extends AbstractRepository,
        E extends AbstractEntity,
        S extends AbstractSpecification,
        F extends AbstractFilterModel> {

    PageContainer<E> sortProcess(Class<E> entityClass, String sort, WebRequest request, int page, int size, long collectionSize, R repository, F filterModel, S specification);
    PageContainer<E> getDefaultDataPageContainer(int page, int size, long collectionSize, R repository);
    PageContainer<E> getDefaultDataPageContainerWithoutSorting(int page, int size, long collectionSize, R repository, F filterModel, S specification);
}
