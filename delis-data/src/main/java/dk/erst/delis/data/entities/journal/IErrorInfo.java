package dk.erst.delis.data.entities.journal;

public interface IErrorInfo {

	String getCode();
	void setCode(String code);

	String getFlag();
	void setFlag(String flag);
	
	String getMessage();
	void setMessage(String message);

	String getLocation();
	void setLocation(String location);

}
