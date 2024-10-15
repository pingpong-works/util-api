package com.util.board;

import com.util.board.entity.Board;
import org.springframework.data.jpa.domain.Specification;

public class BoardSpecification {
    public static Specification<Board> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category.equals("all")) {
                return criteriaBuilder.conjunction();  // 'all'인 경우 필터링 없이 모든 데이터 반환
            }
            return criteriaBuilder.equal(root.get("category"), category);  // 카테고리로 필터링
        };
    }

    // 제목으로 필터링
    public static Specification<Board> hasTitleKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("title"), "%" + keyword + "%");  // 제목에 키워드가 포함된 경우
        };
    }

    // 내용으로 필터링
    public static Specification<Board> hasContentKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("content"), "%" + keyword + "%");  // 내용에 키워드가 포함된 경우
        };
    }

    // 제목 또는 내용으로 필터링
    public static Specification<Board> hasTitleOrContentKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("title"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("content"), "%" + keyword + "%")
            );  // 제목 또는 내용에 키워드가 포함된 경우
        };
    }

    // 작성자로 필터링
    public static Specification<Board> hasEmployeeName(String keyword) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("employeeName"), "%" + keyword + "%");  // 작성자 이름에 키워드가 포함된 경우
        };
    }
}
