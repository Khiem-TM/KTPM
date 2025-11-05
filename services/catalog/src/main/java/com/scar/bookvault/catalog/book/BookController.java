package com.scar.bookvault.catalog.book;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/v1/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> list() { return bookService.list(); }

    @GetMapping("/{id}")
    public Book get(@PathVariable Long id) { return bookService.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody @Valid Book book) { return bookService.create(book); }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @RequestBody @Valid Book book) { return bookService.update(id, book); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { bookService.delete(id); }
}


