package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Borrow;
import com.scar.lms.exception.OperationNotAllowedException;
import com.scar.lms.repository.BorrowRepository;
import com.scar.lms.service.BorrowService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;

    public BorrowServiceImpl(final BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public boolean isBookBorrowedBy(int userId, int bookId) {
//        boolean returned =
        return borrowRepository.existsByUserIdAndBookId(userId, bookId);
    }

    @Async
    @Override
    public void addBorrow(Borrow borrow) {
        if (borrowRepository.existsByUserIdAndBookId(borrow.getUser().getId(), borrow.getBook().getId())) {
            throw new OperationNotAllowedException("Book is already borrowed by the user.");
        }
        extractedAddBorrow(borrow);
    }

    private void extractedAddBorrow(Borrow borrow) {
        Book book = borrow.getBook();
        borrowRepository.save(borrow);
        book.setBorrowCount(book.getBorrowCount() + 1);
        borrow.getUser().setPoints(borrow.getUser().getPoints() + 1);
    }

    @Async
    @Override
    public void updateBorrow(Borrow borrow) {
        borrowRepository.save(borrow);
    }

    @Async
    @Override
    public void removeBorrow(int borrowID) {
        if (!borrowRepository.existsById(borrowID)) {
            throw new OperationNotAllowedException("Unable to remove book.");
        }
        Borrow borrow = borrowRepository.findById(borrowID).get();
        Book book = borrow.getBook();
        borrow.getUser().setPoints(borrow.getUser().getPoints() - 1);
        borrowRepository.delete(borrow);
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<Optional<Borrow>> findBorrow(int bookId, int userId) {
        return CompletableFuture.supplyAsync(() -> borrowRepository.findByUserIdAndBookId(bookId, userId));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Borrow>> findBorrowsOfUser(int userId) {
        return CompletableFuture.supplyAsync(() -> borrowRepository.findAllByUserId(userId));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Borrow>> findAllBorrows() {
        return CompletableFuture.supplyAsync(borrowRepository::findAll);
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Borrow>> findBorrowsByMonth(int month) {
        return CompletableFuture.supplyAsync(() -> borrowRepository.findAllByBorrowDateMonth(month));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<Long> countAllBorrows() {
        return CompletableFuture.supplyAsync(borrowRepository::count);
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<Long> countBorrowsByUser(int userId) {
        return CompletableFuture.supplyAsync(() -> borrowRepository.countByUserId(userId));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<Long> countBorrowsByMonth(int i) {
        return CompletableFuture.supplyAsync(() -> borrowRepository.countByBorrowDateMonth(i));
    }
}
