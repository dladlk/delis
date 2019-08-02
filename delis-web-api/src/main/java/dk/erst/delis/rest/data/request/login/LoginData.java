package dk.erst.delis.rest.data.request.login;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class LoginData {

    @NotEmpty(message = "username can not be empty")
    private String login;

    @NotEmpty(message = "password can not be empty")
    private String password;
}
