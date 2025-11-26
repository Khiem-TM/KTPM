package com.scar.bookvault.catalog.rating;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog/v1/books/{bookId}")
public class RatingController {
    private final RatingRepository repository;

    public RatingController(RatingRepository repository) { this.repository = repository; }

    public record CreateRatingRequest(Long userId, Integer points, String comment) {}

    @GetMapping("/ratings")
    public Page<Rating> list(@PathVariable Long bookId,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        return repository.findByBookId(bookId, PageRequest.of(page, size));
    }

    @PostMapping("/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public Rating create(@PathVariable Long bookId, @RequestBody CreateRatingRequest req) {
        Rating r = new Rating();
        r.setBookId(bookId);
        r.setUserId(req.userId());
        r.setPoints(req.points());
        r.setComment(req.comment());
        return repository.save(r);
    }
}

