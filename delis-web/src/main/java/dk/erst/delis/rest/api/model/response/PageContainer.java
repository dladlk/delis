package dk.erst.delis.rest.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Iehor Funtusov, created by 18.12.18
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageContainer<T> {

    private int currentPage;
    private int pageSize;
    private long collectionSize;
    private List<T> items;
}
