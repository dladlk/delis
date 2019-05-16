package dk.erst.delis.web.container;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDefs {

    private int[] targets;
    private boolean orderable;
}
