package dk.erst.delis.persistence;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.util.StringPatternUtil;
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.collections.CollectionUtils;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author funtusthan, created by 16.01.19
 */

public class AbstractSpecificationProcess<E extends AbstractEntity> implements AbstractSpecification<E> {

    @Override
    public Specification<E> generateCriteriaPredicate(WebRequest request, Class<E> entityClass) {
        return (Specification<E>) (root, criteriaQuery, criteriaBuilder) -> {

            String containsLikePattern;
            List<Predicate> predicates = new ArrayList<>();

            List<Field> fieldsFromEntityModel = new ArrayList<>();
            fieldsFromEntityModel.addAll(Arrays.asList(entityClass.getDeclaredFields()));
            fieldsFromEntityModel.addAll(Arrays.asList(entityClass.getSuperclass().getDeclaredFields()));

            for ( Field field : fieldsFromEntityModel ) {
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
                            if (field.getType().getSuperclass().isAssignableFrom(AbstractEntity.class)) {
                                List<Predicate> innerEntitiesPredicates = new ArrayList<>();
                                List<Field> innerEntitiesFields = new ArrayList<>(Arrays.asList(field.getType().getDeclaredFields()));
                                for (Field innerField : innerEntitiesFields) {
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
                                DateRangeModel range = WebRequestUtil.generateDateRange(parameter);
                                predicates.add(criteriaBuilder.between(root.get(field.getName()), range.getStart(), range.getEnd()));
                            }
                            if (Number.class.isAssignableFrom(field.getType())) {
                                predicates.add(criteriaBuilder.equal(root.get(field.getName()), parameter));
                            }
                        }
                    }
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
