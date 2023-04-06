package dk.erst.delis.oxalis.sender.response;

import java.security.cert.X509Certificate;

import lombok.Getter;
import network.oxalis.api.model.TransmissionIdentifier;
import network.oxalis.api.outbound.TransmissionResponse;
import network.oxalis.api.timestamp.Timestamp;
import network.oxalis.as4.inbound.As4EnvelopeHeader;
import network.oxalis.as4.inbound.As4InboundMetadata;
import network.oxalis.vefa.peppol.common.model.Digest;
import network.oxalis.vefa.peppol.common.model.Endpoint;
import network.oxalis.vefa.peppol.common.model.Header;
import network.oxalis.vefa.peppol.common.model.TransportProfile;

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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.endpoint != null) {
			sb.append("endpoint ");
			sb.append(this.endpoint.getTransportProfile());
			sb.append(", url=");
			sb.append(this.endpoint.getAddress());
		}
		return sb.toString();
	}
}
