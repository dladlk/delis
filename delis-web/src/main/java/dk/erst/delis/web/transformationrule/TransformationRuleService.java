package dk.erst.delis.web.transformationrule;

import dk.erst.delis.dao.RuleDocumentTransformationDaoRepository;
import dk.erst.delis.data.RuleDocumentTransformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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
}
