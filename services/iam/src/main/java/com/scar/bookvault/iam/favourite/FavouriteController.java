package com.scar.bookvault.iam.favourite;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/iam/v1/favourites")
public class FavouriteController {
    private final FavouriteRepository repository;

    public FavouriteController(FavouriteRepository repository) { this.repository = repository; }

    public record FavouriteRequest(@NotNull Long userId, @NotNull Long bookId) {}

    @GetMapping
    public Page<Favourite> list(@RequestParam Long userId,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return repository.findByUserId(userId, PageRequest.of(page, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Favourite create(@RequestBody FavouriteRequest req) {
        if (repository.existsByUserIdAndBookId(req.userId(), req.bookId())) {
            throw new IllegalArgumentException("Already favourited");
        }
        Favourite f = new Favourite();
        f.setUserId(req.userId());
        f.setBookId(req.bookId());
        return repository.save(f);
    }

    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam Long userId, @PathVariable Long bookId) {
        repository.deleteByUserIdAndBookId(userId, bookId);
    }
}

