package dk.erst.delis.sender.service.document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentData implements IDocumentData, Serializable {

	private static final long serialVersionUID = -2505355023114834837L;

	private byte[] data;
	private String description;

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(data);
	}

}
