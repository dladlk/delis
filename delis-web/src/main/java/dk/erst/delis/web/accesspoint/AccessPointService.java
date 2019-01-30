package dk.erst.delis.web.accesspoint;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.AccessPointDaoRepository;
import dk.erst.delis.data.entities.access.AccessPoint;
import dk.erst.delis.data.enums.access.AccessPointType;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccessPointService {
    public static final String UTF_8 = "utf-8";
    private AccessPointDaoRepository accessPointRepository;

    private static CertificateFactory certificateFactory;

    static {
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public AccessPointService(AccessPointDaoRepository accessPointRepository) {
        this.accessPointRepository = accessPointRepository;
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

    void saveAccessPoint(AccessPointData accessPointData) throws CertificateException, Exception {
        AccessPoint accessPoint;
        if (accessPointData.getId() == null) {
            accessPoint = new AccessPoint();
        } else {
            accessPoint = findById(accessPointData.getId());
        }

        accessPoint.setUrl(accessPointData.getUrl());
        accessPoint.setType(accessPointData.getType());
        // Validate certificate
        String certificateString = accessPointData.getCertificate();
        certificateString = certificateString.replaceAll("\\s+","");
        byte[] bytes = certificateString.getBytes(UTF_8);

        byte[] certBytes = Base64.getDecoder().decode(bytes);
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));
        String name = certificate.getSubjectDN().getName();
        accessPoint.setCertificateCN(name);
        accessPoint.setCertificate(new SerialBlob(bytes));
        accessPointRepository.save(accessPoint);
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

    public void copyToDTOEdit (AccessPoint accessPoint, AccessPointData accessPointData) {
        BeanUtils.copyProperties(accessPoint, accessPointData);
        try {
            java.sql.Blob certificate = accessPoint.getCertificate();
            String s = new String(certificate.getBytes(1, (int)certificate.length()));
            accessPointData.setCertificate(s);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void copyToDTOView (AccessPoint accessPoint, AccessPointData accessPointData) {
        BeanUtils.copyProperties(accessPoint, accessPointData);
        accessPointData.setCertificate(accessPoint.getCertificateCN());
    }

    //todo remove or uncomment after problem with sobmitting AccessPointData as select-option is investigated
//    todo rather delete...
//    public AccessPointData loadById (Long id) {
//        AccessPointData data = null;
//
//        AccessPoint accessPoint = findOne(id);
//        if (accessPoint != null) {
//            data = new AccessPointData();
//            copyToDTOEdit(accessPoint, data);
//        }
//        return data;
//    }
//    public List<AccessPointData> loadAccessPointsByType(AccessPointType type) {
//        List<AccessPoint> accessPointsList = accessPointRepository.findByType(type);
//        List<AccessPointData> accessPointDataList = new ArrayList<>();
//        for (AccessPoint ap : accessPointsList) { // to-do convert later to lambda
//            AccessPointData accessPointData = new AccessPointData();
//            BeanUtils.copyProperties(ap, accessPointData);
//            accessPointDataList.add(accessPointData);
//        }
//        return accessPointDataList;
//    }
}
