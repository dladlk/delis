package dk.erst.delis.web.accesspoint;

import dk.erst.delis.dao.AccessPointDaoRepository;
import dk.erst.delis.data.AccessPoint;
import dk.erst.delis.data.AccessPointType;
import dk.erst.delis.data.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessPointService {
    private AccessPointDaoRepository accessPointRepository;

    @Autowired
    public AccessPointService(AccessPointDaoRepository accessPointRepository) {
        this.accessPointRepository = accessPointRepository;
    }

    public Iterable<AccessPoint> getAccessPoints() {
        return accessPointRepository.findAll();
    }

    void saveAccessPoint(AccessPointData accessPointData) {
        AccessPoint accessPoint;
        if (accessPointData.getId() == null) {
            accessPoint = new AccessPoint();
        } else {
            accessPoint = findById(accessPointData.getId());
        }

        accessPoint.setUrl(accessPointData.getUrl());
        accessPoint.setType(accessPointData.getType());
        accessPoint.setCertificate(accessPointData.getCertificate());
        accessPointRepository.save(accessPoint);
    }

    AccessPoint findByUrl(String url) {
        return accessPointRepository.findByUrl(url);
    }

    AccessPoint findById(Long id) {
        return findOne(id);
    }

    private AccessPoint findOne(Long id) {
        AccessPoint accessPoint = accessPointRepository.findById(id).orElse(null);
        if (accessPoint != null) {
            return accessPoint;
        } else {
            throw new RuntimeException();
        }
    }

    void deleteAccessPoint(Long id) {
        accessPointRepository.delete(findOne(id));
    }
}
