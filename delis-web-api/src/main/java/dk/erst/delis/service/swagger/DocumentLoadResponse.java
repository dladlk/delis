package dk.erst.delis.service.swagger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author funtusthan, created by 12.03.19
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentLoadResponse {

    @JsonIgnore private int status;
    private String message;
}
