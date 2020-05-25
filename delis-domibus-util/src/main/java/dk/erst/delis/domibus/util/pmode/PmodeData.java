package dk.erst.delis.domibus.util.pmode;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PmodeData {

	private String partyName;
	private String endpointUrl;
	private List<PmodeData.Service> serviceList = new ArrayList<>();
	private List<PmodeData.Action> actionList = new ArrayList<>();
	private List<PmodeData.Leg> legList = new ArrayList<>();

	@Getter
	@Setter
	@AllArgsConstructor
	public static class Service {
		private String name;
		private String value;
		private String type;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class Action {
		private String name;
		private String value;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class Leg {
		private String name;
		private String service;
		private String action;
	}
}
