package dk.erst.delis.web.validationrule;

import dk.erst.delis.dao.RuleDocumentValidationDaoRepository;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ValidationRuleService {
    private RuleDocumentValidationDaoRepository repository;

    @Autowired
    public ValidationRuleService(RuleDocumentValidationDaoRepository repository) {
        this.repository = repository;
    }

    public Iterable<RuleDocumentValidation> findAll() {
        Iterable<RuleDocumentValidation> all = repository.findAll();
        return all;
    }

    public List<RuleDocumentValidationData> loadRulesList() {
        Iterable<RuleDocumentValidation> list = repository.findAll();
        List<RuleDocumentValidationData> dtoList = new ArrayList<>();
        for (RuleDocumentValidation rule : list) {
            RuleDocumentValidationData ruleData = new RuleDocumentValidationData();
            BeanUtils.copyProperties(rule, ruleData);
            dtoList.add(ruleData);
        }
        return dtoList;
    }

    void saveRule(RuleDocumentValidationData ruleData) {
        RuleDocumentValidation rule;
        if (ruleData.getId() == null) {
            rule = new RuleDocumentValidation();
        } else {
            rule = findById(ruleData.getId());
        }
        BeanUtils.copyProperties(ruleData, rule);
        repository.save(rule);
    }

    public RuleDocumentValidation findById(Long id) {
        return findOne(id);
    }

    private RuleDocumentValidation findOne(Long id) {
        RuleDocumentValidation ruleDocumentValidation = repository.findById(id).orElse(null);
        if (ruleDocumentValidation != null) {
            return ruleDocumentValidation;
        } else {
            throw new RuntimeException(String.format("Validation Rule with id=%s not found", id));
        }
    }

    void deleteRule(Long id) {
        repository.delete(findOne(id));
    }

    public List<RuleDocumentValidation> createDefaultValidationRuleList() {

        List<RuleDocumentValidation> result = new ArrayList<>();
        /*
         * XSD rules
         */
        result.add(xsd(DocumentFormat.OIOUBL_INVOICE, "xsd/UBL_2.0/maindoc/UBL-Invoice-2.0.xsd"));
        result.add(xsd(DocumentFormat.OIOUBL_CREDITNOTE, "xsd/UBL_2.0/maindoc/UBL-CreditNote-2.0.xsd"));

        result.add(xsd(DocumentFormat.BIS3_INVOICE, "xsd/UBL_2.1/maindoc/UBL-Invoice-2.1.xsd"));
        result.add(xsd(DocumentFormat.BIS3_CREDITNOTE, "xsd/UBL_2.1/maindoc/UBL-CreditNote-2.1.xsd"));

        result.add(xsd(DocumentFormat.CII, "xsd/CII_D16B_SCRDM_uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd"));

        /*
         * Schematron
         */

        result.add(sch(DocumentFormat.OIOUBL_INVOICE, "sch/oioubl/OIOUBL_Schematron_2018-09-15_v1.10.0.35220/OIOUBL_Invoice_Schematron.xsl", 10));
        result.add(sch(DocumentFormat.OIOUBL_CREDITNOTE, "sch/oioubl/OIOUBL_Schematron_2018-09-15_v1.10.0.35220/OIOUBL_CreditNote_Schematron.xsl", 10));

        String BIS3_PEPPOL = "sch/bis3/peppol_2019-01-02_1/PEPPOL-EN16931-UBL.xslt";
        String BIS3_CEN = "sch/bis3/cen_2019-01-02_1/CEN-EN16931-UBL.xslt";
        result.add(sch(DocumentFormat.BIS3_INVOICE, BIS3_PEPPOL, 10));
        result.add(sch(DocumentFormat.BIS3_INVOICE, BIS3_CEN, 20));
        result.add(sch(DocumentFormat.BIS3_CREDITNOTE, BIS3_PEPPOL, 10));
        result.add(sch(DocumentFormat.BIS3_CREDITNOTE, BIS3_CEN, 20));

        result.add(sch(DocumentFormat.CII, "sch/cii/peppol_2019-01-02_1/PEPPOL-EN16931-CII.xslt", 10));
        result.add(sch(DocumentFormat.CII, "sch/cii/cen_2019-01-02_1/CEN-EN16931-CII.xslt", 20));
        return result;
    }

    private RuleDocumentValidation xsd(DocumentFormat format, String path) {
        RuleDocumentValidation v = new RuleDocumentValidation();
        v.setActive(true);
        v.setDocumentFormat(format);
        v.setPriority(10);
        v.setValidationType(RuleDocumentValidationType.XSD);
        v.setRootPath(path);
        return v;
    }

    private RuleDocumentValidation sch(DocumentFormat format, String path, int priority) {
        RuleDocumentValidation v = new RuleDocumentValidation();
        v.setActive(true);
        v.setDocumentFormat(format);
        v.setPriority(priority);
        v.setValidationType(RuleDocumentValidationType.SCHEMATRON);
        v.setRootPath(path);
        return v;
    }

    public void recreateDefault() {
        repository.deleteAll();
        List<RuleDocumentValidation> defaultValidationRuleList = createDefaultValidationRuleList();
        repository.saveAll(defaultValidationRuleList);
    }

}
