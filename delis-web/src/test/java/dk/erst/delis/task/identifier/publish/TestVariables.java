package dk.erst.delis.task.identifier.publish;

public interface TestVariables {

    String SMP_ENDPOINT_URL = "http://192.168.1.117:8080/smp-4.1.0";
    String SMP_ENDPOINT_USERNAME = "smp_admin";
    String SMP_ENDPOINT_PASSWORD = "changeit";

    String IDENTIFIER_TYPE = "0088";
    String IDENTIFIER_VALUE = "tbcntrl00002";

    String ORDER_DOC_IDENTIFIER = "urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biitrns001:ver2.0:extended:urn:www.peppol.eu:bis:peppol28a:ver1.0::2.1";
    String ORDER_PROCESS_IDENTIFIER = "urn:www.cenbii.eu:profile:bii28:ver2.0";

    String INVOICE1_DOC_IDENTIFIER = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1";
    String INVOICE1_PROCESS_IDENTIFIER = "urn:eu.toop.process.datarequestresponse";

    String INVOICE2_DOC_IDENTIFIER = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0::2.1";
    String INVOICE2_PROCESS_IDENTIFIER = "urn:www.cenbii.eu:profile:bii05:ver2.0";
}
