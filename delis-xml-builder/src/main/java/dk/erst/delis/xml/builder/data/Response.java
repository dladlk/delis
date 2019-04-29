package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Response {

	private String responseCode;
	private String responseCodeListId;
	private String responseDescription;
	private String effectiveDate;
	private ResponseStatus[] status;

	public ResponseStatus getStatusOrCreate(int i) {
		if (status == null) {
			status = new ResponseStatus[i + 1];
		}
		if (status.length <= i) {
			ResponseStatus[] statusNew = new ResponseStatus[i + 1];
			System.arraycopy(this.status, 0, statusNew, 0, this.status.length);
			this.status = statusNew;
		}
		if (this.status[i] == null) {
			this.status[i] = ResponseStatus.builder().build();
		}
		return this.status[i];
	}

}
