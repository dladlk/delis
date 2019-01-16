package dk.erst.delis.persistence.document;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.persistence.AbstractSpecification;
import dk.erst.delis.persistence.AbstractSpecificationUtil;

import org.apache.commons.collections.CollectionUtils;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.criteria.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dk.erst.delis.persistence.document.DocumentConstants.*;

/**
 * @author Iehor Funtusov, created by 04.01.19
 */

public class DocumentSpecification implements AbstractSpecification<Document, DocumentFilterModel> {

    @Override
    public Specification<Document> generateCriteriaPredicate(DocumentFilterModel documentFilterModel) {

        return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> {

            String containsLikePattern;
            List<Predicate> predicates = new ArrayList<>();

            List<Field> fieldsFromEntityModel = new ArrayList<>();
            fieldsFromEntityModel.addAll(Arrays.asList(Document.class.getDeclaredFields()));
            fieldsFromEntityModel.addAll(Arrays.asList(Document.class.getSuperclass().getDeclaredFields()));

            WebRequest webRequest = documentFilterModel.getWebRequest();

            for ( Field field : fieldsFromEntityModel ) {
                if (Modifier.isPrivate(field.getModifiers())) {
                    String parameter = webRequest.getParameter(field.getName());
                    if (Objects.nonNull(parameter)) {
                        System.out.println("field name = " + field.getName());
                        System.out.println("field class = " + field.getClass());
                        System.out.println("field type = " + field.getType());
                        System.out.println("parameter = " + parameter);
                        if (field.getType().isAssignableFrom(String.class)) {
                            containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(parameter);
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field.getName())), containsLikePattern));
                        }
                        if (Enum.class.isAssignableFrom(field.getType())) {
                            predicates.add(criteriaBuilder.equal(root.get(field.getName()), Enum.valueOf((Class<Enum>) field.getType(), parameter)));
                        }
                        if (field.getType().getSuperclass().isAssignableFrom(AbstractEntity.class)) {
                            List<Predicate> innerEntitiesPredicates = new ArrayList<>();
                            List<Field> innerEntitiesFields = new ArrayList<>(Arrays.asList(field.getType().getDeclaredFields()));
                            for (Field innerField : innerEntitiesFields) {
                                if (Modifier.isPrivate(innerField.getModifiers())) {
                                    if (innerField.getType().isAssignableFrom(String.class)) {
                                        containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(parameter);
                                        innerEntitiesPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field.getName()).get(innerField.getName())), containsLikePattern));
                                    }
                                }
                            }
                            if (CollectionUtils.isNotEmpty(innerEntitiesPredicates)) {
                                predicates.add(criteriaBuilder.or(innerEntitiesPredicates.toArray(new Predicate[0])));
                            }
                        }
                    }
                }
            }

            if (Objects.nonNull(documentFilterModel.getStart()) && Objects.nonNull(documentFilterModel.getEnd())) {
                predicates.add(criteriaBuilder.between(root.get(CREATE_TIME_FIELD), documentFilterModel.getStart(), documentFilterModel.getEnd()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
