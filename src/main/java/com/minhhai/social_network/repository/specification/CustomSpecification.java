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
        // Tránh duplicate entity khi join one-to-many
        query.distinct(true);

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

    /**
     * Lấy path từ key. Hỗ trợ join lồng nhau (a.b.c).
     * Nếu join đã tồn tại thì tái sử dụng, không tạo join mới.
     */
    private Path<?> getPath(Root<T> root, String key) {
        if (key.contains(".")) {
            String[] parts = key.split("\\.");
            From<?, ?> join = root;

            for (int i = 0; i < parts.length - 1; i++) {
                String joinAttr = parts[i];

                // Kiểm tra join đã tồn tại chưa
                Join<?, ?> existingJoin = null;
                for (Join<?, ?> j : join.getJoins()) {
                    if (j.getAttribute().getName().equals(joinAttr)) {
                        existingJoin = j;
                        break;
                    }
                }

                if (existingJoin == null) {
                    join = join.join(joinAttr, JoinType.LEFT);
                } else {
                    join = existingJoin;
                }
            }

            return join.get(parts[parts.length - 1]);
        } else {
            return root.get(key);
        }
    }
}