package dk.erst.delis.web.validationrule;

import dk.erst.delis.dao.RuleDocumentValidationDaoRepository;
import dk.erst.delis.data.RuleDocumentValidation;
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
}
