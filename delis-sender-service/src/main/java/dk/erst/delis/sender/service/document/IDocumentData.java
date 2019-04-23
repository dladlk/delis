package dk.erst.delis.sender.service.document;

import java.io.InputStream;

public interface IDocumentData {

	public InputStream getInputStream();

	public String getDescription();
}
