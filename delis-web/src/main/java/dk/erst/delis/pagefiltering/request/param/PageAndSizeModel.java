package dk.erst.delis.pagefiltering.request.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author funtusthan, created by 14.01.19
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageAndSizeModel {

    int page;
    int size;
}
