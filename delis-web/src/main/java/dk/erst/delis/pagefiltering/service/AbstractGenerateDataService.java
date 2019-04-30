package dk.erst.delis.pagefiltering.service;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.pagefiltering.persistence.AbstractRepository;
import dk.erst.delis.pagefiltering.response.PageContainer;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 13.01.19
 */

public interface AbstractGenerateDataService<
        R extends AbstractRepository,
        E extends AbstractEntity> {

    PageContainer<E> generateDataPageContainer(Class<E> entityClass, WebRequest request, R repository);

    E getOneById(long id, Class<E> entityClass, R repository);
}
