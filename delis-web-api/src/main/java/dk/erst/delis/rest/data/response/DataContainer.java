package dk.erst.delis.rest.data.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DataContainer<T> {

    private T data;
}