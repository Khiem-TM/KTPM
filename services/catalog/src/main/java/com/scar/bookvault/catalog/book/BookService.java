package com.scar.bookvault.catalog.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> list() {
        return bookRepository.findAll();
    }

    public Book get(Long id) {
        return bookRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Book create(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("ISBN already exists");
        }
        book.setCreatedAt(java.time.OffsetDateTime.now());
        book.setUpdatedAt(java.time.OffsetDateTime.now());
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, Book incoming) {
        Book existing = get(id);
        existing.setTitle(incoming.getTitle());
        existing.setAuthor(incoming.getAuthor());
        existing.setIsbn(incoming.getIsbn());
        existing.setQuantity(incoming.getQuantity());
        existing.setUpdatedAt(java.time.OffsetDateTime.now());
        return bookRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}


