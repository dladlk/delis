package dk.erst.delis.service.document;

import dk.erst.delis.data.Document;
import dk.erst.delis.persistence.document.DocumentData;
import dk.erst.delis.rest.data.response.PageContainer;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

public interface DocumentService {

    PageContainer<DocumentData> getAll(int page, int size);

    Document getOneById(long id);
}
