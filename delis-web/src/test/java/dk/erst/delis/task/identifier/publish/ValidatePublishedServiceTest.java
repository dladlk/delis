package dk.erst.delis.task.identifier.publish;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;

import javax.sql.rowset.serial.SerialBlob;

import org.junit.Test;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.SmpEndpointConfig;
import dk.erst.delis.data.entities.access.AccessPoint;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.config.ConfigValueType;
import dk.erst.delis.data.enums.identifier.IdentifierValueType;
import dk.erst.delis.task.codelist.CodeListDict;
import dk.erst.delis.task.codelist.CodeListReaderService;
import dk.erst.delis.task.identifier.publish.ValidatePublishedService.ValidatePublishedResult;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;
import dk.erst.delis.web.accesspoint.AccessPointService;
import dk.erst.delis.web.accesspoint.AccessPointService.CertificateData;
import dk.erst.delis.web.identifier.IdentifierService;

public class ValidatePublishedServiceTest {

	private static String certificatePEM = "MIID2jCCAsKgAwIBAgIEb0y+bzANBgkqhkiG9w0BAQsFADCBhjELMAkGA1UEBhMCREsxEDAOBgNVBAgTB0Rlbm1hcmsxEzARBgNVBAcTCkNvcGVuaGFnZW4xFTATBgNVBAoTDENvbmNlcHQgVGVzdDEVMBMGA1UECxMMQ0VGIFJORCBFUlNUMSIwIAYDVQQDExlDb25jZXB0IFRlc3QgQ29tcG9uZW50IENBMB4XDTE5MDEwMTExMjIyNFoXDTIwMDEwMTExMjIyNFowgYQxCzAJBgNVBAYTAkRLMRAwDgYDVQQIEwdEZW5tYXJrMRMwEQYDVQQHEwpDb3BlbmhhZ2VuMRUwEwYDVQQKEwxDb25jZXB0IFRlc3QxFTATBgNVBAsTDENFRiBSTkQgRVJTVDEgMB4GA1UEAxMXZHluY29uY2VwdHRlc3RwYXJ0eTAxZ3cwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCVVaPbz2FcRV97AiIoDjvzhMuEB5U/tK76Js464HZNo+0vDDLVmMJ8INstyBZOOwnmiFJVjz2gviNwz3AvlHbo7itow9Sl1CUTAxtCi1LZEzdaeYFwMQzvBpe7zCboXOjzrmo0UjuHJNnyISiBz0BSkdgPE0GC+wGmIMZct/4vxHmg+SweMGukwPTr4Q/8S/gVMCak7TT/ri6doVhZZNUmn22r7ulpS7hCpHADmKieBXa22Y1leYXAi+uTKT+kx6TneXYLS0BOUU9sXdx3wHDeiYdprux3Ozfn5a6SRakjU4NY9/pUwsgPVXyKAUUOe51WCl1PJ6SdWd8ADUwMa/ctAgMBAAGjUDBOMB8GA1UdIwQYMBaAFGNMC4by8+L3rkQmyBBiz42OL3vxMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFJMw575cGRN3JyPnxloG/Cz7S7LXMA0GCSqGSIb3DQEBCwUAA4IBAQB5XOMbEy7T8yAQMfFezn2NDBmm0w4X8QbvOirc/+AdDM86fKALTSDyBUNKmTtfV02lZzRgQJmp7AyEmQbFw57xvVacQITF7XLPsY+HBKmTb3/sX4HCStvl4iAJjlH5KyLlqPJltdldAk7bQBwqXmsF0q/QSoW8rmPvctGq3IT4o++GMPAC+o2ek970mUCpU0L4wBrgka1Kb9xK7fjUl9gndGevWF1pQwpcPQBQPqe99aymR9NC4oMoweDvTDgLk/XqepPddHhY9xJ2wEi9dIKFMbJ6cjGmdl+Xuc38rfmLjiVSkma9H6ABcyUUPKfDvTuFxLYAQJw6Yysl16yAlaW2";
	
	@Test
	public void testValidatePublishedIdentifier() {
		OrganisationSetupData setupData = new OrganisationSetupData(); 
		setupData.setAs4(4L);
		setupData.setAs2(2L);
		setupData.getSubscribeProfileSet().add(OrganisationSubscriptionProfileGroup.BIS3);
		
		ConfigBean configBean = mock(ConfigBean.class);
		when(configBean.getSmpEndpointConfig()).then(d -> {
			SmpEndpointConfig smpConfig = new SmpEndpointConfig();
			smpConfig.setUrl("https://smp.edelivery-test.trueservice.dk");
			smpConfig.setFormat("PEPPOL");
			return smpConfig;
		});
		when(configBean.getStorageCodeListPath()).then(d -> {
			return Paths.get(ConfigValueType.CODE_LISTS_PATH.getDefaultValue());
		});
		
		AccessPointService accessPointService = mock(AccessPointService.class);

		AccessPoint as4 = buildAccessPoint("https://edelivery-test.trueservice.dk/domibus1/services/msh");
		AccessPoint as2 = buildAccessPoint("https://edelivery-test.trueservice.dk/oxalis1/as2");
		
		when(accessPointService.findById(any(Long.class))).then(d -> {
			Long id = (Long)(d.getArgument(0));
			if (id == 2L) {
				return as2;
			}
			return as4;
		});

		IdentifierService identifierService = mock(IdentifierService.class); 
		OrganisationSetupService organisationSetupService = mock(OrganisationSetupService.class);
		when(organisationSetupService.load(any(Organisation.class))).then(d -> {
			return setupData;
		});
		
		CodeListDict codeListDict = new CodeListDict(new CodeListReaderService(configBean));
		IdentifierPublishDataService identifierPublishDataService = new IdentifierPublishDataService(codeListDict, accessPointService);
		ValidatePublishedService s = new ValidatePublishedService(configBean, identifierPublishDataService, identifierService, organisationSetupService);
		Identifier identifier = new Identifier();
		identifier.setType(IdentifierValueType.GLN.getCode());
		identifier.setValue("5790001968533");
		Organisation organisation = new Organisation();
		organisation.setId(1L);
		
		ValidatePublishedResult resultList = new ValidatePublishedResult();
		s.validatePublishedIdentifiers(organisation, resultList);
		assertNotNull(resultList);
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
