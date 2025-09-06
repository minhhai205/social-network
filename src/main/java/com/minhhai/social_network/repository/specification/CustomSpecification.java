package com.minhhai.social_network.repository.specification;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

@Getter
@AllArgsConstructor
public class CustomSpecification<T> implements Specification<T> {

    private SpecSearchCriteria criteria;

    @Override
    public Predicate toPredicate(@NonNull final Root<T> root, final CriteriaQuery<?> query,
                                 @NonNull final CriteriaBuilder cb) {

        Path<?> path = getPath(root, criteria.getKey());

        return switch (criteria.getOperation()) {
            case EQUALITY -> cb.equal(path, criteria.getValue());
            case NEGATION -> cb.notEqual(path, criteria.getValue());
            case GREATER_THAN -> cb.greaterThan(path.as(String.class), criteria.getValue().toString());
            case LESS_THAN -> cb.lessThan(path.as(String.class), criteria.getValue().toString());
            case LIKE -> cb.like(path.as(String.class), "%" + criteria.getValue() + "%");
            case STARTS_WITH -> cb.like(path.as(String.class), criteria.getValue() + "%");
            case ENDS_WITH -> cb.like(path.as(String.class), "%" + criteria.getValue());
            default -> null;
        };
    }

    private Path<?> getPath(Root<T> root, String key) {
        if (key.contains(".")) {
            String[] parts = key.split("\\.");
            From<?, ?> join = root;
            for (int i = 0; i < parts.length - 1; i++) {
                join = join.join(parts[i], JoinType.LEFT);
            }
            return join.get(parts[parts.length - 1]);
        } else {
            return root.get(key);
        }
    }
}