package dk.erst.delis.pagefiltering.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author funtusthan, created by 12.01.19
 */

@Getter
@Setter
@AllArgsConstructor
public class DataContainer<T> {

    private T data;
}