package dk.erst.delis.web.accesspoint;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.AccessPointDaoRepository;
import dk.erst.delis.dao.OrganisationSetupDaoRepository;
import dk.erst.delis.data.entities.access.AccessPoint;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.OrganisationSetup;
import dk.erst.delis.data.enums.access.AccessPointType;
import dk.erst.delis.data.enums.organisation.OrganisationSetupKey;
import dk.erst.delis.task.organisation.setup.ValidationResultData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccessPointService {

	private AccessPointDaoRepository accessPointRepository;

	private OrganisationSetupDaoRepository organisationSetupDaoRepository;

	private static CertificateFactory certificateFactory;

	static {
		try {
			certificateFactory = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			e.printStackTrace();
		}
	}

	@Autowired
	public AccessPointService(AccessPointDaoRepository accessPointRepository, OrganisationSetupDaoRepository organisationSetupDaoRepository) {
		this.accessPointRepository = accessPointRepository;
		this.organisationSetupDaoRepository = organisationSetupDaoRepository;
	}

	public Iterable<AccessPoint> getAccessPoints() {
		return accessPointRepository.findAll();
	}

	public List<AccessPointData> getAccessPointDTOs() {
		Iterable<AccessPoint> accessPointsList = accessPointRepository.findAll();
		List<AccessPointData> accessPointDataList = new ArrayList<>();
		for (AccessPoint ap : accessPointsList) { // to-do convert later to lambda
			AccessPointData accessPointData = new AccessPointData();
			copyToDTOView(ap, accessPointData);
			accessPointDataList.add(accessPointData);
		}
		return accessPointDataList;
	}

	public List<AccessPoint> findAccessPointsByType(AccessPointType type) {
		List<AccessPoint> accessPointsList = accessPointRepository.findByType(type);
		return accessPointsList;
	}

	public ValidationResultData validate(AccessPointData data) {
		ValidationResultData res = new ValidationResultData();

		if (StringUtils.isBlank(data.getUrl())) {
			res.addError("url", "URL is mandatory");
		} else {
			if (data.getUrl().length() > 250) {
				res.addError("url", "URL maximum length is 250 symbols");
			} else {
				try {
					new URL(data.getUrl());
				} catch (Exception e) {
					res.addError("url", "Given value is not a valid URL");
				}
			}
		}

		if (StringUtils.isBlank(data.getServiceDescription())) {
			res.addError("serviceDescription", "Service description is mandatory");
		} else {
			if (data.getServiceDescription().length() > 250) {
				res.addError("serviceDescription", "Service description maximum length is 250 symbols");
			}
		}

		if (StringUtils.isBlank(data.getTechnicalContactUrl())) {
			res.addError("technicalContactUrl", "Technical contact URL is mandatory");
		} else {
			if (data.getTechnicalContactUrl().length() > 250) {
				res.addError("technicalContactUrl", "Technical contact URL maximum length is 250 symbols");
			}
		}

		if (StringUtils.isBlank(data.getCertificate())) {
			res.addError("certificate", "Certificate is mandatory");
		} else {
			if (parseCertificateDataByPEM(data.getCertificate()) == null) {
				res.addError("certificate", "PEM data are not a valid certificate");
			}
		}

		if (data.getId() != null && data.getId() > 0) {
			AccessPoint current = findById(data.getId());
			if (current.getType() != data.getType()) {
				List<Organisation> referencedAccessPointList = getOrganisationReferencedAccessPointList(data.getId());
				if (referencedAccessPointList != null && !referencedAccessPointList.isEmpty()) {
					res.addError("type", "Type of access point cannot be changed, as this access point is used in setup of " + referencedAccessPointList.size() + " organisation(s).");
				}
			}
		}

		return res;
	}

	void saveAccessPoint(AccessPointData accessPointData) throws Exception {
		AccessPoint accessPoint;
		if (accessPointData.getId() == null) {
			accessPoint = new AccessPoint();
		} else {
			accessPoint = findById(accessPointData.getId());
		}

		accessPoint.setUrl(accessPointData.getUrl());
		accessPoint.setType(accessPointData.getType());
		accessPoint.setServiceDescription(accessPointData.getServiceDescription());
		accessPoint.setTechnicalContactUrl(accessPointData.getTechnicalContactUrl());

		CertificateData data = parseCertificateDataByPEM(accessPointData.getCertificate());
		if (data != null) {
			accessPoint.setCertificateCN(data.certififcateName);
			accessPoint.setCertificate(new SerialBlob(data.certificateBytes));
			accessPointRepository.save(accessPoint);
		}
	}

	@Getter
	public static class CertificateData {
		byte[] certificateBytes;
		String certififcateName;
		Date expirationDate;
	}

	public static CertificateData parseCertificateDataByPEM(String certificateString) {
		try {
			certificateString = certificateString.replaceAll("\\s+", "");
			byte[] bytes = certificateString.getBytes(StandardCharsets.UTF_8);
			byte[] certBytes = Base64.getDecoder().decode(bytes);
			CertificateData cd = parseCertificateData(certBytes);
			cd.certificateBytes = bytes;
			return cd;
		} catch (Exception e) {
			return null;
		}
	}

	private static CertificateData parseCertificateData(byte[] certBytes) throws CertificateException {
		CertificateData res = new CertificateData();
		X509Certificate certificate;
		certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));
		String name = certificate.getSubjectDN().getName();

		res.certififcateName = name;
		res.expirationDate = certificate.getNotAfter();
		return res;
	}

	public AccessPoint findById(Long id) {
		return findOne(id);
	}

	private AccessPoint findOne(Long id) {
		AccessPoint accessPoint = accessPointRepository.findById(id).orElse(null);
		if (accessPoint != null) {
			return accessPoint;
		} else {
			throw new RuntimeException(String.format("AccessPoint with id=%s not found", id));
		}
	}

	void deleteAccessPoint(Long id) {
		accessPointRepository.delete(findOne(id));
	}

	public void copyToDTOEdit(AccessPoint accessPoint, AccessPointData accessPointData) {
		BeanUtils.copyProperties(accessPoint, accessPointData);
		try {
			java.sql.Blob certificate = accessPoint.getCertificate();
			String s = new String(certificate.getBytes(1, (int) certificate.length()));
			accessPointData.setCertificate(s);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void copyToDTOView(AccessPoint accessPoint, AccessPointData accessPointData) {
		BeanUtils.copyProperties(accessPoint, accessPointData);
		accessPointData.setCertificate(accessPoint.getCertificateCN());
		try {
			CertificateData cd = parseCertificateData(accessPoint.decodeCertificateToBytes());
			accessPointData.setCertificateExpirationDate(cd.getExpirationDate());
		} catch (Exception e) {
			log.error("Failed to decode certificate for acess point "+accessPoint);
		}
	}

	public List<Organisation> getOrganisationReferencedAccessPointList(long id) {
		AccessPoint accessPoint = accessPointRepository.findById(id).orElse(null);
		Set<Organisation> orgSet = new HashSet<Organisation>();
		if (accessPoint != null) {
			List<OrganisationSetup> list;
			list = organisationSetupDaoRepository.findAllByKeyAndValue(OrganisationSetupKey.ACCESS_POINT_AS2, String.valueOf(accessPoint.getId()));
			collectOrganisations(orgSet, list);
			list = organisationSetupDaoRepository.findAllByKeyAndValue(OrganisationSetupKey.ACCESS_POINT_AS4, String.valueOf(accessPoint.getId()));
			collectOrganisations(orgSet, list);
		}
		ArrayList<Organisation> res = new ArrayList<Organisation>(orgSet);
		Collections.sort(res, new Comparator<Organisation>() {
			@Override
			public int compare(Organisation o1, Organisation o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return res;
	}

	private void collectOrganisations(Set<Organisation> orgSet, List<OrganisationSetup> list) {
		if (list != null) {
			for (OrganisationSetup s : list) {
				orgSet.add(s.getOrganisation());
			}
		}
	}

}
