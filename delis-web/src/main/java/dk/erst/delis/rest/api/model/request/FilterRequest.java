package dk.erst.delis.rest.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Iehor Funtusov, created by 19.12.18
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {

    private String field;
    private String value;
}