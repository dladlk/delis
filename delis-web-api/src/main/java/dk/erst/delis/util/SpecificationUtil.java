package dk.erst.delis.util;

import dk.erst.delis.data.entities.AbstractCreateEntity;
import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;
import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.data.entities.journal.JournalOrganisation;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.rest.data.request.param.DateRangeModel;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class SpecificationUtil {

    public List<Predicate> generateSpecificationPredicates(
            WebRequest request,
            Class<? extends AbstractEntity> entityClass,
            Root<? extends AbstractEntity> root,
            CriteriaBuilder criteriaBuilder) {
        String containsLikePattern;
        List<Predicate> predicates = new ArrayList<>();
        for (Field field : ClassLoaderUtil.getAllFieldsByEntity(entityClass)) {
            if (Modifier.isPrivate(field.getModifiers())) {
                String parameter = request.getParameter(field.getName());
                if (Objects.nonNull(parameter)) {
                    if (field.getType().isPrimitive()) {
                        if (field.getType().isAssignableFrom(boolean.class)) {
                            predicates.add(criteriaBuilder.equal(root.get(field.getName()), Boolean.parseBoolean(parameter)));
                        }
                    } else {
                        if (field.getType().isAssignableFrom(String.class)) {
                            containsLikePattern = StringPatternUtil.getContainsLikePattern(parameter);
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field.getName())), containsLikePattern));
                        }
                        if (Enum.class.isAssignableFrom(field.getType())) {
                            predicates.add(criteriaBuilder.equal(root.get(field.getName()), Enum.valueOf((Class<Enum>) field.getType(), parameter)));
                        }
                        if (isInnerEntityField(field.getType())) {
                            List<Predicate> innerEntitiesPredicates = new ArrayList<>();
                            for (Field innerField : ClassLoaderUtil.getAllFieldsByEntity(field.getType())) {
                                if (Modifier.isPrivate(innerField.getModifiers())) {
                                    if (innerField.getType().isAssignableFrom(String.class)) {
                                        containsLikePattern = StringPatternUtil.getContainsLikePattern(parameter);
                                        innerEntitiesPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field.getName()).get(innerField.getName())), containsLikePattern));
                                    }
                                }
                            }
                            if (CollectionUtils.isNotEmpty(innerEntitiesPredicates)) {
                                predicates.add(criteriaBuilder.or(innerEntitiesPredicates.toArray(new Predicate[0])));
                            }
                        }
                        if (field.getType().isAssignableFrom(Date.class)) {
                            if (StringPatternUtil.dateRegExPattern(parameter)) {
                                DateRangeModel range = WebRequestUtil.generateDateRange(parameter);
                                predicates.add(criteriaBuilder.between(root.get(field.getName()), range.getStart(), range.getEnd()));
                            }
                        }
                        if (Number.class.isAssignableFrom(field.getType())) {
                            predicates.add(criteriaBuilder.equal(root.get(field.getName()), parameter));
                        }
                    }
                }
            }
        }
        return predicates;
    }

    public List<Predicate> generateSpecificationPredicatesByOrganisation(
            Long orgId,
            Class<? extends AbstractEntity> entityClass,
            Root<? extends AbstractEntity> root,
            CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (Document.class.isAssignableFrom(entityClass) || Identifier.class.isAssignableFrom(entityClass) ||
                JournalDocument.class.isAssignableFrom(entityClass) ||
                JournalIdentifier.class.isAssignableFrom(entityClass) ||
                JournalOrganisation.class.isAssignableFrom(entityClass)) {
            for (Field field : ClassLoaderUtil.getAllFieldsByEntity(entityClass)) {
                if (Modifier.isPrivate(field.getModifiers())) {
                    if (!field.getType().isPrimitive()) {
                        if (isInnerEntityField(field.getType())) {
                            if (Organisation.class.isAssignableFrom(field.getType())) {
                                return getPredicateByOrganisation(orgId, field.getType(), field.getName(), root, criteriaBuilder, predicates);
                            }
                        }
                    }
                }
            }
        }
        return predicates;
    }

    public List<Predicate> generateSpecificationPredicatesByEntityId(
            Long entityId,
            Class<? extends AbstractEntity> entityClass,
            Root<? extends AbstractEntity> root,
            CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        for (Field field : ClassLoaderUtil.getAllFieldsByEntity(entityClass)) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (Objects.equals(field.getName(), "id")) {
                    predicates.add(criteriaBuilder.equal(root.get(field.getName()), entityId));
                }
            }
        }
        return predicates;
    }

    private boolean isInnerEntityField(Class<?> type) {
        return type.getSuperclass().isAssignableFrom(AbstractEntity.class)
                || type.getSuperclass().isAssignableFrom(AbstractCreateUpdateEntity.class)
                || type.getSuperclass().isAssignableFrom(AbstractCreateEntity.class);
    }

    private List<Predicate> getPredicateByOrganisation(Long orgId, Class<?> type, String organisation, Root<?> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        List<Predicate> innerEntitiesPredicates = new ArrayList<>();
        for (Field innerField : ClassLoaderUtil.getAllFieldsByEntity(type)) {
            if (Modifier.isPrivate(innerField.getModifiers())) {
                if (Objects.equals(innerField.getName(), "id")) {
                    innerEntitiesPredicates.add(criteriaBuilder.equal(root.get(organisation).get(innerField.getName()), orgId));
                }
            }
        }
        predicates.add(criteriaBuilder.and(innerEntitiesPredicates.toArray(new Predicate[0])));
        return predicates;
    }
}
