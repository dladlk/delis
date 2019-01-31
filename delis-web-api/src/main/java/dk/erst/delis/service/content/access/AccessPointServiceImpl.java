package dk.erst.delis.service.content.access;

import dk.erst.delis.data.entities.access.AccessPoint;
import dk.erst.delis.persistence.repository.access.AccessPointRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 17.01.19
 */

@Service
public class AccessPointServiceImpl implements AccessPointService {

    private final AccessPointRepository accessPointRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public AccessPointServiceImpl(AccessPointRepository accessPointRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.accessPointRepository = accessPointRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<AccessPoint> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(AccessPoint.class, webRequest, accessPointRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public AccessPoint getOneById(long id) {
        return (AccessPoint) abstractGenerateDataService.getOneById(id, AccessPoint.class, accessPointRepository);
    }
}
