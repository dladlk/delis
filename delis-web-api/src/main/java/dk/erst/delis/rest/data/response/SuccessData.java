package dk.erst.delis.rest.data.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SuccessData {

    private boolean success;
    private String message;

    public SuccessData() {
        this.success = true;
    }
}
