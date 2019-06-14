package dk.erst.delis.rest.data.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessData {

    private boolean success;

    public SuccessData() {
        this.success = true;
    }
}
