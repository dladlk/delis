package dk.erst.delis.web.accesspoint;

import dk.erst.delis.dao.AccessPointDaoRepository;
import dk.erst.delis.data.AccessPoint;
import dk.erst.delis.data.AccessPointType;
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

    public List<AccessPoint> findAccessPointsByType(AccessPointType type) {
        List<AccessPoint> accessPointsList = accessPointRepository.findByType(type);
        return accessPointsList;
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

    public AccessPoint findById(Long id) {
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

//todo remove or uncomment after problem with sobmitting AccessPointData as select-option is investigated
//    todo rather delete...
//    public AccessPointData loadById (Long id) {
//        AccessPointData data = null;
//
//        AccessPoint accessPoint = findOne(id);
//        if (accessPoint != null) {
//            data = new AccessPointData();
//            BeanUtils.copyProperties(accessPoint, data);
//        }
//        return data;
//    }
//
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
