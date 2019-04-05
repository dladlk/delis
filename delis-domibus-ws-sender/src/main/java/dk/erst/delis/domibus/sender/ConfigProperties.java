package dk.erst.delis.domibus.sender;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

@ConfigurationProperties("config")
public class ConfigProperties {

	private String wsdlUrl = "https://edelivery-test.trueservice.dk/domibus1/services/backend?wsdl";
	private boolean wsForceHttps = true;
	private boolean wsUseAuth = true;
	private String wsLogin = "pluginuser";
	private String wsPassword = "Plugin_user1";
	private boolean wsDumpHttp = false;

	private String wsSendParty = "dynconcepttestparty01gw";

	private int resultMaxWaitMs = 10000;
	private int resultCheckIntervalMs = 500;

}
