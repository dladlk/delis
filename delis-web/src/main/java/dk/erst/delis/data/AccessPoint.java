package dk.erst.delis.data;

import lombok.Data;

@Data
public class AccessPoint {

	private Long id;
	private String url;
	private AccessPointType type;
}
