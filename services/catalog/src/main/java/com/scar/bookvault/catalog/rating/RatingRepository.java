package com.scar.bookvault.catalog.rating;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Page<Rating> findByBookId(Long bookId, Pageable pageable);
    long countByBookId(Long bookId);
    Double averagePointsByBookId(Long bookId);
}

