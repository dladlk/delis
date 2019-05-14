package dk.erst.delis.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PageContainer<T> {

    private int currentPage;
    private int totalPages;
    private int pageSize;
    private long collectionSize;
    private List<T> items;

    public PageContainer() {
        this.currentPage = 1;
        this.totalPages = 1;
        this.pageSize = 10;
        this.collectionSize = 0;
        this.items = Collections.emptyList();
    }
}
