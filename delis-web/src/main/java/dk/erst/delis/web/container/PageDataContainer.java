package dk.erst.delis.web.container;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageDataContainer implements Serializable {

    private static final long serialVersionUID = 84982870159183974L;

    private int page = 1;
    private int size = 10;
    private int totalPages = 1;
    private long totalElements = 1;
    private int orderCol = 1;
    private String orderDir = "desc";
    private ColumnDefs columnDefs;
}
