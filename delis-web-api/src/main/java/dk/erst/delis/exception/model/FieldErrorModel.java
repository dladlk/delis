package dk.erst.delis.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Iehor Funtusov, created by 09.01.19
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorModel {

    private String field;
    private String error;
    private String message;
}
