package dk.erst.delis.config;

import lombok.Data;

@Data
public class SmpEndpointConfig {

	private String url = "http://smp.cef.contest.my";
	private String userName = "smp_admin";
	private String password = "changeit";

}
