package dk.erst.delis.oxalis.sender.response;

import java.security.cert.X509Certificate;

import lombok.Getter;
import no.difi.oxalis.api.model.TransmissionIdentifier;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import no.difi.oxalis.api.timestamp.Timestamp;
import no.difi.oxalis.as4.inbound.As4EnvelopeHeader;
import no.difi.oxalis.as4.inbound.As4InboundMetadata;
import no.difi.vefa.peppol.common.model.Digest;
import no.difi.vefa.peppol.common.model.Endpoint;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.common.model.TransportProfile;

@Getter
public class DelisResponse extends As4InboundMetadata {

	private Endpoint endpoint;

	private DelisResponse(TransmissionIdentifier transmissionIdentifier, String conversationId, Header header, Timestamp timestamp, TransportProfile transportProfile, Digest digest, X509Certificate certificate, byte[] primaryReceipt, As4EnvelopeHeader as4EnvelopeHeader) {
		super(transmissionIdentifier, conversationId, header, timestamp, transportProfile, digest, certificate, primaryReceipt, as4EnvelopeHeader);
	}

	public static DelisResponse of(TransmissionResponse tr) {
		DelisResponse dr = new DelisResponse(tr.getTransmissionIdentifier(), "", tr.getHeader(), new Timestamp(tr.getTimestamp(), tr.primaryReceipt()), tr.getProtocol(), tr.getDigest(), null, tr.primaryReceipt().getValue(), null);
		dr.endpoint = tr.getEndpoint();
		return dr;
	}
}
