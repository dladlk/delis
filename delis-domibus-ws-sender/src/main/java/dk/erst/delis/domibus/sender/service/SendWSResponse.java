package dk.erst.delis.domibus.sender.service;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendWSResponse {
	
	private boolean success;
    private String messageId;
    private String messageStatus;
    private List<String> errorList;
    
    public void addError(String message) {
    	if (this.errorList == null) {
    		this.errorList = new ArrayList<>();
    	}
    	this.errorList.add(message);
    }
}
