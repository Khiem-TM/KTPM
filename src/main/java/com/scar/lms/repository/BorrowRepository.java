package com.scar.lms.repository;

import com.scar.lms.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer> {
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Borrow b WHERE b.user.id = :userId AND b.book.id = :bookId AND b.returnDate IS NULL")
    boolean existsByUserIdAndBookId(int userId, int bookId);

    @Query("SELECT b FROM Borrow b WHERE b.book.id = :bookId AND b.user.id = :userId AND b.returnDate IS NULL")
    Optional<Borrow> findByUserIdAndBookId(int userId, int bookId);

    List<Borrow> findAllByUserId(int userId);

    @Query("SELECT b FROM Borrow b WHERE FUNCTION('MONTH', b.borrowDate) = :month")
    List<Borrow> findAllByBorrowDateMonth(@Param("month") int month);

    @Query("SELECT COUNT(b) FROM Borrow b WHERE MONTH(b.borrowDate) = :month")
    Long countByBorrowDateMonth(@Param("month") int month);

    Long countByUserId(int userId);

    long count();
}
