package dk.erst.delis.xml.builder.data;

import java.util.ArrayList;

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
	private ArrayList<ResponseStatus> status;

	public ResponseStatus getStatusOrCreate(int i) {
		if (status == null) {
			status = new ArrayList<>();
		}
		int curSize = status.size();
		status.ensureCapacity(i + 1);
		for (int j = curSize; j < i + 1; j++) {
			status.add(null);
		}
		if (this.status.get(i) == null) {
			this.status.set(i, ResponseStatus.builder().build());
		}
		return this.status.get(i);
	}

	public void addStatus(ResponseStatus responseStatus) {
		if (status == null) {
			status = new ArrayList<ResponseStatus>();
		}
		status.add(responseStatus);
	}

}
