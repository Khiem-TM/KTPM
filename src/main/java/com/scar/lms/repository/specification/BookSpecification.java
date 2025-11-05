package com.scar.lms.repository.specification;

import com.scar.lms.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> hasTitle(String title) {
        return (root, result, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Book> hasAuthor(String authorName) {
        return (root, result, criteriaBuilder) -> criteriaBuilder.like(root.get("author"), "%" + authorName + "%");
    }

    public static Specification<Book> hasGenre(String genreName) {
        return (root, result, criteriaBuilder) -> criteriaBuilder.like(root.get("genre"), "%" + genreName + "%");
    }

    public static Specification<Book> hasPublisher(String publisherName) {
        return (root, result, criteriaBuilder) -> criteriaBuilder.like(root.get("publisher"), "%" + publisherName + "%");
    }

    public static Specification<Book> hasYear(Integer year) {
        return (root, result, criteriaBuilder) -> criteriaBuilder.equal(root.get("publicationYear"), year);
    }
}