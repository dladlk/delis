package dk.erst.delis.web.container;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PageDataContainer implements Serializable {

    private static final long serialVersionUID = 84982870159183974L;

    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
}
