package dk.erst.delis.service.content.dashboard;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.persistence.repository.document.SendDocumentRepository;
import dk.erst.delis.persistence.repository.identifier.IdentifierRepository;
import dk.erst.delis.persistence.repository.organization.OrganizationRepository;
import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.rest.data.response.dashboard.DashboardDocumentAdminData;
import dk.erst.delis.rest.data.response.dashboard.DashboardDocumentData;
import dk.erst.delis.rest.data.response.dashboard.DashboardDocumentUserData;
import dk.erst.delis.rest.data.response.dashboard.DashboardSendDocumentData;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.DateUtil;
import dk.erst.delis.util.SecurityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final SecurityService securityService;
    private final DocumentRepository documentRepository;
    private final SendDocumentRepository sendDocumentRepository;
    private final OrganizationRepository organizationRepository;
    private final IdentifierRepository identifierRepository;

    @Autowired
    public DashboardServiceImpl(
            SecurityService securityService,
            DocumentRepository documentRepository,
            SendDocumentRepository sendDocumentRepository,
            OrganizationRepository organizationRepository,
            IdentifierRepository identifierRepository) {
        this.securityService = securityService;
        this.documentRepository = documentRepository;
        this.sendDocumentRepository = sendDocumentRepository;
        this.organizationRepository = organizationRepository;
        this.identifierRepository = identifierRepository;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public List<DashboardDocumentData> generateDashboardDocumentDataList(WebRequest request) {
        DateRangeModel dateRange = DateUtil.generateDateRangeByLastHour();
        if (SecurityUtil.hasRole("ROLE_USER")) {
            Organisation organisation = securityService.getOrganisation();
            List<Document> documents = documentRepository.findAllByOrganisationAndCreateTimeBetweenAndLastErrorIsNull(organisation, dateRange.getStart(), dateRange.getEnd());
//            List<Document> documents = documentRepository.findAllByOrganisationAndLastErrorIsNull(organisation);
            return generateDashboardDocumentDataListByUser(documents, getLocale(request), organisation.getId());
        } else {
            List<Document> documents = documentRepository.findAllByCreateTimeBetweenAndLastErrorIsNull(dateRange.getStart(), dateRange.getEnd());
//            List<Document> documents = documentRepository.findAllByLastErrorIsNull();
            return generateDashboardDocumentDataListByAdmin(documents, getLocale(request));
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public List<DashboardDocumentData> generateDashboardDocumentErrorDataList(WebRequest request) {
        DateRangeModel dateRange = DateUtil.generateDateRangeByLastHour();
        if (SecurityUtil.hasRole("ROLE_USER")) {
            Organisation organisation = securityService.getOrganisation();
            List<Document> documents = documentRepository.findAllByOrganisationAndCreateTimeBetweenAndLastErrorIsNotNull(organisation, dateRange.getStart(), dateRange.getEnd());
//            List<Document> documents = documentRepository.findAllByOrganisationAndLastErrorIsNotNull(organisation);
            return generateDashboardDocumentDataListByUser(documents, getLocale(request), organisation.getId());
        } else {
            List<Document> documents = documentRepository.findAllByCreateTimeBetweenAndLastErrorIsNotNull(dateRange.getStart(), dateRange.getEnd());
//            List<Document> documents = documentRepository.findAllByLastErrorIsNotNull();
            return generateDashboardDocumentDataListByAdmin(documents, getLocale(request));
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public List<DashboardSendDocumentData> generateSendDashboardDocumentDataList(WebRequest request) {
        DateRangeModel dateRange = DateUtil.generateDateRangeByLastHour();
        if (SecurityUtil.hasRole("ROLE_USER")) {
            Organisation organisation = securityService.getOrganisation();
            List<SendDocument> documents = sendDocumentRepository.findAllByOrganisationAndCreateTimeBetween(organisation, dateRange.getStart(), dateRange.getEnd());
//            List<SendDocument> documents = sendDocumentRepository.findAllByOrganisation(organisation);
            return generateDashboardSendDocumentDataListByUser(organisation.getName(), documents, getLocale(request));
        } else {
            List<SendDocument> documents = sendDocumentRepository.findAllByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd());
//            List<SendDocument> documents = sendDocumentRepository.findAll();
            return generateDashboardSendDocumentDataListByAdmin(documents, getLocale(request));
        }
    }

    private List<DashboardDocumentData> generateDashboardDocumentDataListByUser(List<Document> documents, String locale, Long organisationId) {
        List<String> identifiers = identifierRepository.findDistinctNameByOrganisation(organisationId);
        List<DashboardDocumentData> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(documents)) {
            identifiers.forEach(identifier -> list.add(
                    new DashboardDocumentUserData(
                            identifier, documents.stream().filter(document -> {
                        if (Objects.nonNull(document.getReceiverIdentifier())) {
                            return StringUtils.equals(identifier, document.getReceiverIdentifier().getName());
                        } else {
                            return false;
                        }
                    }).collect(Collectors.toList()), locale)));
            return list;
        } else {
            identifiers.forEach(identifier -> list.add(new DashboardDocumentUserData(identifier)));
            return list;
        }
    }

    private List<DashboardDocumentData> generateDashboardDocumentDataListByAdmin(List<Document> documents, String locale) {
        List<String> organisations = organizationRepository.findDistinctName();
        List<DashboardDocumentData> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(documents)) {
            organisations.forEach(organisation -> list.add(
                    new DashboardDocumentAdminData(
                            organisation, documents.stream().filter(document -> {
                        if (Objects.nonNull(document.getOrganisation())) {
                            return StringUtils.equals(organisation, document.getOrganisation().getName());
                        } else {
                            return false;
                        }
                    }).collect(Collectors.toList()), locale)));
            return list;
        } else {
            organisations.forEach(organisation -> list.add(new DashboardDocumentAdminData(organisation)));
            return list;
        }
    }

    private List<DashboardSendDocumentData> generateDashboardSendDocumentDataListByUser(String organisation, List<SendDocument> documents, String locale) {
        List<DashboardSendDocumentData> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(documents)) {
            list.add(new DashboardSendDocumentData(organisation, documents, locale));
            return list;
        } else {
            list.add(new DashboardSendDocumentData(organisation));
            return list;
        }
    }

    private List<DashboardSendDocumentData> generateDashboardSendDocumentDataListByAdmin(List<SendDocument> documents, String locale) {
        List<String> organisations = organizationRepository.findDistinctName();
        if (CollectionUtils.isNotEmpty(documents)) {
            List<DashboardSendDocumentData> list = new ArrayList<>();
            organisations.forEach(organisation -> list.add(
                    new DashboardSendDocumentData(
                            organisation, documents.stream().filter(document -> {
                        if (Objects.nonNull(document.getOrganisation())) {
                            return StringUtils.equals(organisation, document.getOrganisation().getName());
                        } else {
                            return false;
                        }
                    }).collect(Collectors.toList()), locale)));
            return list;
        } else {
            return organisations.stream().map(DashboardSendDocumentData::new).collect(Collectors.toList());
        }
    }

    private String getLocale(WebRequest request) {
        String locale = request.getParameter("locale_lang");
        return (locale != null) ? locale : "da";
    }
}
