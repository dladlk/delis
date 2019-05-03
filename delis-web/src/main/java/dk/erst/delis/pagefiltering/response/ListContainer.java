package dk.erst.delis.pagefiltering.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author funtusthan, created by 19.01.19
 */

@Getter
@Setter
@AllArgsConstructor
public class ListContainer<T> {

    private List<T> items;
}
