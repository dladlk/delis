package dk.erst.delis.web.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class UserData {

    private Long id;
	
    @NotBlank
    @Size(min = 2, max = 50)
    private String username;
	
    private String password;
    
    private String password2;

    @Size(min = 1, max = 50)
    private String firstName;
    @Size(min = 1, max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 50)
    private String email;

    private String organisationCode;
    
    private boolean disabledIrForm;

    public boolean isNew() {
    	return this.id == null;
    }
}
