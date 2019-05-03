package dk.erst.delis.pagefiltering.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Iehor Funtusov, created by 18.12.18
 */

@Getter
@Setter
@AllArgsConstructor
public class PageContainer<T> {

    private int totalPages;
    private int currentPage;
    private int pageSize;
    private long collectionSize;
    private List<T> items;

    private int paginatorSize = 10;
    private int paginatorStart;
    private int paginatorEnd;

    private String sortField = "id"; // todo modify for multifields sorting in future
    private SortingDirection sortDirection = SortingDirection.Asc;

    private Map<?, ?> filterMap = new HashMap<>();

    public PageContainer(int currentPage, int pageSize, long collectionSize, List<T> items, String sortField, SortingDirection sortDirection) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.collectionSize = collectionSize;
        this.items = items;
        this.sortField = sortField;
        this.sortDirection = sortDirection;
        int i = collectionSize % pageSize != 0 ? 1 : 0;
        this.totalPages = (int)collectionSize/pageSize + i;

        paginatorStart = (currentPage-1)/pageSize + 1;
        paginatorEnd = (paginatorStart + paginatorSize) < totalPages ? (paginatorStart + paginatorSize) : totalPages;
    }

    public PageContainer() {
        this.currentPage = 1;
        this.pageSize = 10;
        this.collectionSize = 0;
        this.items = Collections.emptyList();
    }
}
