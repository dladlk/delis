package dk.erst.delis.sender.delis;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.sender.document.IDocumentData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DelisDocumentData implements IDocumentData, Serializable {

	private static final long serialVersionUID = -2505355023114834837L;

	private byte[] data;
	private String description;
	private SendDocument sendDocument;
	private long id;
	private long startTime;

	public DelisDocumentData(long id) {
		this.id = id;
		this.startTime = System.currentTimeMillis();
	}
	
	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(data);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("#");
		sb.append(id);
		sb.append(" ");
		sb.append(description);
		return sb.toString();
	}
}
