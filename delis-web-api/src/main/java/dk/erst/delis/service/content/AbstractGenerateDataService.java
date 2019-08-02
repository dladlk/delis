package dk.erst.delis.service.content;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.persistence.AbstractRepository;
import dk.erst.delis.rest.data.response.PageContainer;

import org.springframework.web.context.request.WebRequest;

public interface AbstractGenerateDataService<R extends AbstractRepository, E extends AbstractEntity> {

    PageContainer<E> generateDataPageContainer(Class<E> entityClass, WebRequest request, R repository);
}
