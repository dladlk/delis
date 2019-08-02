package dk.erst.delis.service.content;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.rest.data.response.PageContainer;

import org.springframework.web.context.request.WebRequest;

public interface AbstractService<E extends AbstractEntity> {

    PageContainer<E> getAll(WebRequest request);

    E getOneById(long id);
}
