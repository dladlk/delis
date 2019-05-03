package dk.erst.delis.config;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmpEndpointConfig {

	private String url = "http://localhost:8090/smp-4.1.0";
	private String userName = "smp_admin";
	private String password = "changeit";
	
	/*
	 * Default - OASIS (bdxr), if PEPPOL - busdox
	 */
	private String format = "OASIS"; 

}
