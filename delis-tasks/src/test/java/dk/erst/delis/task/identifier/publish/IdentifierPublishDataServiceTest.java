package dk.erst.delis.task.identifier.publish;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;

import org.junit.Test;

import dk.erst.delis.data.entities.access.AccessPoint;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.task.codelist.CodeListDict;
import dk.erst.delis.task.identifier.publish.data.SmpPublishProcessData;
import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;
import dk.erst.delis.web.accesspoint.AccessPointService;
import dk.erst.delis.web.accesspoint.AccessPointService.CertificateData;

public class IdentifierPublishDataServiceTest {

	private static String certificatePEM = "MIID2jCCAsKgAwIBAgIEb0y+bzANBgkqhkiG9w0BAQsFADCBhjELMAkGA1UEBhMCREsxEDAOBgNVBAgTB0Rlbm1hcmsxEzARBgNVBAcTCkNvcGVuaGFnZW4xFTATBgNVBAoTDENvbmNlcHQgVGVzdDEVMBMGA1UECxMMQ0VGIFJORCBFUlNUMSIwIAYDVQQDExlDb25jZXB0IFRlc3QgQ29tcG9uZW50IENBMB4XDTE5MDEwMTExMjIyNFoXDTIwMDEwMTExMjIyNFowgYQxCzAJBgNVBAYTAkRLMRAwDgYDVQQIEwdEZW5tYXJrMRMwEQYDVQQHEwpDb3BlbmhhZ2VuMRUwEwYDVQQKEwxDb25jZXB0IFRlc3QxFTATBgNVBAsTDENFRiBSTkQgRVJTVDEgMB4GA1UEAxMXZHluY29uY2VwdHRlc3RwYXJ0eTAxZ3cwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCVVaPbz2FcRV97AiIoDjvzhMuEB5U/tK76Js464HZNo+0vDDLVmMJ8INstyBZOOwnmiFJVjz2gviNwz3AvlHbo7itow9Sl1CUTAxtCi1LZEzdaeYFwMQzvBpe7zCboXOjzrmo0UjuHJNnyISiBz0BSkdgPE0GC+wGmIMZct/4vxHmg+SweMGukwPTr4Q/8S/gVMCak7TT/ri6doVhZZNUmn22r7ulpS7hCpHADmKieBXa22Y1leYXAi+uTKT+kx6TneXYLS0BOUU9sXdx3wHDeiYdprux3Ozfn5a6SRakjU4NY9/pUwsgPVXyKAUUOe51WCl1PJ6SdWd8ADUwMa/ctAgMBAAGjUDBOMB8GA1UdIwQYMBaAFGNMC4by8+L3rkQmyBBiz42OL3vxMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFJMw575cGRN3JyPnxloG/Cz7S7LXMA0GCSqGSIb3DQEBCwUAA4IBAQB5XOMbEy7T8yAQMfFezn2NDBmm0w4X8QbvOirc/+AdDM86fKALTSDyBUNKmTtfV02lZzRgQJmp7AyEmQbFw57xvVacQITF7XLPsY+HBKmTb3/sX4HCStvl4iAJjlH5KyLlqPJltdldAk7bQBwqXmsF0q/QSoW8rmPvctGq3IT4o++GMPAC+o2ek970mUCpU0L4wBrgka1Kb9xK7fjUl9gndGevWF1pQwpcPQBQPqe99aymR9NC4oMoweDvTDgLk/XqepPddHhY9xJ2wEi9dIKFMbJ6cjGmdl+Xuc38rfmLjiVSkma9H6ABcyUUPKfDvTuFxLYAQJw6Yysl16yAlaW2";

	@Test
	public void testCreateServiceList() {
		AccessPointService accessPointService = mock(AccessPointService.class);
		when(accessPointService.findById(any(Long.class))).then(id -> {
			return buildAccessPoint("http://localhost:9089");
		});
		
		CodeListDict codeListDict = null;
		
		IdentifierPublishDataService s = new IdentifierPublishDataService(codeListDict, accessPointService);
		Identifier id = new Identifier();
		id.setValue("VALUE");
		id.setType("GLN");
		OrganisationSetupData setupData = new OrganisationSetupData();
		setupData.setAs4(1L);
		Set<OrganisationSubscriptionProfileGroup> profileSet = new HashSet<OrganisationSubscriptionProfileGroup>();
		profileSet.add(OrganisationSubscriptionProfileGroup.BIS3_Ordering);
		profileSet.add(OrganisationSubscriptionProfileGroup.BIS3_OrderOnly);
		setupData.setSubscribeProfileSet(profileSet);
		List<SmpPublishServiceData> serviceList = s.createServiceList(id, setupData);
		assertEquals(2, serviceList.size());
		
		Collections.sort(serviceList, (a,b) -> a.getDocumentIdentifier().getDocumentIdentifierValue().compareTo(b.getDocumentIdentifier().getDocumentIdentifierValue()));
		assertEquals("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:fdc:peppol.eu:poacc:trns:order:3::2.1", serviceList.get(0).getDocumentIdentifier().getDocumentIdentifierValue());
		assertEquals("urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2::OrderResponse##urn:fdc:peppol.eu:poacc:trns:order_response:3::2.1", serviceList.get(1).getDocumentIdentifier().getDocumentIdentifierValue());
		
		List<SmpPublishProcessData> processList = serviceList.get(0).getProcessList();
		assertEquals(2, processList.size());
		
		Collections.sort(processList, (a,b) -> a.getProcessIdentifier().getProcessIdentifierValue().compareTo(b.getProcessIdentifier().getProcessIdentifierValue()));
		
		assertEquals("urn:fdc:peppol.eu:poacc:bis:order_only:3", processList.get(0).getProcessIdentifier().getProcessIdentifierValue());
		assertEquals("urn:fdc:peppol.eu:poacc:bis:ordering:3", processList.get(1).getProcessIdentifier().getProcessIdentifierValue());
		
	}
	
	private AccessPoint buildAccessPoint(String url) {
		AccessPoint ap = new AccessPoint();
		ap.setUrl(url);
		CertificateData certificateData = AccessPointService.parseCertificateDataByPEM(certificatePEM);
		ap.setCertificateCN(certificateData.getCertififcateName());
		try {
			ap.setCertificate(new SerialBlob(certificateData.getCertificateBytes()));
		} catch (Exception e) {
		}
		return ap;
	}

}
