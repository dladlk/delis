package dk.erst.delis.document.sbdh.cii;

import com.google.common.collect.ImmutableMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.Map;

public class CIINapeSpaceResolver implements NamespaceContext {

    private static final Map<String, String> NAMESPACE_MAP = ImmutableMap.<String, String>builder()
            .put("xsi", "http://www.w3.org/2001/XMLSchema-instance")
            .put("rsm", "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100")
            .put("ram", "urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100")
            .put("udt", "urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100")
            .put("qdt", "urn:un:unece:uncefact:data:standard:QualifiedDataType:100")
            .build();

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("No prefix provided!");
        }
        String uri = NAMESPACE_MAP.get(prefix);
        if (uri == null) {
            return XMLConstants.NULL_NS_URI;
        } else {
            return uri;
        }
    }

    @Override
    public String getPrefix(String namespaceURI) {
        // Not needed in this context.
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        // Not needed in this context.
        return null;
    }
}
