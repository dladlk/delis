package dk.erst.delis.web.transformationrule;

import dk.erst.delis.dao.RuleDocumentTransformationDaoRepository;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static dk.erst.delis.data.enums.document.DocumentFormatFamily.BIS3;
import static dk.erst.delis.data.enums.document.DocumentFormatFamily.CII;
import static dk.erst.delis.data.enums.document.DocumentFormatFamily.OIOUBL;

@Service
public class TransformationRuleService {
    private RuleDocumentTransformationDaoRepository repository;

    @Autowired
    public TransformationRuleService(RuleDocumentTransformationDaoRepository repository) {
        this.repository = repository;
    }

    public Iterable<RuleDocumentTransformation> findAll () {
        return repository.findAll();
    }

    public List<RuleDocumentTransformationData> loadRulesList() {
        Iterable<RuleDocumentTransformation> list = repository.findAll();
        List<RuleDocumentTransformationData> dtoList = new ArrayList<>();
        for (RuleDocumentTransformation rule : list) {
            RuleDocumentTransformationData ruleData = new RuleDocumentTransformationData();
            BeanUtils.copyProperties(rule, ruleData);
            dtoList.add(ruleData);
        }
        return dtoList;
    }

    void saveRule(RuleDocumentTransformationData ruleData) {
        RuleDocumentTransformation rule;
        if (ruleData.getId() == null) {
            rule = new RuleDocumentTransformation();
        } else {
            rule = findById(ruleData.getId());
        }
        BeanUtils.copyProperties(ruleData, rule);
        repository.save(rule);
    }

    public RuleDocumentTransformation findById(Long id) {
        return findOne(id);
    }

    private RuleDocumentTransformation findOne(Long id) {
        RuleDocumentTransformation ruleDocumentTransformation = repository.findById(id).orElse(null);
        if (ruleDocumentTransformation != null) {
            return ruleDocumentTransformation;
        } else {
            throw new RuntimeException(String.format("Transformation Rule with id=%s not found", id));
        }
    }

    void deleteRule(Long id) {
        repository.delete(findOne(id));
    }

    private List<RuleDocumentTransformation> createDefaultTransformationRuleList() {
        ArrayList<RuleDocumentTransformation> result = new ArrayList<>();
        result.add(b(CII, BIS3, "cii_to_bis3/v_2018-12-22_DLK_Change_AddressLine_PayableRoundingAmount/CII_2_BIS-Billing.xslt"));
        result.add(b(BIS3, OIOUBL, "bis3_to_oioubl/v_2018-03-14_34841/BIS-Billing_2_OIOUBL_MASTER.xslt"));
        return result;
    }

    private RuleDocumentTransformation b(DocumentFormatFamily from, DocumentFormatFamily to, String path) {
        RuleDocumentTransformation r = new RuleDocumentTransformation();

        r.setActive(true);
        r.setDocumentFormatFamilyFrom(from);
        r.setDocumentFormatFamilyTo(to);
        r.setRootPath(path);

        return r;
    }

    public void recreateDefault() {
        repository.deleteAll();
        List<RuleDocumentTransformation> defaultTransformationRuleList = createDefaultTransformationRuleList();
        repository.saveAll(defaultTransformationRuleList);

    }
}
