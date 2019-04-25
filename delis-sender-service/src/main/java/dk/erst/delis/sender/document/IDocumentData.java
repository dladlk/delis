package dk.erst.delis.sender.document;

import java.io.InputStream;

public interface IDocumentData {

	public long getId();
	
	public InputStream getInputStream();

	public String getDescription();
	
	public long getStartTime();
}
