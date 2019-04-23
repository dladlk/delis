package dk.erst.delis.pagefiltering.service;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.pagefiltering.response.PageContainer;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 13.01.19
 */

public interface AbstractService<E extends AbstractEntity> {

    PageContainer<E> getAll(WebRequest request);

    E getOneById(long id);
}
