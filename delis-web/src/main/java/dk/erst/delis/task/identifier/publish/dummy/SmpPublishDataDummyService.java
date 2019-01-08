package dk.erst.delis.task.identifier.publish.dummy;

import dk.erst.delis.task.identifier.publish.data.SmpDocumentIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpProcessIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import dk.erst.delis.task.identifier.publish.data.SmpServiceEndpointData;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class SmpPublishDataDummyService {

	@Data
	private static class TempDocumentProcess {
		private final String documentIdentifier;
		private final String procesIdentifier;
	}

	private static TempDocumentProcess INVOICE_PEPPOL_28 = new TempDocumentProcess(
			"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1",
			"urn:eu.toop.process.datarequestresponse");
	private static TempDocumentProcess INVOICE_PEPPOL_5A = new TempDocumentProcess(
			"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0::2.1",
			"urn:www.cenbii.eu:profile:bii05:ver2.0");
	private static TempDocumentProcess ORDER = new TempDocumentProcess(
			"urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biitrns001:ver2.0:extended:urn:www.peppol.eu:bis:peppol28a:ver1.0::2.1",
			"urn:www.cenbii.eu:profile:bii28:ver2.0");

	private static TempDocumentProcess[] TEMP_DOCUMENT_LIST = new TempDocumentProcess[] { ORDER, INVOICE_PEPPOL_28, INVOICE_PEPPOL_5A };

	private static String TRANSPORT_AS2 = "peppol-transport-as4-v2_0";
	private static String TRANSPORT_AS4 = "busdox-transport-as2-ver1p0";

	private ArrayList<SmpServiceEndpointData> createEndpoints() {
		ArrayList<SmpServiceEndpointData> endpoints = new ArrayList<>();
		endpoints.add(buildServiceEndpointData(TRANSPORT_AS4, "https://edelivery2.trueservice.dk/domibus/msh"));
		endpoints.add(buildServiceEndpointData(TRANSPORT_AS2, "https://edelivery2.trueservice.dk/oxalis/as2"));
		return endpoints;
	}

	private SmpServiceEndpointData buildServiceEndpointData(String transportProfile, String url) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date serviceActivationDate = null;
		Date serviceExpirationDate = null;
		try {
			serviceActivationDate = simpleDateFormat.parse("2018-07-01");
			serviceExpirationDate = simpleDateFormat.parse("2020-07-01");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		SmpServiceEndpointData r = new SmpServiceEndpointData();
		r.setTransportProfile(transportProfile);
		r.setUrl(url);
		r.setServiceDescription("Test service description");
		r.setTechnicalContactUrl("http://example.com");
		r.setServiceActivationDate(serviceActivationDate);
		r.setServiceExpirationDate(serviceExpirationDate);
		r.setRequireBusinessLevelSignature(true);
		r.setCertificate(Base64.getDecoder().decode("MIIEwjCCAqqgAwIBAgIEeUiPHTANBgkqhkiG9w0BAQsFADCBhjELMAkGA1UEBhMC" +
				"REsxEDAOBgNVBAgTB0Rlbm1hcmsxEzARBgNVBAcTCkNvcGVuaGFnZW4xFTATBgNV" +
				"BAoTDENvbmNlcHQgVGVzdDEVMBMGA1UECxMMQ0VGIFJORCBFUlNUMSIwIAYDVQQD" +
				"ExlDb25jZXB0IFRlc3QgQ29tcG9uZW50IENBMB4XDTE4MDcwMTEzMzIyOFoXDTE5" +
				"MDcwMTEzMzIyOFowezELMAkGA1UEBhMCREsxEDAOBgNVBAgTB0Rlbm1hcmsxEzAR" +
				"BgNVBAcTCkNvcGVuaGFnZW4xFTATBgNVBAoTDENvbmNlcHQgVGVzdDEVMBMGA1UE" +
				"CxMMQ0VGIFJORCBFUlNUMRcwFQYDVQQDDA5zbXBfY29uY2VwdF8wMTCCASIwDQYJ" +
				"KoZIhvcNAQEBBQADggEPADCCAQoCggEBAKmxzzTNpC3sd9v9e/jjbfv9A1U/B94/" +
				"eXZQ44dNlow36FZ7JmeexUsoRm6M1S/Sworg8gGORes9mhXVoS4YOZOGBc4+LWzD" +
				"elUVGGeM5bXrvO2XykoAuNcuQO2/AhzW8/IWK2czb87HQN9MPORU6O0zh3WEah4C" +
				"G17z/qeLhIKK333ISjB3IXmfOgJC7DMNDwuRQAywrN40Cry8lL3hZ4yQuzMbbkrr" +
				"G/K1GRMlL7HAB+cTjJ1jiuZA0T3+4fTGw+y859YYC8s9nsi5g4w+VQanHWsUQuJT" +
				"zj7yPbAiw2yP0Y4J9Fa5VeF2pnYZ7lWjLUvPJYAblB1rR6PcHv5fxC0CAwEAAaNC" +
				"MEAwHwYDVR0jBBgwFoAUhfAShfnqS/MYxfwXHHUyYw2fY+wwHQYDVR0OBBYEFD5K" +
				"wg4Oc6lDwuI56JeFK546D6vhMA0GCSqGSIb3DQEBCwUAA4ICAQA5lUS/Xym0O16a" +
				"AQQV3dW4QXZBw7eSfiVSg699Oty4gxTyoSZZ8WXAnaM+HJ6+Jr6wyvN8tsyt48T2" +
				"7Z3jPcaQlC0UsZFDpipqlFQ4N8B0UrRLBaSiebj2klul/6QKTo25qGYQcBnTOQIe" +
				"irm6+P1o8E2AlZnbUjI9S8XhKPcPoZVruQgVATlkKCboBl5KC/WqV2p6KDjarZnz" +
				"B3ReqPbR5yztC0Ki0idGYxU6m5Mr4KNk4NPwc2Q78VMPId9uAqc7WT9WUch3BFQZ" +
				"+ElAu8iWDlNMr5564e9Zk/tRx7jOL0jApyPE/OMtLi1n3dKMzXrZWFmu2l0xVZo8" +
				"Qx47cpzPnMi54/VVL4UgyMvqD5mO9CC2dTOyz9durtrJku2N8cFTwngk/jSC+q2P" +
				"ftHSQhEpQyDbc+LeKH0GB4XKaW2d4+cvXnIlVydF1a+Q2OjqiBHYi+jQlSFKmTPW" +
				"9jilpUWh34l5vK9LtTAPD2LN+vsi7ogo4e41xnOkMgzIIQXE+2OZXLdFJKVa4gom" +
				"IOuf/ucRmF+uFg74NU2jQtI36BT1Ghg8V/S0by+JJLWjKej5RJR0bBaTcDy8uRJ5" +
				"PyoFGSS5KmL1iuQpOyRvQanmxEgaLOkvRUZijjbXJFFlPgmdkuzFw96AqtfJtFgo" +
				"pFyLm4JrBiexzr4qt7ebxHEVXqgD/g=="));
		return r;
	}

	public List<SmpPublishServiceData> buildDummyServiceList() {
		List<SmpPublishServiceData> serviceList = new ArrayList<>();

		List<SmpServiceEndpointData> endpointList = createEndpoints();
		for (TempDocumentProcess tdp : TEMP_DOCUMENT_LIST) {
			SmpPublishServiceData sd = new SmpPublishServiceData();
			sd.setDocumentIdentifier(SmpDocumentIdentifier.of(tdp.getDocumentIdentifier()));
			sd.setProcessIdentifier(SmpProcessIdentifier.of(tdp.getProcesIdentifier()));
			sd.setEndpoints(endpointList);
			serviceList.add(sd);
		}

		return serviceList;
	}
}
