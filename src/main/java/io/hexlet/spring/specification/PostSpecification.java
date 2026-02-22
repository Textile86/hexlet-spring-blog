package io.hexlet.spring.specification;

import io.hexlet.spring.dto.PostParamsDTO;
import io.hexlet.spring.model.Post;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostSpecification {

    public Specification<Post> build(PostParamsDTO params) {
        return withTitleCont(params.getTitleCont())
                .and(withUserId(params.getUserId()))
                .and(withCreatedAtGt(params.getCreatedAtGt()))
                .and(withCreatedAtLt(params.getCreatedAtLt()));
    }

    private Specification<Post> withTitleCont(String titleCont) {
        return (root, query, cb) -> {
            if (titleCont == null || titleCont.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("title")), "%" + titleCont.toLowerCase() + "%");
        };
    }

    private Specification<Post> withUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    private Specification<Post> withCreatedAtGt(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            return cb.greaterThan(root.get("createdAt"), date);
        };
    }

    private Specification<Post> withCreatedAtLt(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            return cb.lessThan(root.get("createdAt"), date);
        };
    }
}