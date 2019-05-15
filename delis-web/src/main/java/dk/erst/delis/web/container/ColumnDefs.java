package dk.erst.delis.web.container;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ColumnDefs {

    private int[] targets;
    private boolean orderable;
}
